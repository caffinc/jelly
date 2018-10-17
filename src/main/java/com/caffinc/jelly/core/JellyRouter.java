package com.caffinc.jelly.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface JellyRouter {
    void addSlice(@Nonnull SliceInfo sliceInfo);

    @CheckForNull
    String getSlice(String versionId, String className, String methodSignature);
}
