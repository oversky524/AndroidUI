package io.base.network;

import android.net.Uri;
import android.support.v4.util.ArrayMap;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.internal.Util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.base.exceptions.ExceptionUtils;
import io.base.utils.AndroidUtils;
import io.base.utils.CheckUtils;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

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
    public static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");

    public static RequestBody buildFormUrlEncodedBody(String ... keyValuePairs){
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for(int i=0; i<keyValuePairs.length; i+=2){
            String key = keyValuePairs[i];
            if(key == null) continue;
            builder.add(key, keyValuePairs[i+1]);
        }
        return builder.build();
    }

    public static String getContent(RequestBody requestBody){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Buffer sink = new Buffer();
        String result = null;
        try {
            requestBody.writeTo(sink);
            sink.writeTo(baos);
            result = baos.toString();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param uri file uri
     * */
    public static RequestBody createBody(final MediaType type, final Uri uri){
        return new UriRequestBody(uri, type);
    }

    private static class UriRequestBody extends RequestBody{
        private Uri mUri;
        private int mLength = -1;
        private MediaType mType;

        UriRequestBody(Uri uri, MediaType type){
            mType = type;
            mUri = uri;
            InputStream inputStream = null;
            try {
                inputStream = AndroidUtils.getInputStream(uri);
                mLength = inputStream.available();
            } catch (FileNotFoundException e) {
                ExceptionUtils.printExceptionStack(e);
            } catch (IOException e) {
                ExceptionUtils.printExceptionStack(e);
            }finally {
                try {
                    if(null != inputStream) inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public MediaType contentType() { return mType; }

        @Override
        public long contentLength() throws IOException { return mLength; }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            Source source = null;
            try {
                InputStream inputStream = AndroidUtils.getInputStream(mUri);
                source = Okio.source(inputStream);
                sink.writeAll(source);
            } catch (FileNotFoundException e) {
                ExceptionUtils.printExceptionStack(e);
            } finally {
                Util.closeQuietly(source);
            }
        }
    }
}
