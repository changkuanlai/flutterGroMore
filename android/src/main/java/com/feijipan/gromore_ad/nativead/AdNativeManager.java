package com.feijipan.gromore_ad.nativead;

import android.app.Activity;

import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAdLoadCallback;

import java.util.List;
import java.util.Map;

import io.flutter.Log;

public class AdNativeManager {

    private static final String TAG = "AdNativeManager";
    
    public GMNativeAd mGMNativeAd;
    private Activity mActivity;
    private Map<String, Object> params;
    public AdFeedManager mAdFeedManager;
    boolean isRead = false;
    public AdNativeManager(Activity activity, Map<String, Object> params){
        this.mActivity = activity;
        this.params = params;
        initAdLoader();
    }
  
    public void initAdLoader() {
        mAdFeedManager = new AdFeedManager(mActivity, new GMNativeAdLoadCallback() {
            @Override
            public void onAdLoaded(List<GMNativeAd> ads) {
                mAdFeedManager.printLoadAdInfo(); //打印已经加载广告的信息
                mAdFeedManager.printLoadFailAdnInfo();// 获取本次waterfall加载中，加载失败的adn错误信息。

                if (ads == null || ads.isEmpty()) {
                    Log.e(TAG, "on FeedAdLoaded: ad is null! 广告加载失败");
                    return;
                }
                Log.e(TAG, "广告加载成功！");
                mGMNativeAd = ads.get(0);
                isRead = true;
            }

            @Override
            public void onAdLoadedFail(AdError adError) {
                Log.e(TAG, " 广告加载失败 load feed ad error : " + adError.code + ", " + adError.message);
                mAdFeedManager.printLoadFailAdnInfo();//
                // 获取本次waterfall加载中，加载失败的adn错误信息。
                mAdFeedManager.loadAdWithCallback(params);
                isRead = false;
             }
        });

         mAdFeedManager.loadAdWithCallback(params);
     }
}
