package com.caffinc.jelly.core;

public class SliceInfo {
    private String hostName;
    private String versionId;

    public SliceInfo(String hostName, String versionId) {
        this.hostName = hostName;
        this.versionId = versionId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }
}
