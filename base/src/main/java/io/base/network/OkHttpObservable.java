package io.base.network;

import android.support.v4.util.ArrayMap;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

import io.base.defaultclass.OkHttpLogInterceptor;
import io.base.exceptions.ExceptionUtils;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by gaochao on 2016/2/17.
 */
public class OkHttpObservable {
    private static boolean sEnableLog = true;
    public static void enableLog(boolean enable){ sEnableLog = enable; }

    private static boolean mUnderTest = false;//是否添加LeakCanary内存泄露检查
    public static void setUnderTest(){
        mUnderTest = true;
    }

    public interface InitOkHttpClientListener{
        void init(OkHttpClient client);
    }

    public interface DealWithResponseListener{
        Object dealWith(Response response);
        void dealWith(Throwable error);
    }

    private OkHttpClient mClient = new OkHttpClient();
    private DealWithResponseListener mDealWithResponseListener;

    public OkHttpObservable(InitOkHttpClientListener initOkHttpClient, DealWithResponseListener dealWithResponse){
        if(sEnableLog){
            OkHttpLogInterceptor logging = new OkHttpLogInterceptor(mUnderTest ? new OkHttpLogInterceptor.SystemLogger()
                    : OkHttpLogInterceptor.Logger.DEFAULT);
            logging.setLevel(OkHttpLogInterceptor.Level.BODY);
            mClient.interceptors().add(logging);
        }
        if(initOkHttpClient != null) initOkHttpClient.init(mClient);
        mDealWithResponseListener = dealWithResponse;
    }

    public OkHttpObservable(){ this(null, null); }

    public OkHttpObservable(DealWithResponseListener dealWithResponse){ this(null, dealWithResponse); }

    public Observable<Object> getObservable(final Request request){
        return getObservable(request, null);
    }

    public Observable<Object> getObservable(final Request request, final DealWithResponseListener pListener){
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                final Call call = mClient.newCall(request);
                final DealWithResponseListener listener = (pListener !=null) ? pListener : mDealWithResponseListener;
                try {
                    Response response = call.execute();
                    subscriber.onNext(listener == null ? response : listener.dealWith(response));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    if(listener != null) listener.dealWith(e);
                    else subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<Object> post(String url, RequestBody requestBody, ArrayMap<String, String> headers,
                                   final DealWithResponseListener pListener){
        return getObservable(OkHttpUtils.build(url, HttpUtils.Method.POST, requestBody, headers), pListener);
    }

    public Observable<Object> post(String url, RequestBody requestBody, ArrayMap<String, String> headers){
        return getObservable(OkHttpUtils.build(url, HttpUtils.Method.POST, requestBody, headers));
    }

    public Observable<Object> put(String url, RequestBody requestBody, ArrayMap<String, String> headers,
                                  final DealWithResponseListener pListener){
        return getObservable(OkHttpUtils.build(url, HttpUtils.Method.POST, requestBody, headers), pListener);
    }

    public Observable<Object> put(String url, RequestBody requestBody, ArrayMap<String, String> headers){
        return getObservable(OkHttpUtils.build(url, HttpUtils.Method.POST, requestBody, headers));
    }

    public Observable<Object> get(String url, RequestBody requestBody, ArrayMap<String, String> headers){
        return getObservable(OkHttpUtils.build(url, HttpUtils.Method.POST, requestBody, headers));
    }
}
