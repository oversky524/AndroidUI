package io.base.network;

import android.support.v4.util.ArrayMap;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import io.base.utils.CheckUtils;

/**
 * Created by gaochao on 2016/2/17.
 */
public class OkHttpUtils {
    private OkHttpUtils(){ throw new AssertionError("No instances"); }

    public static Request build(String url, String method, RequestBody requestBody, ArrayMap<String, String> headers){
        Request.Builder builder = new Request.Builder()
                .url(url).method(method, requestBody);
        if(headers != null) {
            for (String key : headers.keySet()) {
                builder.addHeader(key, headers.get(key));
            }
        }
        return builder.build();
    }

    public static ArrayMap<String, String> buildHeaders(String ... keyValuePairs){
        final int len = keyValuePairs.length;
        CheckUtils.checkState((len & 1) == 1, "Key and Value have to be paired");
        ArrayMap<String, String> headers = new ArrayMap<>();
        for(int i=0; i<len; i+=2){
            headers.put(keyValuePairs[i], keyValuePairs[i+1]);
        }
        return headers;
    }

    public static void buildHeaders(ArrayMap<String, String> headers, String ... keyValuePairs){
        final int len = keyValuePairs.length;
        CheckUtils.checkState((len & 1) == 1, "Key and Value have to be paired");
        for(int i=0; i<len; i+=2){
            headers.put(keyValuePairs[i], keyValuePairs[i+1]);
        }
    }

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse(HttpUtils.ContentType.JSON);

    public static RequestBody buildFormUrlEncodedBody(String ... keyValuePairs){
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for(int i=0; i<keyValuePairs.length; i+=2){
            String key = keyValuePairs[i];
            if(key == null) continue;
            builder.add(key, keyValuePairs[i+1]);
        }
        return builder.build();
    }
}
