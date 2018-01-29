package com.caffinc.jelly;

import com.google.gson.Gson;
import javassist.util.proxy.ProxyFactory;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jelly {
    private static final Logger LOG = LoggerFactory.getLogger(Jelly.class);

    private interface JellyClient {
        @POST("jelly")
        Call<RemoteResponse> call(@Body RemoteCall call);
    }

    public RemoteResponse remoteCall(RemoteCall call) {
        try {
            if (handlerMap.containsKey(call.getClassName())) {
                Object handler = handlerMap.get(call.getClassName());
                Method method = methodMap.get(call.getMethodName());
                Object[] args = new Object[call.getArgs().size()];
                for (int index = 0; index < method.getParameterCount(); index++) {
                    String arg = call.getArgs().get(index);
                    args[index] = gson.fromJson(arg, method.getParameterTypes()[index]);
                }
                try {
                    return new RemoteResponse(gson.toJson(method.invoke(handler, args)), null, null);
                } catch (InvocationTargetException e) {
                    LOG.error("Exception throw by underlying object", e);
                    Throwable cause = e.getCause();
                    return new RemoteResponse(null, gson.toJson(cause), cause.getClass().getName());
                }
            } else {
                return new RemoteResponse(null, gson.toJson(new IllegalArgumentException("Unable to find handler for " + call)), IllegalAccessException.class.getName());
            }
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Some very bad mojo here", e);
        }
    }

    private static Jelly jelly = new Jelly();

    private List<String> remoteSlices;
    private JellyClient client;
    private Map<String, Object> handlerMap;
    private Map<String, Method> methodMap;
    private HostSelectionInterceptor hostSelectionInterceptor;
    private Gson gson;

    private Jelly() {
        this.remoteSlices = new ArrayList<>();
        this.handlerMap = new HashMap<>();
        this.methodMap = new HashMap<>();
        this.remoteSlices.add("http://localhost:53559/");
        this.gson = new Gson();
        this.hostSelectionInterceptor = new HostSelectionInterceptor();
        this.client = getClient(this.hostSelectionInterceptor);
    }

    static final class HostSelectionInterceptor implements Interceptor {
        private volatile String host;

        public void setHost(String host) {
            this.host = host;
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String host = this.host;
            if (host != null) {
                HttpUrl newUrl = request.url().newBuilder()
                        .host(host)
                        .build();
                request = request.newBuilder()
                        .url(newUrl)
                        .build();
            }
            return chain.proceed(request);
        }
    }

    private JellyClient getClient(HostSelectionInterceptor hostSelectionInterceptor) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(hostSelectionInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:53559/")
                .callFactory(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(JellyClient.class);
    }

    public static Jelly getInstance() {
        return jelly;
    }

    private String getMethodSignature(Method method) {
        StringBuilder sb = new StringBuilder(method.getName());
        for (Class clazz : method.getParameterTypes()) {
            sb.append("_" + clazz.getName());
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
                Response<RemoteResponse> response = client.call(new RemoteCall(className, getMethodSignature(thisMethod), argList)).execute();
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

    public static <T> T build(T object) {
        return getInstance().getProxy(object);
    }
}
