package com.test.onepluswatermark.utils;

/**
 * Created by zhaolin on 17-6-11.
 */

public class SafeUtils {

    public static <T> T checkNotNull(T t) {
        if (t == null) {
            throw new RuntimeException("The parameter must not be null");
        }
        return t;
    }
}
