package com.jdcloud.logs.api.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LogItem implements Serializable {

    private static final long serialVersionUID = -8985206344235175874L;

    public long time;
    public List<LogContent> contents = new ArrayList<LogContent>();

    public LogItem() {
        this.time = System.currentTimeMillis();
    }

    public LogItem(long time) {
        this.time = time;
    }

    public LogItem(long time, List<LogContent> contents) {
        this.time = time;
        setContents(contents);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void addContent(String key, String value) {
        addContent(new LogContent(key, value));
    }

    public void addContent(LogContent content) {
        contents.add(content);
    }

    public List<LogContent> getContents() {
        return contents;
    }

    public void setContents(List<LogContent> contents) {
        this.contents = new ArrayList<LogContent>(contents);
    }

}
