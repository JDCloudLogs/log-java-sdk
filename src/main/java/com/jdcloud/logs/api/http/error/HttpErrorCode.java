package com.jdcloud.logs.api.http.error;

/**
 * Http Error Code
 *
 * @author liubai
 * @date 2022/7/11
 */
public class HttpErrorCode {

    public static String UNKNOWN = "Unknown";

    /**
     * Unknown host. This error is returned when a
     * {@link java.net.UnknownHostException} is thrown.
     */
    public static String UNKNOWN_HOST = "UnknownHost";

    /**
     * Connection timeout. This error is returned when a
     * {@link org.apache.http.conn.ConnectTimeoutException} is thrown.
     */
    public static  String CONNECTION_TIMEOUT = "ConnectionTimeout";

    /**
     * Socket timeout. This error is returned when a
     * {@link java.net.SocketTimeoutException} is thrown.
     */
    public static  String SOCKET_TIMEOUT = "SocketTimeout";

    /**
     * Socket exception. This error is returned when a
     * {@link java.net.SocketException} is thrown.
     */
    public static  String SOCKET_EXCEPTION = "SocketException";

    /**
     * Non repeatable request. This error is returned when a
     * {@link org.apache.http.NoHttpResponseException} is thrown.
     */
    public static  String NON_REPEATABLE_REQUEST = "NonRepeatableRequest";

    /**
     * Http response exception. This error is returned when a
     * {@link com.google.api.client.http.HttpResponseException} is thrown.
     */
    public static  String HTTP_RESPONSE_EXCEPTION = "HttpResponseException";
}
