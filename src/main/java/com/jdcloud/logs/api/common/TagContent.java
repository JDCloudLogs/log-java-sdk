package com.jdcloud.logs.api.common;

import java.io.Serializable;

public class TagContent implements Serializable {

    private static final long serialVersionUID = 6111604603444928213L;

    public String key;
    public String value;

    public TagContent(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}
