package com.jdcloud.logs.api.config;

import com.jdcloud.logs.api.util.NetworkUtils;

/**
 * client config
 *
 * @author liubai
 * @date 2022/7/4
 */
public class ClientConfig {
    private final String accessKeyId;
    private final String secretAccessKey;
    private final String regionId;
    private final String endpoint;
    private String sourceIp;
    private int connectionTimeout = 5000;
    private int socketTimeout = 10000;

    public ClientConfig(String accessKeyId, String secretAccessKey, String regionId, String endpoint) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.regionId = regionId;
        this.endpoint = endpoint;
        this.sourceIp = NetworkUtils.getLocalMachineIp();
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public String getRegionId() {
        return regionId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
        if (sourceIp == null || sourceIp.isEmpty()) {
            this.sourceIp = NetworkUtils.getLocalMachineIp();
        }
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }
}
