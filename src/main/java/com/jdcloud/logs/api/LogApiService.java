package com.jdcloud.logs.api;

import com.jdcloud.logs.api.exception.LogException;
import com.jdcloud.logs.api.request.PutLogsRequest;
import com.jdcloud.logs.api.response.PutLogsResponse;

/**
 * log api service
 *
 * @author liubai
 * @date 2022/7/4
 */
public interface LogApiService {
    PutLogsResponse putLogs(PutLogsRequest request) throws LogException;
}
