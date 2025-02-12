package com.lizhe.bhrpccommon.utils;

import java.util.stream.IntStream;

/**
 * SerializationUtils
 * {@code @description} 网络通信序列化工具类
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 下午4:26
 * @version 1.0
 */
public class SerializationUtils {
    private static final String PADDING_STRING = "0";

    /**
     * 约定序列化类型最大长度为16
     */
    public static final int MAX_SERIALIZATION_TYPE_COUNR = 16;

    /**
     * 为长度不足16的字符串后面补0
     * @param str 原始字符串
     * @return 补0后的字符串
     */
    public static String paddingString(String str) {
        String string = transNullToEmpty(str);
        if (string.length() >= MAX_SERIALIZATION_TYPE_COUNR) return string;
        int paddingCount = MAX_SERIALIZATION_TYPE_COUNR - string.length();
        StringBuilder paddingString = new StringBuilder(str);
        // 使用stream流进行循环添加
        IntStream.range(0, paddingCount).forEach(i -> paddingString.append(PADDING_STRING));
        return paddingString.toString();
    }

    public static String transNullToEmpty(String str) {
        return str == null ? "" : str;
    }
}
