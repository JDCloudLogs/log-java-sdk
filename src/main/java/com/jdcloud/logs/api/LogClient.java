package com.jdcloud.logs.api;

import com.jdcloud.logs.api.common.*;
import com.jdcloud.logs.api.common.enums.CompressType;
import com.jdcloud.logs.api.config.ClientConfig;
import com.jdcloud.logs.api.exception.LogException;
import com.jdcloud.logs.api.http.HttpClient;
import com.jdcloud.logs.api.http.comm.HttpMethod;
import com.jdcloud.logs.api.http.comm.HttpRequestMessage;
import com.jdcloud.logs.api.request.PutLogsRequest;
import com.jdcloud.logs.api.response.PutLogsResponse;
import com.jdcloud.logs.api.util.GzipUtils;
import com.jdcloud.logs.api.util.LZ4Encoder;
import com.jdcloud.logs.api.util.StringUtils;
import com.jdcloud.logs.api.util.Validate;
import com.jdcloud.sdk.service.JdcloudResponse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * log client
 *
 * @author liubai
 * @date 2022/7/2
 */
public class LogClient implements LogApiService {

    private final ClientConfig clientConfig;
    private final HttpClient httpClient;

    public LogClient(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.httpClient = new HttpClient(clientConfig);
    }

    public LogClient(ClientConfig clientConfig, ThreadPoolExecutor executor) {
        this.clientConfig = clientConfig;
        this.httpClient = new HttpClient(clientConfig, executor);
    }

    @Override
    public PutLogsResponse putLogs(PutLogsRequest request) throws LogException {
        Validate.notNull(request, "request");
        String logTopic = request.getLogTopic();
        Validate.notBlank(logTopic, "logTopic");
        CompressType compressType = request.getCompressType();
        Validate.notNull(compressType, "compressType");

        byte[] logBytes = request.getLogGroupBytes();
        if (logBytes == null) {
            List<LogItem> logItems = request.getLogItems();
            if (logItems.size() > Constants.CONST_MAX_PUT_LINES) {
                throw new LogException("InvalidLogSize",
                        "logItems' length exceeds maximum limitation : " + Constants.CONST_MAX_PUT_LINES + " lines", "");
            }

            String source = request.getSource();
            Logs.LogGroup.Builder logs = Logs.LogGroup.newBuilder();
            if (source == null || source.isEmpty()) {
                logs.setSource(this.clientConfig.getSourceIp());
            } else {
                logs.setSource(source);
            }

            String fileName = request.getSource();
            if (StringUtils.isNotBlank(fileName)) {
                logs.setFileName(source);
            }

            ArrayList<TagContent> tags = request.getTags();
            if (tags != null && tags.size() > 0) {
                for (TagContent tag : tags) {
                    Logs.LogTag.Builder tagBuilder = logs.addLogTagsBuilder();
                    tagBuilder.setKey(tag.getKey());
                    tagBuilder.setValue(tag.getValue());
                }
            }

            for (LogItem item : logItems) {
                Logs.Log.Builder log = logs.addLogsBuilder();
                log.setTime(item.getTime());
                for (LogContent content : item.getContents()) {
                    Validate.notBlank(content.getKey(), "key");
                    Logs.Log.Content.Builder contentBuilder = log
                            .addContentsBuilder();
                    contentBuilder.setKey(content.getKey());
                    if (content.getValue() == null) {
                        contentBuilder.setValue("");
                    } else {
                        contentBuilder.setValue(content.getValue());
                    }
                }
            }

            logBytes = Logs.LogGroupList.newBuilder().addLogGroupList(logs.build()).build().toByteArray();
        }
        if (logBytes.length > Constants.CONST_MAX_PUT_SIZE) {
            throw new LogException("InvalidLogSize", "logItems' size is " + logBytes.length
                    + " bytes, exceeds maximum limitation : " + Constants.CONST_MAX_PUT_SIZE + " bytes", "");
        }

        Map<String, String> headers = new HashMap<String, String>();
        long originalSize = logBytes.length;
        headers.put(Constants.HEADER_X_JDCLOUD_BODYRAWSIZE, String.valueOf(originalSize));

        if (compressType == CompressType.LZ4) {
            logBytes = LZ4Encoder.compressToLhLz4Chunk(logBytes.clone());
            headers.put(Constants.HEADER_X_JDCLOUD_COMPRESSTYPE, compressType.toString());
        } else if (compressType == CompressType.GZIP) {
            logBytes = GzipUtils.compress(logBytes);
            headers.put(Constants.HEADER_X_JDCLOUD_COMPRESSTYPE, compressType.toString());
        }

        String uri = "/logtopics/" + logTopic + ":push";

        int tries = 2;
        for (int i = 0; i < tries; i++) {
            try {
                JdcloudResponse jdcloudResponse = httpClient.execute(new HttpRequestMessage(clientConfig.getRegionId(),
                        HttpMethod.POST, uri, request.getParams(), headers, logBytes, clientConfig));
                return new PutLogsResponse(jdcloudResponse.getRequestId());
            } catch (LogException e) {
                String requestId = e.getRequestId();
                if (i == 1 || requestId != null && !requestId.isEmpty()) {
                    throw e;
                }
            } catch (UnsupportedEncodingException e1) {
                throw new LogException("ErrorRequest", e1.getMessage(), e1, "");
            }
        }
        return null;
    }

    public void shutdown() {
        httpClient.shutdown();
    }
}
