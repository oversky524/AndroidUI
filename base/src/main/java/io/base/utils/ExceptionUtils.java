package io.base.utils;

/**
 * Created by gaochao on 2015/11/2.
 */
public class ExceptionUtils {
    private ExceptionUtils(){}

    public static void printExceptionStack(Throwable throwable){
        throwable.printStackTrace();
//        FIR.sendCrashManually(throwable);
    }
}
