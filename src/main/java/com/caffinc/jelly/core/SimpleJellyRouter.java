package com.caffinc.jelly.core;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleJellyRouter implements JellyRouter {
    private Map<String, List<String>> hosts;
    private SecureRandom random;

    public SimpleJellyRouter(String versionId, List<String> hostList) {
        this.random = new SecureRandom();
        this.hosts = new HashMap<>();
        this.hosts.put(versionId, hostList);
    }

    @Override
    public void addSlice(SliceInfo sliceInfo) {
        if (!hosts.containsKey(sliceInfo.getVersionId())) {
            hosts.put(sliceInfo.getVersionId(), new ArrayList<>());
        }
        hosts.get(sliceInfo.getVersionId()).add(sliceInfo.getHostName());
    }

    @Override
    public String getSlice(String versionId, String className, String methodSignature) {
        return hosts.get(versionId).get(random.nextInt(hosts.get(versionId).size()));
    }
}
