package com.jdcloud.logs.api.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liubai
 */
public class Request implements Serializable {

    private static final long serialVersionUID = 2790817785476278329L;

    private Map<String, String> params = new HashMap<String, String>();

    public String getParam(String key) {
        if (params.containsKey(key)) {
            return params.get(key);
        } else {
            return "";
        }
    }

    public void setParam(String key, String value) {
        if (value == null) {
            params.put(key, "");
        } else {
            params.put(key, value);
        }
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getParams() {
        return params;
    }

}
