package com.caffinc.jelly;

import java.util.List;

public class RemoteCall {
    private String className;
    private String methodName;
    private List<String> args;

    public RemoteCall(String className, String methodName, List<String> args) {
        this.className = className;
        this.methodName = methodName;
        this.args = args;
    }

    public RemoteCall() {
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<String> getArgs() {
        return args;
    }

    public String toString() {
        return "RemoteCall[" + this.className + "." + this.methodName + "]";
    }
}