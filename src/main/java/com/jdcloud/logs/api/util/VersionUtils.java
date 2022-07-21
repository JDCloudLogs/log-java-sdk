package com.jdcloud.logs.api.util;

/**
 * @author liubai
 */
public class VersionUtils {

    public static final String VERSION;

    static {
        Package pkg = VersionUtils.class.getPackage();
        String version = StringUtils.trim(pkg.getImplementationVersion());
        VERSION = StringUtils.defaultIfBlank(version, "0.1.0");
    }

}
