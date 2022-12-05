# JDCloud log service java sdk
log-api 是对日志服务api封装的SDK
### 注意
为了提高效率和吞吐量，请尽量不要直接使用该SDK往日志服务中写数据，写数据请使用 log-producer SDK

### sample 1 : 构建logClient
```

ClientConfig clientConfig = new ClientConfig("accessKeyId", "secretAccessKey", "regionId", "endpoint");
LogClient logClient = new LogClient(clientConfig);

```

### sample 2 : 写数据
```

for (int i = 0; i < 10; i++) {
    List<LogItem> logItems = new ArrayList<LogItem>();
    LogItem logItem = new LogItem();
    logItem.setTime(System.nanoTime());
    logItem.addContent("level", "INFO");
    logItem.addContent("thread", "main");
    logItem.addContent("location", "DiscoveryClient");
    logItem.addContent("message", "Test message " + i + "0");
    logItems.add(logItem);

    LogItem logItem2 = new LogItem();
    logItem2.setTime(System.nanoTime());
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

```

## Maven配置
```
<dependency>
    <groupId>com.jdcloud.logs</groupId>
    <artifactId>log-api</artifactId>
    <version>0.2.0</version>
</dependency>
```
