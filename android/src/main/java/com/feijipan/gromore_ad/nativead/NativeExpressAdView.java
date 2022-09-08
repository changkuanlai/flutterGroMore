package com.feijipan.gromore_ad.nativead;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.v2.GMAdSize;
import com.bytedance.msdk.api.v2.GMDislikeCallback;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeExpressAdListener;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMVideoListener;
import com.feijipan.gromore_ad.util.UIUtils;
import com.feijipan.gromore_ad.util.ViewConfig;

import java.util.List;
import java.util.Map;

import io.flutter.Log;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

 public class NativeExpressAdView implements PlatformView {

    private final String TAG = "gromore_ad_NativeExpressAdView";
    private FrameLayout mFeedContainer;

    private AdFeedManager mAdFeedManager; //激励视频管理类

    private GMNativeAd mGMNativeAd; //原生广告model
    private Context mContext;
    private Activity activity;
    private MethodChannel channel;

    public NativeExpressAdView(@NonNull Context context, int id, BinaryMessenger messenger, @Nullable Map<String, Object> params, Activity activity) {
        this.activity = activity;
        this.mContext = context;
        mFeedContainer = new FrameLayout(context);
        initAdLoader();
        mAdFeedManager.loadAdWithCallback(params);
        channel = new MethodChannel(messenger, ViewConfig.nativeAdView + "_" + id);
    }

    @Override
    public View getView() {
        final ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(800, 800));
        imageView.setBackgroundColor(Color.argb(255, 111, 111, 111));
        return mFeedContainer;
    }

    @Override
    public void dispose() {
        Log.e(TAG, "on dispose:  释放广告");
        mAdFeedManager.destroy();
        mAdFeedManager.getGMUnifiedNativeAd().destroy();
        mFeedContainer.removeAllViews();
    }

    void initAdLoader() {
        mAdFeedManager = new AdFeedManager(activity, new GMNativeAdLoadCallback() {
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
//                for (GMNativeAd ttNativeAd : ads) {
//                     mAdFeedManager.printShowAdInfo(ttNativeAd); //打印已经展示的广告信息
//
//                    Log.d(TAG, "adn: " + ttNativeAd.getAdNetworkPlatformId());
//                    Map<String, Object> mediaExtraInfo = ttNativeAd.getMediaExtraInfo();
//                    if (mediaExtraInfo != null) {
//                        Log.d(TAG, "coupon: " + mediaExtraInfo.get("coupon"));
//                        Log.d(TAG, "live_room: " + mediaExtraInfo.get("live_room"));
//                        Log.d(TAG, "product: " + mediaExtraInfo.get("product"));
//                    }
//                }
                showAd();

            }

            @Override
            public void onAdLoadedFail(AdError adError) {
                Log.e(TAG, " 广告加载失败 load feed ad error : " + adError.code + ", " + adError.message);
                mAdFeedManager.printLoadFailAdnInfo();// 获取本次waterfall加载中，加载失败的adn错误信息。
                channel.invokeMethod("onAdFail", adError.message);
            }
        });
    }


    /**
     * 展示原生广告
     */
    private void showAd() {

        if (!mGMNativeAd.isReady()) {
            Log.e(TAG, "广告已经无效，请重新请求");
            return;
        }
//        View view = null;
        if (mGMNativeAd.isExpressAd()) { //模板
            getExpressAdView(mGMNativeAd);
        }

    }

    private void getExpressAdView(@NonNull final GMNativeAd ad) {
        //判断是否存在dislike按钮
        if (ad.hasDislike()) {
            ad.setDislikeCallback(activity, new GMDislikeCallback() {
                @Override
                public void onSelected(int position, String value) {

                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "dislike 点击了取消");
                    channel.invokeMethod("onAdDislike", "点了不喜欢");
                }

                /**
                 * 拒绝再次提交
                 */
                @Override
                public void onRefuse() {

                }

                @Override
                public void onShow() {

                }
            });
        }

        //设置点击展示回调监听
        ad.setNativeAdListener(new GMNativeExpressAdListener() {
            @Override
            public void onAdClick() {
                Log.d(TAG, "onAdClick");
                channel.invokeMethod("onAdClick", "");
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow");

            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e(TAG, "模板广告渲染失败code onRenderFail   code=" + code + ",msg=" + msg);
                channel.invokeMethod("onAdFail", msg);

            }

            // ** 注意点 ** 不要在广告加载成功回调里进行广告view展示，要在onRenderSucces进行广告view展示，否则会导致广告无法展示。
            @Override
            public void onRenderSuccess(float width, float height) {
                Log.d(TAG, "onRenderSuccess" + "模板广告渲染成功:width=" + width + ",height=" + height);
                //获取视频播放view,该view SDK内部渲染，在媒体平台可配置视频是否自动播放等设置。
                int sWidth;
                int sHeight;
                /**
                 * 如果存在父布局，需要先从父布局中移除
                 */
                final View video = ad.getExpressView(); // 获取广告view  如果存在父布局，需要先从父布局中移除
                if (width == GMAdSize.FULL_WIDTH && height == GMAdSize.AUTO_HEIGHT) {
                    sWidth = FrameLayout.LayoutParams.MATCH_PARENT;
                    sHeight = FrameLayout.LayoutParams.WRAP_CONTENT;
                } else {
                    sWidth = UIUtils.getScreenWidth(mContext);
                    sHeight = (int) ((sWidth * height) / width);
                }
                mFeedContainer.removeAllViews();
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(sWidth, sHeight);
                mFeedContainer.addView(video, layoutParams);
                channel.invokeMethod("onAdShow","");

            }
        });

        //视频广告设置播放状态回调（可选）
        ad.setVideoListener(new GMVideoListener() {

            @Override
            public void onVideoStart() {
                Log.d(TAG, "onVideoStart 模板广告视频开始播放");
            }

            @Override
            public void onVideoPause() {
                Log.d(TAG, "onVideoPause 模板广告视频暂停");

            }

            @Override
            public void onVideoResume() {
                Log.d(TAG, "onVideoResume 模板广告视频继续播放");

            }

            @Override
            public void onVideoCompleted() {
                Log.d(TAG, "onVideoCompleted 模板播放完成");
            }

            @Override
            public void onVideoError(AdError adError) {
                Log.e(TAG, "onVideoError 模板广告视频播放出错");
            }
        });

        ad.render();
    }


}
