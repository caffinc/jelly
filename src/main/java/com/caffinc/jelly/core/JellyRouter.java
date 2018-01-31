package com.caffinc.jelly.core;

public interface JellyRouter {
    void addSlice(SliceInfo sliceInfo);

    String getSlice(String versionId, String className, String methodSignature);
}
