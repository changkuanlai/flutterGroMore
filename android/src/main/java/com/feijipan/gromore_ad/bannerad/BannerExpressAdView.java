package com.feijipan.gromore_ad.bannerad;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.TToast;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAdListener;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAdLoadCallback;
import com.feijipan.gromore_ad.util.ViewConfig;

import java.util.Map;

import io.flutter.Log;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class BannerExpressAdView implements PlatformView {

    private final String TAG = "gromore_ad_BannerExpressAdView";

    private AdBannerManager adBannerManager;
    private MethodChannel channel;
    private Activity activity;
    private FrameLayout mBannerContainer;
    // banner广告事件的监听
    private GMBannerAdListener mAdBannerListener;
    private Context mContext;

    public BannerExpressAdView(@NonNull Context context, int id, BinaryMessenger messenger, @Nullable
                                Map<String, Object> params, Activity activity) {
        this.activity = activity;
        this.mContext = context;
        mBannerContainer = new FrameLayout(context);
        channel = new MethodChannel(messenger, ViewConfig.bannerAdView+"_"+id);
        initListener();
        adBannerManager.loadAdWithCallback(params);
    }




    @Override
    public View getView() {
        return mBannerContainer;
    }

    @Override
    public void dispose() {
      adBannerManager.destroy();
    }
    void initListener() {

        mAdBannerListener = new GMBannerAdListener() {

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed");
                if (mBannerContainer != null) {
                    mBannerContainer.removeAllViews();
                }
                if (adBannerManager != null && adBannerManager.getBannerAd() != null) {
                    adBannerManager.getBannerAd().destroy();
                }
                channel.invokeMethod("onAdDislike", "banner 点了不喜欢");
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
                channel.invokeMethod("onAdClicked", "");

            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow");
                channel.invokeMethod("onAdShow","" );
            }

            /**
             * show失败回调。如果show时发现无可用广告（比如广告过期），会触发该回调。
             * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
             *
             * @param adError showFail的具体原因
             */
            @Override
            public void onAdShowFail(AdError adError) {
                Log.d(TAG, "onAdShowFail");
                if(adError.code == 41044){
                    TToast.show(mContext, adError.message);
                }
                channel.invokeMethod("onAdFail", adError.message);
             }
        };

        adBannerManager = new AdBannerManager(activity, new GMBannerAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull AdError adError) {
                Log.d(TAG, "onAdShowFail");
                channel.invokeMethod("onAdFail", adError.message);
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
                showBannerAd();
            }
        },mAdBannerListener);
    }

    /**
     * 展示广告
     */
    private void showBannerAd() {

            /**
             * 在添加banner的View前需要清空父容器
             */
            mBannerContainer.removeAllViews();
            if (adBannerManager.getBannerAd() != null) {
                // 在调用getBannerView之前，可以选择使用isReady进行判断，当前是否有可用广告。
                if (!adBannerManager.getBannerAd().isReady()) {
//                    TToast.show(this, "广告已经无效，建议重新请求");
                    channel.invokeMethod("onAdFail", "请重新加载广告");

                    return;
                }
                //横幅广告容器的尺寸必须至少与横幅广告一样大。如果您的容器留有内边距，实际上将会减小容器大小。如果容器无法容纳横幅广告，则横幅广告不会展示
                /**
                 * mBannerViewAd.getBannerView()一个广告对象只能调用一次，第二次为null
                 */
                View view = adBannerManager.getBannerAd().getBannerView();
                if (view != null) {
                    mBannerContainer.addView(view);
                } else {
//                    TToast.show(this, "");
                    channel.invokeMethod("onAdFail", "请重新加载广告");

                }
            }
        }

}


