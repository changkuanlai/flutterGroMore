package com.feijipan.gromore_ad.fullscreenvideoadinteraction;

import android.app.Activity;
import android.content.Context;
import android.util.Log;


import androidx.annotation.NonNull;

import com.bytedance.msdk.adapter.util.Logger;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.reward.RewardItem;
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAdListener;
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAdLoadCallback;
import com.feijipan.gromore_ad.GromoreAdPlugin;

import java.util.HashMap;
import java.util.Map;

public class FullScreenVideoAdInteraction {
    private String  TAG = "Full_Screen_Video_Ad_Interaction";
    Context mContext;
    Activity mActivity;

    private AdInterstitialFullManager mAdInterstitialFullManager; //插全屏管理类

    private boolean mLoadSuccess; //是否加载成功
    private GMInterstitialFullAdListener mGMInterstitialFullAdListener;

    public FullScreenVideoAdInteraction(Context context, Activity mActivity, @NonNull Map<String, Object> params){

        this.mContext = context;
        this.mActivity = mActivity;
        initListener();
        initAdLoader();

        mAdInterstitialFullManager.loadAdWithCallback(params,context);
    }


     void initListener() {
        mGMInterstitialFullAdListener = new GMInterstitialFullAdListener() {
            @Override
            public void onInterstitialFullShow() {
                Log.d(TAG, "onInterstitialFullShow");
                Map map = new HashMap<String,String>();
                map.put("adType","fullScreenVideoAdInteraction");
                map.put("onAdMethod","onAdShow");
                 GromoreAdPlugin.getEventPlugin().sendContent(map);
            }

            /**
             * show失败回调。如果show时发现无可用广告（比如广告过期或者isReady=false），会触发该回调。
             * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
             * @param adError showFail的具体原因
             */
            @Override
            public void onInterstitialFullShowFail(@NonNull AdError adError) {
                Log.d(TAG, "onInterstitialFullShowFail");
                // 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载
                Map map = new HashMap<String,String>();
                map.put("adType","fullScreenVideoAdInteraction");
                map.put("onAdMethod","onAdFail");
                map.put("error",adError.message);
                GromoreAdPlugin.getEventPlugin().sendContent(map);
            }

            @Override
            public void onInterstitialFullClick() {
                Log.d(TAG, "onInterstitialFullClick");
                Map map = new HashMap<String,String>();
                map.put("adType","fullScreenVideoAdInteraction");
                map.put("onAdMethod","onAdClick");
                GromoreAdPlugin.getEventPlugin().sendContent(map);
            }

            @Override
            public void onInterstitialFullClosed() {
                Log.d(TAG, "onInterstitialFullClosed");
                Map map = new HashMap<String,String>();
                map.put("adType","fullScreenVideoAdInteraction");
                map.put("onAdMethod","onAdClose");
                GromoreAdPlugin.getEventPlugin().sendContent(map);
                onDestroy();
            }

            @Override
            public void onVideoComplete() {
                Log.d(TAG, "onVideoComplete");
//                Map map = new HashMap<String,String>();
//                map.put("adType","fullScreenVideoAdInteraction");
//                map.put("onAdMethod","onAdFinish");
//                GromoreAdPlugin.getEventPlugin().sendContent(map);
            }

            @Override
            public void onVideoError() {
                Log.d(TAG, "onVideoError");
            }

            @Override
            public void onSkippedVideo() {
                Log.d(TAG, "onSkippedVideo");
                Map map = new HashMap<String,String>();
                map.put("adType","fullScreenVideoAdInteraction");
                map.put("onAdMethod","onAdSkip");
                GromoreAdPlugin.getEventPlugin().sendContent(map);
            }

            /**
             * 当广告打开浮层时调用，如打开内置浏览器、内容展示浮层，一般发生在点击之后
             * 常常在onAdLeftApplication之前调用
             */
            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened");
            }

            /**
             * 此方法会在用户点击打开其他应用（例如 Google Play）时
             * 于 onAdOpened() 之后调用，从而在后台运行当前应用。
             */
            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication");
            }
             
            /// 验证奖励
            @Override
            public void onRewardVerify(@NonNull RewardItem rewardItem) {

            }
        };
    }

     void initAdLoader() {
        mAdInterstitialFullManager = new AdInterstitialFullManager(mActivity, new GMInterstitialFullAdLoadCallback() {
            @Override
            public void onInterstitialFullLoadFail(@NonNull AdError adError) {
                mLoadSuccess = false;
                Log.e(TAG, "load interaction ad error : " + adError.code + ", " + adError.message);
                mAdInterstitialFullManager.printLoadFailAdnInfo();// 获取本次waterfall加载中，加载失败的adn错误信息。

                Map map = new HashMap<String,String>();
                map.put("adType","fullScreenVideoAdInteraction");
                map.put("onAdMethod","onAdFail");
                map.put("error",adError.message);
                GromoreAdPlugin.getEventPlugin().sendContent(map);
            }

            @Override
            public void onInterstitialFullAdLoad() {
                mLoadSuccess = true;
                Log.e(TAG, "load interaction ad success ! ");
                mAdInterstitialFullManager.printLoadAdInfo(); //展示已经加载广告的信息
                mAdInterstitialFullManager.printLoadFailAdnInfo();// 获取本次waterfall加载中，加载失败的adn错误信息。
            }

            @Override
            public void onInterstitialFullCached() {
                mLoadSuccess = true;
                Log.d(TAG, "onFullVideoCached....缓存成功！");
                    showInterFullAd();
            }
        });
    }



    /**
     * 展示广告
     */
     void showInterFullAd() {
        if (mLoadSuccess && mAdInterstitialFullManager != null) {
            if (mAdInterstitialFullManager.getGMInterstitialFullAd() != null && mAdInterstitialFullManager.getGMInterstitialFullAd().isReady()) {
                //在获取到广告后展示,强烈建议在onInterstitialFullCached回调后，展示广告，提升播放体验
                //该方法直接展示广告，如果展示失败了（如过期），会回调onVideoError()
                //展示广告，并传入广告展示的场景
                mAdInterstitialFullManager.getGMInterstitialFullAd().setAdInterstitialFullListener(mGMInterstitialFullAdListener);
                mAdInterstitialFullManager.getGMInterstitialFullAd().showAd(mActivity);
                mAdInterstitialFullManager.printSHowAdInfo();//打印已经展示的广告信息
                mLoadSuccess = false;
            } else {
//                TToast.show(this, "当前广告不满足show的条件");
            }
        } else {
//            TToast.show(this, "请先加载广告");
        }
    }

    void onDestroy() {
        if (mAdInterstitialFullManager != null) {
            mAdInterstitialFullManager.destroy();
        }
    }

}
