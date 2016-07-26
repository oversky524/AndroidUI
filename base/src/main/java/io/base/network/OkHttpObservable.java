package io.base.network;

import android.support.v4.util.ArrayMap;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

import io.base.BaseApplication;
import io.base.defaultclass.OkHttpLogInterceptor;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by gaochao on 2016/2/17.
 */
public class OkHttpObservable {
    private static boolean sEnableLog = false;
    public static void enableLog(boolean enable){ sEnableLog = enable; }

    public interface InitOkHttpClientListener{
        void init(OkHttpClient client);
    }

    public interface DealWithResponseListener{
        Object dealWith(Response response) throws IOException;
    }

    private OkHttpClient mClient = new OkHttpClient();
    private DealWithResponseListener mDealWithResponseListener;
    private Scheduler mSubscribeOnScheduler = Schedulers.io();

    public OkHttpObservable(InitOkHttpClientListener initOkHttpClient, DealWithResponseListener dealWithResponse){
        if(sEnableLog){
            OkHttpLogInterceptor logging = new OkHttpLogInterceptor(BaseApplication.underTest() ? new OkHttpLogInterceptor.SystemLogger()
                    : OkHttpLogInterceptor.Logger.DEFAULT);
            logging.setLevel(OkHttpLogInterceptor.Level.BODY);
            mClient.interceptors().add(logging);
        }
        if(initOkHttpClient != null) initOkHttpClient.init(mClient);
        mDealWithResponseListener = dealWithResponse;
    }

    public void init(InitOkHttpClientListener initOkHttpClient){
        initOkHttpClient.init(mClient);
    }

    public OkHttpObservable(){ this(null, null); }

    public void setSubscribeOnScheduler(Scheduler scheduler){ mSubscribeOnScheduler = scheduler; }

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
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(mSubscribeOnScheduler);
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
