package com.feijipan.gromore_ad.rewardvideoad;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.bytedance.msdk.adapter.util.Logger;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.reward.RewardItem;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdListener;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdLoadCallback;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.feijipan.gromore_ad.GromoreAdPlugin;
import com.feijipan.gromore_ad.util.UIUtils;

import java.util.HashMap;
import java.util.Map;

import io.flutter.Log;

public class RewardVideoAdView {

    private final String TAG = "gromore_ad_rewardVideoAdView";

    private AdRewardManager mAdRewardManager; //激励视频管理类
    private GMRewardedAdListener mGMRewardedAdListener;

    private boolean mLoadSuccess; //是否加载成功
    private Activity mActivity;
    public RewardVideoAdView(Context context, Activity mActivity, @NonNull Map<String, Object> params){
        this.mActivity = mActivity;
        initListener();
        initAdLoader();
        mAdRewardManager.loadAdWithCallback(params,context);
     }


    public void initListener() {

        mGMRewardedAdListener = new GMRewardedAdListener() {

            /**
             * 广告的展示回调 每个广告仅回调一次
             */
            public void onRewardedAdShow() {
                Log.d(TAG, "onRewardedAdShow");
                Map map = new HashMap<String,String>();
                map.put("adType","rewardedVideoAd");
                map.put("onAdMethod","onAdShow");
                GromoreAdPlugin.getEventPlugin().sendContent(map);
            }

            /**
             * show失败回调。如果show时发现无可用广告（比如广告过期或者isReady=false），会触发该回调。
             * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
             * @param adError showFail的具体原因
             */
            @Override
            public void onRewardedAdShowFail(AdError adError) {
                if (adError == null) {
                    return;
                }
                Log.d(TAG, "onRewardedAdShowFail, errCode: " + adError.code + ", errMsg: " + adError.message);
                // 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载
                Map map = new HashMap<String,String>();
                map.put("adType","rewardedVideoAd");
                map.put("onAdMethod","onAdFail");
                map.put("error",adError.message);
                 GromoreAdPlugin.getEventPlugin().sendContent(map);
            }

            /**
             * 注意Admob的激励视频不会回调该方法
             */
            @Override
            public void onRewardClick() {
                Log.d(TAG, "onRewardClick");
                Map map = new HashMap<String,String>();
                map.put("adType","rewardedVideoAd");
                map.put("onAdMethod","onAdClick");
                GromoreAdPlugin.getEventPlugin().sendContent(map);
            }

            /**
             * 广告关闭的回调
             */
            public void onRewardedAdClosed() {
                Log.d(TAG, "onRewardedAdClosed");
                Map map = new HashMap<String,String>();
                map.put("adType","rewardedVideoAd");
                map.put("onAdMethod","onAdClose");
                GromoreAdPlugin.getEventPlugin().sendContent(map);
                onDestroy();
            }

            /**
             * 视频播放完毕的回调 Admob广告不存在该回调
             */
            public void onVideoComplete() {
                Log.d(TAG, "onVideoComplete");

            }

            /**
             * 1、视频播放失败的回调
             */
            public void onVideoError() {
                Log.d(TAG, "onVideoError");
            }

            /**
             * 激励视频播放完毕，验证是否有效发放奖励的回调
             */
            public void onRewardVerify(RewardItem rewardItem) {

                Log.d(TAG, "onRewardVerify");
            }

            /**
             * - Mintegral GDT Admob广告不存在该回调
             */
            @Override
            public void onSkippedVideo() {

            }

        };

    }

    public void initAdLoader() {
        mAdRewardManager = new AdRewardManager(mActivity, new GMRewardedAdLoadCallback() {
            @Override
            public void onRewardVideoLoadFail(AdError adError) {
                mLoadSuccess = false;
                Log.e(TAG, "load RewardVideo ad error : " + adError.code + ", " + adError.message);
                mAdRewardManager.printLoadFailAdnInfo();
                Map map = new HashMap<String,String>();
                map.put("adType","rewardedVideoAd");
                map.put("onAdMethod","onAdFail");
                map.put("error",adError.message);
                GromoreAdPlugin.getEventPlugin().sendContent(map);
            }

            @Override
            public void onRewardVideoAdLoad() {
                mLoadSuccess = true;
                Log.e(TAG, "load RewardVideo ad success !");
                // 获取本次waterfall加载中，加载失败的adn错误信息。
                mAdRewardManager.printLoadAdInfo(); //打印已经加载广告的信息
                mAdRewardManager.printLoadFailAdnInfo();//打印加载失败的adn错误信息
            }

            @Override
            public void onRewardVideoCached() {
                mLoadSuccess = true;
                Log.d(TAG, "onRewardVideoCached....缓存成功");
                    showRewardAd();
            }
        });
    }

    /**
     * 展示广告
     */
    private void showRewardAd() {
        if (mLoadSuccess && mAdRewardManager != null) {
            if (mAdRewardManager.getGMRewardAd() != null && mAdRewardManager.getGMRewardAd().isReady()) {
                //在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
                //该方法直接展示广告，如果展示失败了（如过期），会回调onVideoError()
                //展示广告，并传入广告展示的场景
                mAdRewardManager.getGMRewardAd().setRewardAdListener(mGMRewardedAdListener);
                mAdRewardManager.getGMRewardAd().showRewardAd(mActivity);
                mAdRewardManager.printSHowAdInfo();//打印已经展示的广告信息
                mLoadSuccess = false;
            } else {
//                TToast.show(this, "当前广告不满足show的条件");
            }
        } else {
            Log.e(TAG, "onRewardVideo 当前还没有加载");

        }
    }

      void onDestroy() {
        if (mAdRewardManager != null) {
            mAdRewardManager.destroy();
        }
    }

}
