package com.jdcloud.logs.api.util;

/**
 * @author liubai
 */
public class Validate {

    public static <T> void notNull(final T object, final String name) {
        if (object == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }

    public static <T extends CharSequence> T notEmpty(final T chars, final String name) {
        if (chars == null) {
            throw new IllegalArgumentException(name + " is null");
        }
        if (chars.length() == 0) {
            throw new IllegalArgumentException(name + " is empty");
        }
        return chars;
    }

    public static <T extends CharSequence> void notBlank(final T chars, final String name) {
        if (chars == null) {
            throw new IllegalArgumentException(name + " is null");
        }
        if (StringUtils.isBlank(chars)) {
            throw new IllegalArgumentException(name + " is blank");
        }
    }

}
