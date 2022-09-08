package com.feijipan.gromore_ad.bannerad;

import android.app.Activity;
import android.util.Log;

import com.bytedance.msdk.adapter.util.Logger;
import com.bytedance.msdk.api.GMAdEcpmInfo;
import com.bytedance.msdk.api.v2.GMAdSize;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAd;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAdListener;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.banner.GMNativeToBannerListener;
import com.bytedance.msdk.api.v2.slot.GMAdOptionUtil;
import com.bytedance.msdk.api.v2.slot.GMAdSlotBanner;


import java.util.List;
import java.util.Map;

/**
 * banner管理类。
 * 只需要复制粘贴到项目中，通过回调处理相应的业务逻辑即可使用完成广告加载&展示
 */
public class AdBannerManager {
    private static final String TAG = ""+ AdBannerManager.class.getSimpleName();

    /**
     * banner对应的广告对象
     * 每次加载banner的时候需要新建一个GMBannerAd，一个广告对象只能load一次，banner广告对象getBannerView只能一次，第二次调用会返回空
     */
    private GMBannerAd mBannerViewAd;
    private Activity mActivity;
    /**
     * banner加载广告回调
     * 请在加载广告成功后展示广告
     */
    private GMBannerAdLoadCallback mBannerAdLoadCallback;
    /**
     * banner广告监听回调
     */
    private GMBannerAdListener mAdBannerListener;
    private String mAdUnitId; //广告位
    private int expressViewWidth;
    private int expressViewHeight;
    private Map<String, Object> params;
    /**
     * ------------------------- 以下是必要实现，如果不实现会导致加载广告失败  --------------------------------------
     */

    /**
     * 管理类构造函数
     * @param activity banner展示的Activity
     * @param bannerAdLoadCallback banner加载广告回调
     * @param adBannerListener banner广告监听回调
     */
    public AdBannerManager(Activity activity, GMBannerAdLoadCallback bannerAdLoadCallback,
                           GMBannerAdListener adBannerListener) {
        mActivity = activity;
        mBannerAdLoadCallback = bannerAdLoadCallback;
        mAdBannerListener = adBannerListener;
//        mNativeToBannerListener = nativeToBannerListener;
    }

    /**
     * 获取banner广告对象
     */
    public GMBannerAd getBannerAd() {
        return mBannerViewAd;
    }

    /**
     * 加载banner广告，如果没有config配置会等到加载完config配置后才去请求广告
     */
    public void loadAdWithCallback(Map<String, Object> params) {
        this.params = params;
        this.mAdUnitId = (String) params.get("codeId");
        this.expressViewWidth = (int) params.get("expressViewWidth");
        this.expressViewHeight = (int) params.get("expressViewHeight");
        if (GMMediationAdSdk.configLoadSuccess()) {
            loadBannerAd(this.mAdUnitId);
        } else {
            GMMediationAdSdk.registerConfigCallback(mSettingConfigCallback);
        }
    }

    /**
     * 真正的开始加载banner广告
     * @param adUnitId 广告位id
     */
    private void loadBannerAd(final String adUnitId) {
        if (mBannerViewAd != null) {
            mBannerViewAd.destroy();
        }
        mBannerViewAd = new GMBannerAd(mActivity, adUnitId);

        //设置广告事件监听
        mBannerViewAd.setAdBannerListener(mAdBannerListener);
        // banner中混出自渲染信息流广告时，提供素材转成view的listener
//        mBannerViewAd.setNativeToBannerListener(mNativeToBannerListener);
//
//        // 当在banner中混出信息流时，该设置用于信息流相关的设置，例如gdt的设置如下：
//        // 针对Gdt Native自渲染广告，可以自定义gdt logo的布局参数。该参数可选,非必须。
//        FrameLayout.LayoutParams gdtNativeAdLogoParams =
//                new FrameLayout.LayoutParams(
//                        UIUtils.dip2px(mActivity.getApplicationContext(), 40),
//                        UIUtils.dip2px(mActivity.getApplicationContext(), 13),
//                        Gravity.RIGHT | Gravity.TOP); // 例如，放在右上角
//        GMAdSlotGDTOption.Builder adSlotNativeBuilder = GMAdOptionUtil.getGMAdSlotGDTOption()
//                .setNativeAdLogoParams(gdtNativeAdLogoParams);
        //设置广告配置
        /**
         * 创建BANNER广告请求类型参数GMAdSlotBanner,具体参数含义参考文档
         */
        GMAdSlotBanner slotBanner = new GMAdSlotBanner.Builder()
                .setBannerSize(GMAdSize.BANNER_CUSTOME)
                .setImageAdSize(expressViewWidth, expressViewHeight)// GMAdSize.BANNER_CUSTOME可以调用setImageAdSize设置大小
//                .setRefreshTime(30) // 从v3100版本开始，不支持sdk端设置banner轮播时间，只能从GroMore平台进行配置。sdk端设置无效。
                .setAllowShowCloseBtn(true)//如果广告本身允许展示关闭按钮，这里设置为true就是展示。注：目前只有mintegral支持。
//                .setBidNotify(true)//开启bidding比价结果通知，默认值为false
//                .setGMAdSlotGDTOption(adSlotNativeBuilder.build())
//                .setAdmobNativeAdOptions(GMAdOptionUtil.getAdmobNativeAdOptions())//admob相关配置
                .setMuted(true) // 控制视频是否静音
                .build();

        mBannerViewAd.loadAd(slotBanner, mBannerAdLoadCallback);
    }

    public void onResume() {
        if (mBannerViewAd != null) {
            mBannerViewAd.onResume();
        }
    }

    public void onPause() {
        if (mBannerViewAd != null) {
            mBannerViewAd.onPause();
        }
    }

    /**
     * 在Activity onDestroy中需要调用清理资源
     */
    public void destroy() {
        if (mBannerViewAd != null) {
            mBannerViewAd.destroy();
        }
        mActivity = null;
        mBannerViewAd = null;
        mBannerAdLoadCallback = null;
        mAdBannerListener = null;
        GMMediationAdSdk.unregisterConfigCallback(mSettingConfigCallback);
    }


    /**
     * config配置回调
     */
    private GMSettingConfigCallback mSettingConfigCallback = new GMSettingConfigCallback() {
        @Override
        public void configLoad() {
            loadAdWithCallback(params);
        }
    };


    /**
     * ------------------------- 以下是非必要功能请选择性使用  --------------------------------------
     */

    /**
     * 展示广告加载信息
     */
    public void printLoadAdInfo() {
        if (mBannerViewAd == null) {
            return;
        }
        /**
         * 获取已经加载的clientBidding ，多阶底价广告的相关信息
         */
        List<GMAdEcpmInfo> gmAdEcpmInfos = mBannerViewAd.getMultiBiddingEcpm();
        if (gmAdEcpmInfos != null) {
            for (GMAdEcpmInfo info : gmAdEcpmInfos) {
                Log.e(TAG, "***多阶+client相关信息*** AdNetworkPlatformId" + info.getAdNetworkPlatformId()
                        + "  AdNetworkRitId:" + info.getAdNetworkRitId()
                        + "  ReqBiddingType:" + info.getReqBiddingType()
                        + "  PreEcpm:" + info.getPreEcpm()
                        + "  LevelTag:" + info.getLevelTag()
                        + "  ErrorMsg:" + info.getErrorMsg()
                        + "  request_id:" + info.getRequestId()
                        + "  SdkName:" + info.getAdNetworkPlatformName()
                        + "  CustomSdkName:" + info.getCustomAdNetworkPlatformName());
            }
        }

        /**
         * 获取实时填充/缓存池中价格最优的代码位信息即相关价格信息，每次只有一个信息
         */
        GMAdEcpmInfo gmAdEcpmInfo = mBannerViewAd.getBestEcpm();
        if (gmAdEcpmInfo != null) {
            Log.e(TAG, "***实时填充/缓存池中价格最优的代码位信息*** AdNetworkPlatformId" + gmAdEcpmInfo.getAdNetworkPlatformId()
                    + "  AdNetworkRitId:" + gmAdEcpmInfo.getAdNetworkRitId()
                    + "  ReqBiddingType:" + gmAdEcpmInfo.getReqBiddingType()
                    + "  PreEcpm:" + gmAdEcpmInfo.getPreEcpm()
                    + "  LevelTag:" + gmAdEcpmInfo.getLevelTag()
                    + "  ErrorMsg:" + gmAdEcpmInfo.getErrorMsg()
                    + "  request_id:" + gmAdEcpmInfo.getRequestId()
                    + "  SdkName:" + gmAdEcpmInfo.getAdNetworkPlatformName()
                    + "  CustomSdkName:" + gmAdEcpmInfo.getCustomAdNetworkPlatformName());
        }

        /**
         * 获取获取当前缓存池的全部信息
         */
        List<GMAdEcpmInfo> gmCacheInfos = mBannerViewAd.getCacheList();
        if (gmCacheInfos != null) {
            for (GMAdEcpmInfo info : gmCacheInfos) {
                Log.e(TAG, "***缓存池的全部信息*** AdNetworkPlatformId" + info.getAdNetworkPlatformId()
                        + "  AdNetworkRitId:" + info.getAdNetworkRitId()
                        + "  ReqBiddingType:" + info.getReqBiddingType()
                        + "  PreEcpm:" + info.getPreEcpm()
                        + "  LevelTag:" + info.getLevelTag()
                        + "  ErrorMsg:" + info.getErrorMsg()
                        + "  request_id:" + info.getRequestId()
                        + "  SdkName:" + info.getAdNetworkPlatformName()
                        + "  CustomSdkName:" + info.getCustomAdNetworkPlatformName());
            }
        }
    }

    /**
     * 打印加载失败的adn错误信息
     */
    public void printLoadFailAdnInfo() {
        if (mBannerViewAd == null) {
            return;
        }

        // 获取本次waterfall加载中，加载失败的adn错误信息。
        Log.d(TAG, "banner ad loadinfos: " + mBannerViewAd.getAdLoadInfoList());
    }

    /**
     * 打印已经展示的广告信息
     */
    public void printShowAdInfo() {
        if (mBannerViewAd == null) {
            return;
        }
        GMAdEcpmInfo gmAdEcpmInfo = mBannerViewAd.getShowEcpm();
        if (gmAdEcpmInfo == null) {
            return;
        }
        String s = ""+
                gmAdEcpmInfo.getAdNetworkRitId()+
                gmAdEcpmInfo.getAdnName()+
                gmAdEcpmInfo.getPreEcpm();
        Logger.e(TAG, s);
    }
}
