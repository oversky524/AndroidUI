package io.base.network;

import org.json.JSONException;
import org.json.JSONObject;

import io.base.utils.CheckUtils;

/**
 * Created by gaochao on 2016/2/17.
 */
public class HttpUtils {
    private HttpUtils(){ throw new AssertionError("No instances"); }

    public interface Method{
        String POST = "POST";
        String PATCH = "PATCH";
        String PUT = "PUT";
        String DELETE = "DELETE";
        String GET = "GET";
        String OPTIONS = "OPTIONS";
        String HEAD = "HEAD";
        String TRACE = "TRACE";
        String CONNECT = "CONNECT";
    }

    public interface ContentType{
        String JSON = "application/json";
        String MULTIPART = "multipart/mixed";
        String FORM_URLENCODED = "application/x-www-form-urlencoded";
    }

    public static String getJsonBody(Object ... keyValuePairs){
        final int len = keyValuePairs.length;
        CheckUtils.checkState((len & 1) == 1, "Key and Value have to be paired");

        JSONObject jsonObject = new JSONObject();
        try {
            for(int i=0; i<len; i+=2){
                Object key = keyValuePairs[i];
                if(key == null) continue;
                jsonObject.put(key.toString(), keyValuePairs[i + 1]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
