package com.jdcloud.logs.api.common;

import java.io.Serializable;

public class LogContent implements Serializable {

	private static final long serialVersionUID = 3027246314882522800L;

	public String key;
	public String value;

	public LogContent() {
	}

	public LogContent(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
