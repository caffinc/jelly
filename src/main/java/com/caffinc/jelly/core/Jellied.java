package com.caffinc.jelly.core;

public interface Jellied {
    @SuppressWarnings("unchecked")
    default <T> T getJelliedObject() {
        return (T) Jelly.build(this);
    }
}
