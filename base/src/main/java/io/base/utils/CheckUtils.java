package io.base.utils;

/**
 * Created by gaochao on 2016/2/18.
 */
public class CheckUtils {
    private CheckUtils(){ throw new AssertionError("No instance!"); }

    public static void checkState(boolean check, String message) {
        if (check) {
            throw new IllegalStateException(message);
        }
    }

    public static void checkNull(Object object, String message) {
        if (object == null) {
            throw new IllegalStateException(message);
        }
    }
}
