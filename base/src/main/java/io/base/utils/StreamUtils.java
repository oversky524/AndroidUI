package io.base.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import io.base.exceptions.ExceptionUtils;

/**
 * Created by gaochao on 2015/7/21.
 * 与IO流相关的utility方法
 */
public class StreamUtils {

    /**
     * 从流中读取，并返回字符串
     * */
    public static String convertStreamToString(InputStream inputStream) {
        if(inputStream == null) return "";

        if(!(inputStream instanceof BufferedInputStream)) inputStream = new BufferedInputStream(inputStream);
        try {
            StringBuffer sb = new StringBuffer();
            char[] buffer = new char[1024];
            Reader reader = new BufferedReader(new InputStreamReader(inputStream));
            int len;
            while ((len = reader.read(buffer, 0, buffer.length)) != -1){
                sb.append(buffer, 0, len);
            }
            return sb.toString();
        } catch (IOException e) {
            ExceptionUtils.printExceptionStack(e);
        }

        return "";
    }
}
