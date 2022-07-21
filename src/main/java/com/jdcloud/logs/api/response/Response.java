package com.jdcloud.logs.api.response;

import java.io.Serializable;

public class Response implements Serializable {

	private static final long serialVersionUID = 6539784261319796336L;

	private final String requestId;

	public Response(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestId() {
		return requestId;
	}
}
