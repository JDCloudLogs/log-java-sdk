package com.jdcloud.logs.api.http.comm;

import com.jdcloud.logs.api.common.Constants;
import com.jdcloud.logs.api.config.ClientConfig;
import com.jdcloud.sdk.service.JdcloudRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * http request
 *
 * @author liubai
 * @date 2022/7/4
 */
public class HttpRequestMessage extends JdcloudRequest {

    private static final String CHART_SET = "UTF-8";

    private final HttpMethod method;
    private final String uri;
    private final Map<String, String> parameters;
    private final Map<String, String> headers;
    private final byte[] body;
    private final String url;

    public HttpRequestMessage(String regionId, HttpMethod method, String uri, Map<String, String> parameters,
                              Map<String, String> headers, byte[] body, ClientConfig config) throws UnsupportedEncodingException {
        super.setRegionId(regionId);
        this.method = method;
        this.uri = "/" + Constants.VERSION + uri;
        this.parameters = parameters;
        this.headers = headers;

        String paramString = parameterToString(parameters);
        String url = config.getEndpoint() + this.uri;
        if (paramString != null) {
            boolean requestHasNoPayload = body.length == 0;
            boolean requestIsPost = method == HttpMethod.POST;
            if (requestIsPost && requestHasNoPayload) {
                try {
                    body = paramString.getBytes(CHART_SET);
                } catch (UnsupportedEncodingException e) {
                    throw new AssertionError("EncodingFailed" + e.getMessage());
                }
            } else {
                url += "?" + paramString;
            }
        }
        this.url = url;
        this.body = body;
    }

    private String parameterToString(Map<String, String> params) throws UnsupportedEncodingException {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder paramString = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> p : params.entrySet()) {
            String key = p.getKey();
            String value = p.getValue();

            if (!first) {
                paramString.append("&");
            }

            // Urlencode each request parameter
            paramString.append(URLEncoder.encode(key, CHART_SET));
            if (value != null) {
                paramString.append("=").append(URLEncoder.encode(value, CHART_SET));
            }

            first = false;
        }

        return paramString.toString();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public String getUrl() {
        return url;
    }
}
