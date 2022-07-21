package com.jdcloud.logs.api.common;

public class Constants {

    public static final String SERVICE_NAME = "logs";
    public static final String VERSION = "v1";
    public static final String CONST_LOCAL_IP = "127.0.0.1";

    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public final static String HEADER_X_JDCLOUD_BODYRAWSIZE = "x-log-bodyrawsize";
    public static final String HEADER_X_JDCLOUD_COMPRESSTYPE = "x-log-compresstype";
    public static final String HEADER_X_JDCLOUD_REQUEST_ID = "x-jdcloud-request-id";

    public static final String HEADER_VALUE_PROTO_BUF = "application/x-protobuf";

    public static int CONST_MAX_PUT_LINES = 32768;
    public static int CONST_MAX_PUT_SIZE = 8 * 1024 * 1024;

    public static final int CONST_HTTP_OK = 200;
}
