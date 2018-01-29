package com.caffinc.jelly;

public class RemoteResponse {
    private String response;
    private String exception;
    private String exceptionClassName;

    public RemoteResponse() {

    }

    public RemoteResponse(String response, String exception, String exceptionClassName) {
        this.response = response;
        this.exception = exception;
        this.exceptionClassName = exceptionClassName;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getExceptionClassName() {
        return exceptionClassName;
    }

    public void setExceptionClassName(String exceptionClassName) {
        this.exceptionClassName = exceptionClassName;
    }
}
