package com.jdcloud.logs.api.util;

/**
 * User agent
 *
 * @author liubai
 * @date 2022/7/5
 */
public class UserAgentUtils {
    private static String USER_AGENT = null;
    private static final String USER_AGENT_PREFIX = "jdcloud-log-sdk-java";

    public static String getUserAgent() {
        if (USER_AGENT == null) {
            USER_AGENT = USER_AGENT_PREFIX + "-" + VersionUtils.VERSION + "/" + System.getProperty("java.version");
        }
        return USER_AGENT;
    }
}
