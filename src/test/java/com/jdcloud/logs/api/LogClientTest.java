package com.jdcloud.logs.api;

import com.jdcloud.logs.api.common.LogItem;
import com.jdcloud.logs.api.config.ClientConfig;
import com.jdcloud.logs.api.exception.LogException;
import com.jdcloud.logs.api.request.PutLogsRequest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 日志客户端测试类
 *
 * @author liubai
 * @date 2022/7/11
 */
public class LogClientTest {

    private final static ClientConfig CLIENT_CONFIG = new ClientConfig(System.getProperty("accessKeyId"),
            System.getProperty("secretAccessKey"), System.getProperty("regionId"), System.getProperty("endpoint"));
    private final static LogClient LOG_CLIENT = new LogClient(CLIENT_CONFIG);

    @Test
    public void putLogsSmokeTest() {
        List<LogItem> logItems = new ArrayList<LogItem>();
        LogItem logItem = new LogItem();
        logItem.setTime(System.currentTimeMillis());
        logItem.addContent("level", "INFO");
        logItem.addContent("thread", "main");
        logItem.addContent("location", "DiscoveryClient");
        logItem.addContent("message", "Getting all instance registry info from the eureka server");
        logItems.add(logItem);
        PutLogsRequest request = new PutLogsRequest(System.getProperty("logTopic"), logItems);
        try {
            LOG_CLIENT.putLogs(request);
        } catch (LogException e) {
            System.err.println(e.toString());
            System.err.println("error message:" + e.getMessage());
        }
    }

    @Test
    public void putLogsBatch() throws LogException {
        for (int i = 0; i < 10; i++) {
            List<LogItem> logItems = new ArrayList<LogItem>();
            LogItem logItem = new LogItem();
            logItem.setTime(System.currentTimeMillis());
            logItem.addContent("level", "INFO");
            logItem.addContent("thread", "main");
            logItem.addContent("location", "DiscoveryClient");
            logItem.addContent("message", "Test message " + i + "0");
            logItems.add(logItem);

            LogItem logItem2 = new LogItem();
            logItem2.setTime(System.currentTimeMillis());
            logItem2.addContent("level", "INFO");
            logItem2.addContent("thread", "main");
            logItem2.addContent("location", "DiscoveryClient");
            logItem2.addContent("message", "Test message " + i + "1");
            logItems.add(logItem2);

            PutLogsRequest request = new PutLogsRequest(System.getProperty("logtopic"), logItems);

            try {
                LOG_CLIENT.putLogs(request);
            } catch (LogException e) {
                System.err.println(i + ": " + e.toString());
                System.err.println("error message:" + e.getMessage());
                throw e;
            }
        }
    }

    @Test
    public void putLogsWithThrowable() throws LogException {
        List<LogItem> logItems = new ArrayList<LogItem>();
        LogItem logItem = new LogItem();
        logItem.setTime(System.currentTimeMillis());
        logItem.addContent("level", "INFO");
        logItem.addContent("thread", "main");
        logItem.addContent("location", "DiscoveryClient");
        logItem.addContent("message", "Test message 0");
        logItem.addContent("throwable", "throwable");
        logItems.add(logItem);

        LogItem logItem2 = new LogItem();
        logItem2.setTime(System.currentTimeMillis());
        logItem2.addContent("level", "INFO");
        logItem2.addContent("thread", "main");
        logItem2.addContent("location", "DiscoveryClient");
        logItem2.addContent("message", "Test message 1");
        logItem2.addContent("throwable", "throwable");
        logItems.add(logItem2);

        PutLogsRequest request = new PutLogsRequest(System.getProperty("logtopic"), logItems);

        try {
            LOG_CLIENT.putLogs(request);
        } catch (LogException e) {
            System.err.println(e.toString());
            System.err.println("error message:" + e.getMessage());
            throw e;
        }
    }
}
