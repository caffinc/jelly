package com.caffinc.jelly;

public interface Jellied {
    default <T> T getJelliedObject() {
        return (T)Jelly.build(this);
    }
}
