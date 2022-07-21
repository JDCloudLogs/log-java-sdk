package com.jdcloud.logs.api.exception;

public class LogException extends Exception {

    private static final long serialVersionUID = -4441995860203577032L;

    private int httpCode = -1;

    private final String errorCode;

    private final String requestId;

    public LogException(String code, String message, String requestId) {
        super(message);
        this.errorCode = code;
        this.requestId = requestId;
    }

    public LogException(String code, String message, Throwable cause, String requestId) {
        super(message, cause);
        this.errorCode = code;
        this.requestId = requestId;
    }

    public LogException(int httpCode, String code, String message, String requestId) {
        super(message);
        this.httpCode = httpCode;
        this.errorCode = code;
        this.requestId = requestId;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    @Override
    public String toString() {
        return "LogException{" +
                "httpCode=" + httpCode +
                ", errorCode='" + errorCode + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
