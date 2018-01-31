package com.caffinc.jelly.core;

import com.caffinc.jelly.RemoteCall;
import com.caffinc.jelly.RemoteResponse;
import com.caffinc.jelly.annotations.Slice;
import com.caffinc.jelly.resources.JellyResource;
import com.caffinc.jetter.Api;
import com.google.gson.Gson;
import javassist.util.proxy.ProxyFactory;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jelly {
    private static final Logger LOG = LoggerFactory.getLogger(Jelly.class);

    private static Jelly jelly;
    private String versionId;
    private JellyRouter router;
    private Map<String, Object> handlerMap;
    private Map<String, Method> methodMap;
    private Map<String, JellyClient> clientMap;
    private List<String> hostList;
    private Gson gson;
    private Server server;

    public static void initialize(int port, String versionId, List<String> hostList) {
        if (jelly != null) {
            throw new IllegalStateException("Already initialized");
        }
        jelly = new Jelly(versionId, hostList);
        try {
            jelly.server = new Api(port).setBaseUrl("/").addServiceResource(JellyResource.class).enableCors().startNonBlocking();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to start Jelly Service", e);
        }
    }

    public static void unload() {
        try {
            jelly.server.stop();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to unload the server", e);
        }
    }

    private static Jelly getInstance() {
        if (jelly == null) {
            throw new IllegalStateException("Please call initialize to start the daemon");
        }
        return jelly;
    }

    private Jelly(String versionId, List<String> hostList) {
        this.versionId = versionId;
        this.hostList = hostList;
        this.handlerMap = new HashMap<>();
        this.methodMap = new HashMap<>();
        this.clientMap = new HashMap<>();
        this.router = new SimpleJellyRouter(versionId, hostList);
        this.gson = new Gson();
        for (String hostName : hostList) {
            this.clientMap.put(hostName, getClient(hostName));
        }
    }

    private String getMethodSignature(Method method) {
        StringBuilder sb = new StringBuilder(method.getName());
        for (Class clazz : method.getParameterTypes()) {
            sb.append("_");
            sb.append(clazz.getName());
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private <T> T getProxy(T object) {
        final String className = object.getClass().getName();
        handlerMap.put(className, object);
        try {
            ProxyFactory factory = new ProxyFactory();
            factory.setFilter((Method method) -> {
                if (method.getAnnotation(Slice.class) != null) {
                    methodMap.put(getMethodSignature(method), method);
                    return true;
                }
                return false;
            });
            factory.setSuperclass(object.getClass());
            return (T) factory.create(new Class<?>[0], new Object[0], (Object self, Method thisMethod, Method proceed, Object[] args) -> {
                List<String> argList = new ArrayList<>();
                for (Object o : args) {
                    argList.add(gson.toJson(o));
                }
                String methodSignature = getMethodSignature(thisMethod);
                JellyClient client = getCachedClient(className, methodSignature);
                Response<RemoteResponse> response = client.call(new RemoteCall(className, methodSignature, argList)).execute();
                RemoteResponse body = response.body();
                if (body != null) {
                    if (body.getException() != null) {
                        throw (Throwable) gson.fromJson(body.getException(), getClass().getClassLoader().loadClass(body.getExceptionClassName()));
                    }
                    return gson.fromJson(body.getResponse(), proceed.getReturnType());
                } else {
                    throw new IllegalStateException("Unable to make remote call");
                }
            });
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalArgumentException("Unable to proxy", e);
        }
    }

    private JellyClient getCachedClient(String className, String methodSignature) {
        String hostName = router.getSlice(versionId, className, methodSignature);
        if (!clientMap.containsKey(hostName)) {
            clientMap.put(hostName, getClient(hostName));
        }
        return clientMap.get(hostName);
    }

    public static <T> T build(T object) {
        return getInstance().getProxy(object);
    }

    public static void addSlice(SliceInfo sliceInfo) {
        getInstance().router.addSlice(sliceInfo);
    }

    private interface JellyClient {
        @POST("jelly")
        Call<RemoteResponse> call(@Body RemoteCall call);
    }

    private JellyClient getClient(String hostName) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(hostName)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(JellyClient.class);
    }

    public static RemoteResponse call(RemoteCall call) {
        return getInstance().internalCall(call);
    }

    private RemoteResponse internalCall(RemoteCall call) {
        try {
            if (handlerMap.containsKey(call.getClassName())) {
                Object handler = handlerMap.get(call.getClassName());
                Method method = methodMap.get(call.getMethodName());
                Object[] args = new Object[call.getArgs().size()];
                for (int index = 0; index < method.getParameterCount(); index++) {
                    String arg = call.getArgs().get(index);
                    args[index] = gson.fromJson(arg, method.getParameterTypes()[index]);
                }
                return invoke(method, handler, args);
            } else {
                return new RemoteResponse(null, gson.toJson(new IllegalArgumentException("Unable to find handler for " + call)), IllegalAccessException.class.getName());
            }
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Some very bad mojo here", e);
        }
    }

    private RemoteResponse invoke(Method method, Object handler, Object[] args) throws IllegalAccessException {
        try {
            return new RemoteResponse(gson.toJson(method.invoke(handler, args)), null, null);
        } catch (InvocationTargetException e) {
            LOG.error("Exception throw by underlying object", e);
            Throwable cause = e.getCause();
            return new RemoteResponse(null, gson.toJson(cause), cause.getClass().getName());
        }
    }
}
