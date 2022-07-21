package com.jdcloud.logs.api.common.enums;

public enum CompressType {
    /**
     * none
     */
    NONE(""),
    /**
     * lz4
     */
    LZ4("lz4"),
    /**
     * gzip
     */
    GZIP("deflate");

    private final String strValue;

    CompressType(String strValue) {
        this.strValue = strValue;
    }

    @Override
    public String toString() {
        return strValue;
    }

    public static CompressType fromString(final String compressType) {
        for (CompressType type : values()) {
            if (type.strValue.equals(compressType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("invalid CompressType: " + compressType + ", should be ("
                + CompressType.NONE + ", " + CompressType.GZIP + ", " + CompressType.LZ4 + ")");
    }
}
