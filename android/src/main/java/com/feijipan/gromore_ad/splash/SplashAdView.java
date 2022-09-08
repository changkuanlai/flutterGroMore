package com.feijipan.gromore_ad.splash;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAdListener;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAdLoadCallback;

import com.feijipan.gromore_ad.GromoreAdPlugin;
import com.feijipan.gromore_ad.util.ViewConfig;

import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class SplashAdView implements PlatformView {

    private final String TAG = "gromore_ad_SplashAdView";
    private final FrameLayout mExpressContainer;
    private AdSplashManager mAdSplashManager;
    private GMSplashAdListener mSplashAdListener;


    Context _context;
 
    private final MethodChannel channel;

    SplashAdView(@NonNull Context context, int id, BinaryMessenger messenger, @Nullable Map<String, Object> params) {
        _context = context;
        assert params != null;
         mExpressContainer = new FrameLayout(context);
        channel = new MethodChannel(messenger, ViewConfig.splashAdView+"_"+id);
        initListener();
        initAdLoader();
        //加载开屏广告
        if (mAdSplashManager != null) {
            mAdSplashManager.loadSplashAd(params);
        }
    }
    
    @Override
    public View getView() {
        return mExpressContainer;
    }

    @Override
    public void dispose() {
        if (mAdSplashManager != null) {
            mAdSplashManager.destroy();
         }
    }

    public void initListener() {
        mSplashAdListener = new GMSplashAdListener() {
            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
                channel.invokeMethod("onAdClicked","");
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow");
                channel.invokeMethod("onAdShow","");
            }

            /**
             * show失败回调。如果show时发现无可用广告（比如广告过期），会触发该回调。
             * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
             * @param adError showFail的具体原因
             */
            @Override
            public void onAdShowFail(AdError adError) {
                Log.d(TAG, "onAdShowFail");
                channel.invokeMethod("onAdFail",adError.message);
            }

            @Override
            public void onAdSkip() {
                Log.d(TAG, "onAdSkip");
                goToMainActivity();
                channel.invokeMethod("onAdSkip","");
            }

            @Override
            public void onAdDismiss() {
                Log.d(TAG, "onAdDismiss");
                channel.invokeMethod("onAdDismiss","");
             }
        };
    }


    public void initAdLoader() {

        mAdSplashManager = new AdSplashManager(GromoreAdPlugin.getAppActivity(), new GMSplashAdLoadCallback() {
            @Override
            public void onSplashAdLoadFail(AdError adError) {
                Log.d(TAG, adError.message);
                Log.e(TAG, "load splash ad error : " + adError.code + ", " + adError.message);

                // 获取本次waterfall加载中，加载失败的adn错误信息。
                if (mAdSplashManager.getSplashAd() != null)
                    Log.d(TAG, "ad load infos: " + mAdSplashManager.getSplashAd().getAdLoadInfoList());

                channel.invokeMethod("onAdFail",adError.message);
                goToMainActivity();
            }

            @Override
            public void onSplashAdLoadSuccess() {
                Log.e(TAG, "load splash ad success ");
                 // 根据需要选择调用isReady()
                if (mAdSplashManager.getSplashAd().isReady()) {
                    mAdSplashManager.getSplashAd().showAd(mExpressContainer);
                }

            }
            // 注意：***** 开屏广告加载超时回调已废弃，统一走onSplashAdLoadFail，GroMore作为聚合不存在SplashTimeout情况。*****
            @Override
            public void onAdLoadTimeout() {
            }
        }, mSplashAdListener);
    }
    
    /**
     * 跳转到主页面
     */
    private void goToMainActivity() {
        mExpressContainer.removeAllViews();
        this.mAdSplashManager.destroy();
    }
}
