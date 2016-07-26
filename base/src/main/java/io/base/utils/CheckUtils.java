package io.base.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gaochao on 2016/2/18.
 */
public class CheckUtils {
    private CheckUtils() {
        throw new AssertionError("No instance!");
    }

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

    /**
     */
    public final static boolean isMobileNumber(String mobileNumber) {
        if (TextUtils.isEmpty(mobileNumber)) {
            return false;
        }
        boolean flag;
        try {
//            Pattern regex = Pattern.compile("^((1[0-9][0-9])\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
            Pattern regex = Pattern.compile("(^(13\\d|14[57]|15[^4,\\D]|17[678]|18\\d)\\d{8}|170[059]\\d{7})$");
            Matcher matcher = regex.matcher(mobileNumber);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     */
    public final static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }
}
