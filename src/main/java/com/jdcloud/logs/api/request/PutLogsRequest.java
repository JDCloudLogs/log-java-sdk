package com.jdcloud.logs.api.request;

import com.jdcloud.logs.api.common.LogItem;
import com.jdcloud.logs.api.common.TagContent;
import com.jdcloud.logs.api.common.enums.CompressType;
import com.jdcloud.logs.api.util.Validate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubai
 */
public class PutLogsRequest extends Request {

    private static final long serialVersionUID = 1410404331953976866L;

    private String logTopic;
    private ArrayList<LogItem> logItems;
    private ArrayList<TagContent> tags = null;
    private String source;
    private String fileName;
    private byte[] logGroupBytes = null;
    private CompressType compressType = CompressType.LZ4;

    public PutLogsRequest(String logTopic, List<LogItem> logItems, String source, String fileName) {
        Validate.notBlank(logTopic, "logTopic");
        Validate.notNull(logItems, "logItems");
        this.logTopic = logTopic;
        this.logItems = new ArrayList<LogItem>(logItems);
        this.source = source;
        this.fileName = fileName;
    }

    public PutLogsRequest(String logTopic, List<LogItem> logItems) {
        this(logTopic, logItems, null, null);
    }

    public PutLogsRequest(String logTopic, byte[] logGroupBytes, String source, String fileName) {
        Validate.notBlank(logTopic, "logTopic");
        Validate.notNull(logGroupBytes, "logGroupBytes");
        this.logTopic = logTopic;
        this.logGroupBytes = logGroupBytes;
        this.source = source;
        this.fileName = fileName;
    }

    public String getLogTopic() {
        return logTopic;
    }

    public void setLogTopic(String logTopic) {
        this.logTopic = logTopic;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ArrayList<LogItem> getLogItems() {
        return logItems;
    }

    public void setLogItems(ArrayList<LogItem> logItems) {
        this.logItems = logItems;
    }

    public ArrayList<TagContent> getTags() {
        return tags;
    }

    public void setTags(ArrayList<TagContent> tags) {
        this.tags = tags;
    }

    public CompressType getCompressType() {
        return compressType;
    }

    public void setCompressType(CompressType compressType) {
        this.compressType = compressType;
    }

    public byte[] getLogGroupBytes() {
        return logGroupBytes;
    }

    public void setLogGroupBytes(byte[] logGroupBytes) {
        this.logGroupBytes = logGroupBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
