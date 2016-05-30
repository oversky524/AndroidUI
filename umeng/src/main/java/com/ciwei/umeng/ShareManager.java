package com.ciwei.umeng;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.BaseShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
//import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * Created by gaochao on 2015/9/14.
 * 分享公用类
 */
public class ShareManager {
    private static final String WEIXIN_APPID = "wx22b04c082e5afea6";
    private static final String WEIXIN_APPSECRET = "d4624c36b6795d1d99dcf0547af5443d";
    private static final String QQ_APPID = "1104861254";
    private static final String QQ_APPSECRET = "s6rmM2BvH7OJePyc";
    private static final String WEIBO_REDIRECT_URI = "http://ciwei.io";
    UMSocialService mShareService = UMServiceFactory.getUMSocialService("com.umeng.share");
    private ShareManager(){}
    private static ShareManager sShareManager = new ShareManager();
    public static ShareManager getInstance(){
        return sShareManager;
    }

    private boolean mIsWeixinInited;
    //分享到微信
    public void shareToWeixin(Context context, String title, String sharedText, int sharedImageResId, Bitmap sharedBitmap,
                                     String targetUrl, SocializeListeners.SnsPostListener listener){
        if(!mIsWeixinInited){
            mIsWeixinInited = true;
            new UMWXHandler(context, WEIXIN_APPID, WEIXIN_APPSECRET).addToSocialSDK();
        }
        share(new WeiXinShareContent(), SHARE_MEDIA.WEIXIN, context, title, sharedText, sharedImageResId, sharedBitmap,
                targetUrl, listener);
    }
    public void shareToWeixinWithImageOnly(Context context, Bitmap sharedBitmap, DefaultSnsPostListener listener){
        shareToWeixin(context, null, null, 0, sharedBitmap, null, listener);
    }

    private boolean mIsWeixinCircleInited;
    //分享到微信朋友圈
    public void shareToWeixinCircle(Context context, String title, String sharedText, int sharedImageResId, Bitmap sharedBitmap,
                              String targetUrl, SocializeListeners.SnsPostListener listener){
        if(!mIsWeixinCircleInited){
            mIsWeixinCircleInited = true;
            UMWXHandler wxCircleHandler = new UMWXHandler(context, WEIXIN_APPID, WEIXIN_APPSECRET);
            wxCircleHandler.setToCircle(true);
            wxCircleHandler.addToSocialSDK();
        }
        share(new CircleShareContent(), SHARE_MEDIA.WEIXIN_CIRCLE, context, title, sharedText, sharedImageResId, sharedBitmap,
                targetUrl, listener);
    }
    public void shareToWeixinCircleWithImageOnly(Context context, Bitmap sharedBitmap, DefaultSnsPostListener listener){
        shareToWeixinCircle(context, null, null, 0, sharedBitmap, null, listener);
    }

    private boolean mIsWeiboInited;
    //分享到新浪微博
    public void shareToSina(Context context, String title, String sharedText, int sharedImageResId, Bitmap sharedBitmap,
                                    String targetUrl, SocializeListeners.SnsPostListener listener){
        if(!mIsWeiboInited){
            mIsWeiboInited = true;
//            new SinaSsoHandler(context).addToSocialSDK();
//            mShareService.getConfig().setSsoHandler(new SinaSsoHandler(context));
            mShareService.getConfig().setSinaCallbackUrl(WEIBO_REDIRECT_URI);
        }
        share(new SinaShareContent(), SHARE_MEDIA.SINA, context, title, sharedText, sharedImageResId, sharedBitmap,
                targetUrl, listener);
    }
    public void shareToSinaWithImageOnly(Context context, Bitmap sharedBitmap, DefaultSnsPostListener listener){
        shareToSina(context, null, null, 0, sharedBitmap, null, listener);
    }

    private boolean mIsQqInited;
    //分享到QQ
    public void shareToQQ(Context context, String title, String sharedText, int sharedImageResId, Bitmap sharedBitmap,
                                    String targetUrl, SocializeListeners.SnsPostListener listener){
        if(!mIsQqInited){
            mIsQqInited = true;
            new UMQQSsoHandler((Activity)context, QQ_APPID, QQ_APPSECRET).addToSocialSDK();
        }
        share(new QQShareContent(), SHARE_MEDIA.QQ, context, title, sharedText, sharedImageResId, sharedBitmap,
                targetUrl, listener);
    }
    public void shareToQQWithImageOnly(Context context, Bitmap sharedBitmap, DefaultSnsPostListener listener){
        shareToQQ(context, null, null, 0, sharedBitmap, null, listener);
    }

    private boolean mIsQzonInited;
    //分享到QQ空间
    public void shareToQzone(Context context, String title, String sharedText, int sharedImageResId, Bitmap sharedBitmap,
                                    String targetUrl, SocializeListeners.SnsPostListener listener){
        if(!mIsQzonInited){
            new QZoneSsoHandler((Activity)context, QQ_APPID, QQ_APPSECRET).addToSocialSDK();
            mIsQzonInited = true;
        }
        share(new QZoneShareContent(), SHARE_MEDIA.QZONE, context, title, sharedText, sharedImageResId, sharedBitmap,
                targetUrl, listener);
    }
    public void shareToQzoneWithImageOnly(Context context, Bitmap sharedBitmap, DefaultSnsPostListener listener){
        shareToQzone(context, null, null, 0, sharedBitmap, "http://shuxin.io/", listener);
    }

    //分享图文链接
    private void share(BaseShareContent content, SHARE_MEDIA media, Context context, String title, String sharedText,
                       int sharedImageResId, Bitmap sharedBitmap,
                       String targetUrl, SocializeListeners.SnsPostListener listener){
        if(!TextUtils.isEmpty(sharedText)){
            content.setShareContent(sharedText);
        }
        if(!TextUtils.isEmpty(title)){
            content.setTitle(title);
        }
        if(sharedBitmap == null){
            content.setShareImage(new UMImage(context, sharedImageResId));
        }else{
            content.setShareImage(new UMImage(context, sharedBitmap));
        }
        if(!TextUtils.isEmpty(targetUrl)){
            content.setTargetUrl(targetUrl);
        }
        if(!TextUtils.isEmpty(title)){
            content.setTitle(title);
        }
        mShareService.setShareMedia(content);
        mShareService.postShare(context, media, listener);
    }

    public static class DefaultSnsPostListener implements SocializeListeners.SnsPostListener{
        @Override
        public void onStart() {

        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, SocializeEntity socializeEntity) {

        }
    }
}
