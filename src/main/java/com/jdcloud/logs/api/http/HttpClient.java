package com.jdcloud.logs.api.http;

import com.google.api.client.http.*;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.jdcloud.logs.api.common.Constants;
import com.jdcloud.logs.api.config.ClientConfig;
import com.jdcloud.logs.api.exception.LogException;
import com.jdcloud.logs.api.http.comm.HttpRequestMessage;
import com.jdcloud.logs.api.http.error.HttpErrorCode;
import com.jdcloud.logs.api.util.UserAgentUtils;
import com.jdcloud.sdk.auth.Credentials;
import com.jdcloud.sdk.auth.sign.SignatureComposer;
import com.jdcloud.sdk.constant.ParameterConstant;
import com.jdcloud.sdk.model.SignRequest;
import com.jdcloud.sdk.service.JdcloudHttpResponse;
import com.jdcloud.sdk.service.JdcloudResponse;
import com.jdcloud.sdk.utils.BinaryUtils;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * http client
 *
 * @author liubai
 * @date 2022/7/4
 */
public class HttpClient {

    private final HttpTransport httpTransport;
    private final HttpRequestFactory httpRequestFactory;
    private final ClientConfig config;
    private ThreadPoolExecutor executor;

    public HttpClient(ClientConfig config) {
        this(config, null);
        int processors = Runtime.getRuntime().availableProcessors();
        this.executor = createThreadPool(processors, processors * 5, processors * 100);
    }

    public HttpClient(final ClientConfig config, ThreadPoolExecutor executor) {
        this.httpTransport = new ApacheHttpTransport();
        this.config = config;
        this.httpRequestFactory = this.httpTransport.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                if (config != null) {
                    if (config.getConnectionTimeout() != -1) {
                        request.setConnectTimeout(config.getConnectionTimeout());
                    }
                    if (config.getSocketTimeout() != -1) {
                        request.setReadTimeout(config.getSocketTimeout());
                    }
                }
            }
        });
        this.executor = executor;
    }

    private ThreadPoolExecutor createThreadPool(int corePoolSize, int maximumPoolSize, int queueSize) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    public JdcloudResponse execute(HttpRequestMessage httpMessage) throws LogException {
        long start = System.nanoTime();
        HttpResponse httpResponse = null;
        try {
            String contentType = getContentType(httpMessage.getHeaders());

            HttpRequest httpRequest = this.httpRequestFactory.buildRequest(httpMessage.getMethod().toString(),
                    new GenericUrl(httpMessage.getUrl()),
                    new ByteArrayContent(contentType, httpMessage.getBody()));
            setHeaders(httpRequest.getHeaders(), httpMessage.getHeaders());

            // 签名
            SignRequest signRequest = new SignRequest(config.getEndpoint(), httpMessage.getUri(),
                    httpMessage.getRegionId(), Constants.SERVICE_NAME, httpRequest,
                    new Credentials(config.getAccessKeyId(), config.getSecretAccessKey()));
            SignatureComposer signer = new SignatureComposer();
            signer.sign(signRequest);

            // 调试日志
//            System.out.println("ContentType: " + httpRequest.getHeaders().getContentType()
//                    + "\nAuthorization: " + httpRequest.getHeaders().getAuthorization()
//                    + "\nX-Jdcloud-Nonce: " + httpRequest.getHeaders().get(ParameterConstant.X_JDCLOUD_NONCE)
//                    + "\nX-Jdcloud-Date: " + httpRequest.getHeaders().get(ParameterConstant.X_JDCLOUD_DATE));

            HttpTask httpTask = new HttpTask(httpRequest);
            Future<HttpResponse> future = executor.submit(httpTask);
            httpResponse = future.get(this.config.getSocketTimeout(), TimeUnit.MILLISECONDS);
            int statusCode = httpResponse.getStatusCode();
            if (statusCode != Constants.CONST_HTTP_OK) {
                String requestId = getRequestId(httpResponse);
                throw new LogException(statusCode, "ErrorResponse", "Response is not 200", requestId);
            }
            JdcloudResponse response = handlerHttpResponse(httpResponse);

            // 调试日志
//            long end = System.nanoTime();
//            long cost = end - start;
//            long beforeCompressBytes = Long.parseLong((String) httpRequest.getHeaders().get(Constants.HEADER_X_JDCLOUD_BODYRAWSIZE));
//            long afterCompressBytes = httpMessage.getBody().length;
//            System.out.println("Http cost: " + cost + "ns, before compress: " + beforeCompressBytes
//                    + ", after compress: " + afterCompressBytes + ", compress ratio: "
//                    + beforeCompressBytes / afterCompressBytes);

            return response;
        } catch (ExecutionException e) {
            throw createNetworkException((IOException) e.getCause());
        } catch (Exception e) {
            if (e.getCause() instanceof HttpResponseException) {
                throw createHttpResponseException((HttpResponseException) e.getCause());
            }
            throw new LogException(HttpErrorCode.UNKNOWN, e.getMessage(), e, "");
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.disconnect();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static LogException createNetworkException(IOException ex) {
        String errorCode = HttpErrorCode.UNKNOWN;
        int httpCode = -1;

        if (ex instanceof SocketTimeoutException) {
            errorCode = HttpErrorCode.SOCKET_TIMEOUT;
        } else if (ex instanceof SocketException) {
            errorCode = HttpErrorCode.SOCKET_EXCEPTION;
        } else if (ex instanceof ConnectTimeoutException) {
            errorCode = HttpErrorCode.CONNECTION_TIMEOUT;
        } else if (ex instanceof UnknownHostException) {
            errorCode = HttpErrorCode.UNKNOWN_HOST;
        } else if (ex instanceof NoHttpResponseException) {
            errorCode = HttpErrorCode.CONNECTION_TIMEOUT;
        } else if (ex instanceof ClientProtocolException) {
            Throwable cause = ex.getCause();
            if (cause instanceof NonRepeatableRequestException) {
                errorCode = HttpErrorCode.NON_REPEATABLE_REQUEST;
                return new LogException(errorCode, cause.getMessage(), "");
            }
        } else if (ex instanceof HttpResponseException) {
            return createHttpResponseException((HttpResponseException) ex);
        }

        return new LogException(httpCode, errorCode, ex.getMessage(), "");
    }

    public static LogException createHttpResponseException(HttpResponseException ex) {
        return new LogException(ex.getStatusCode(), HttpErrorCode.HTTP_RESPONSE_EXCEPTION, ex.getMessage(), "");
    }

    private void setHeaders(HttpHeaders headers, Map<String, String> customHeaders) {
        headers.putAll(customHeaders);
        setCommonHeaders(headers);
        headers.setContentType(getContentType(customHeaders));
    }

    private void setCommonHeaders(HttpHeaders headers) {
        headers.setUserAgent(UserAgentUtils.getUserAgent());
    }

    private String getContentType(Map<String, String> customHeaders) {
        String contentType = Constants.HEADER_VALUE_PROTO_BUF;
        if (customHeaders.get(Constants.HEADER_CONTENT_TYPE) != null) {
            contentType = customHeaders.get(Constants.HEADER_CONTENT_TYPE);
        }
        return contentType;
    }

    protected JdcloudResponse handlerHttpResponse(HttpResponse httpResponse) throws IOException {
        JdcloudResponse response = new JdcloudResponse();
        String reqId = getRequestId(httpResponse);
        response.setRequestId(reqId);
        response.setJdcloudHttpResponse(copyHttpResponse(httpResponse));
        return response;
    }

    private JdcloudHttpResponse copyHttpResponse(HttpResponse httpResponse) throws IOException {
        JdcloudHttpResponse jdcloudHttpResponse = new JdcloudHttpResponse();
        byte[] content = null;
        if (httpResponse.getContent() != null && httpResponse.getStatusCode() != 204
                && httpResponse.getStatusCode() != 304) {
            content = BinaryUtils.toByteArray(httpResponse.getContent());
        }
        jdcloudHttpResponse.setContent(content);
        jdcloudHttpResponse.setContentCharset(httpResponse.getContentCharset());
        jdcloudHttpResponse.setContentEncoding(httpResponse.getContentEncoding());
        jdcloudHttpResponse.setContentType(httpResponse.getContentType());
        jdcloudHttpResponse.setHeaders(httpResponse.getHeaders());
        jdcloudHttpResponse.setMediaType(httpResponse.getMediaType());
        jdcloudHttpResponse.setContentLoggingLimit(httpResponse.getContentLoggingLimit());
        jdcloudHttpResponse.setStatusCode(httpResponse.getStatusCode());
        jdcloudHttpResponse.setStatusMessage(httpResponse.getStatusMessage());
        return jdcloudHttpResponse;
    }

    private String getRequestId(HttpResponse httpResponse) {
        String reqId = null;
        if (httpResponse.getHeaders() != null) {
            Object o = httpResponse.getHeaders().get(Constants.HEADER_X_JDCLOUD_REQUEST_ID);
            if (o instanceof ArrayList) {
                reqId = (String) ((ArrayList) o).get(0);
            }
        }
        return reqId;
    }

    public static class HttpTask implements Callable<HttpResponse> {
        private final HttpRequest httpRequest;

        public HttpTask(HttpRequest httpRequest) {
            this.httpRequest = httpRequest;
        }

        @Override
        public HttpResponse call() throws Exception {
            return httpRequest.execute();
        }
    }

    public void shutdown() {
        shutdown(60000);
    }

    public void shutdown(long timeoutMs) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(timeoutMs, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
                executor.awaitTermination(timeoutMs, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        System.out.println(TimeUnit.NANOSECONDS.toMillis(209424905));
    }
}
