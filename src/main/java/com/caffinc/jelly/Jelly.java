package com.caffinc.jelly;

import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Jelly {
    private interface JellyClient {
        Object call(Object[] args);
    }

    private Object remoteCall(Object[] args) {

    }

    private static Jelly jelly = new Jelly();

    private List<String> remoteSlices;
    private List<JellyClient> clients;

    private Jelly() {
        this.remoteSlices = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.remoteSlices.add("localhost");

        for (String host : remoteSlices) {
            // Do nothing
        }
    }

    private static Jelly getInstance() {
        return jelly;
    }

    @SuppressWarnings("unchecked")
    private <T> T getProxy(T object) {
        final Random random = new Random();
        try {
            ProxyFactory factory = new ProxyFactory();
            factory.setFilter((Method method) -> method.getAnnotation(Slice.class) != null);
            factory.setSuperclass(object.getClass());
            return (T) factory.create(new Class<?>[0], new Object[0], (Object self, Method thisMethod, Method proceed, Object[] args) -> {
//                if (random.nextBoolean()) {
//                    return proceed.invoke(self, args);
//                } else {
//                    if (clients.isEmpty()) {
                throw new UnsupportedOperationException("I'm sorry Dave, I'm afraid I can't do that");
//                    }
//                    return clients.get(random.nextInt(clients.size())).call(args);
//                }
            });
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalArgumentException("Unable to proxy", e);
        }
    }

    public static <T> T build(T object) {
        return getInstance().getProxy(object);
    }
}
