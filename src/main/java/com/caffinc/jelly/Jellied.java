package com.caffinc.jelly;

public interface Jellied {
    @SuppressWarnings("unchecked")
    default <T> T getJelliedObject() {
        return (T) Jelly.build(this);
    }

    default String getClassName() {
        return this.getClass().getName();
    }
}
