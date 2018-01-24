package com.caffinc.jelly;

import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Jelly {
    private static Jelly jelly = new Jelly();

    private List<String> remoteSlices;

    private Jelly() {
        this.remoteSlices = new ArrayList<>();
        remoteSlices.add("localhost");
    }

    private static Jelly getInstance() {
        return jelly;
    }

    @SuppressWarnings("unchecked")
    private <T> T getProxy(T object) {
        try {
            ProxyFactory factory = new ProxyFactory();
            factory.setSuperclass(object.getClass());
            return (T) factory.create(new Class<?>[0], new Object[0], (Object self, Method thisMethod, Method proceed, Object[] args) -> {
//                return proceed.invoke(self, args);
                throw new UnsupportedOperationException("I'm sorry Dave, I'm afraid I can't do that");
            });
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalArgumentException("Unable to proxy", e);
        }
    }

    public static <T> T build(T object) {
        return getInstance().getProxy(object);
    }
}
