package com.feijipan.gromore_ad.splash;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bytedance.msdk.adapter.util.Logger;
import com.bytedance.msdk.adapter.util.UIUtils;
import com.bytedance.msdk.api.GMAdEcpmInfo;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMNetworkRequestInfo;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAd;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAdListener;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAdLoadCallback;
import com.bytedance.msdk.api.v2.slot.GMAdSlotSplash;
import com.feijipan.gromore_ad.util.SplashUtils;


import java.util.List;
import java.util.Map;

/**
 * 开屏管理类。
 * 只需要复制粘贴到项目中，通过回调处理相应的业务逻辑即可使用完成广告加载&展示
 */
public class AdSplashManager {
    private static final String TAG = "" + AdSplashManager.class.getSimpleName();

    /**
     * 开屏对应的广告对象
     * 每次加载全屏视频广告的时候需要新建一个GMSplashAd，否则可能会出现广告填充问题
     */
    private GMSplashAd mSplashAd;
    private Activity mActivity;
    //开屏广告加载超时时间,建议大于1000,这里为了冷启动第一次加载到广告并且展示,示例设置了2000ms
    private static final int AD_TIME_OUT = 3000;
    //强制加载兜底开屏广告，只能在GroMore提供的demo中使用，其他情况设置无效
    private boolean mForceLoadBottom = false;
    /**
     * 开屏加载广告回调
     * 请在加载广告成功后展示广告
     */
    private GMSplashAdLoadCallback mGMSplashAdLoadCallback;
    /**
     * 开屏广告监听回调
     */
    private GMSplashAdListener mSplashAdListener;

    /**
     * ------------------------- 以下是必要实现，如果不实现会导致加载广告失败  --------------------------------------
     */

    /**
     * 管理类构造函数
     *
     * @param activity             开屏展示的Activity
     * @param splashAdLoadCallback 开屏加载广告回调
     * @param splashAdListener     开屏广告监听回调
     */
    public AdSplashManager(Activity activity, GMSplashAdLoadCallback splashAdLoadCallback, GMSplashAdListener splashAdListener) {
        mActivity = activity;
        mGMSplashAdLoadCallback = splashAdLoadCallback;
        mSplashAdListener = splashAdListener;
    }

    /**
     * 获取开屏广告对象
     */
    public GMSplashAd getSplashAd() {
        return mSplashAd;
    }

    /**
     * 加载开屏广告
     *
     */
    public void loadSplashAd(@Nullable Map<String, Object> params) {
        String adUnitId = params.get("codeId").toString();
        mForceLoadBottom = (boolean) params.get("forceLoadBottom");

        mSplashAd = new GMSplashAd(mActivity, adUnitId);
        mSplashAd.setAdSplashListener(mSplashAdListener);

        /**
         * 创建开屏广告请求类型参数GMAdSlotSplash,具体参数含义参考文档
         */
        GMAdSlotSplash adSlot = new GMAdSlotSplash.Builder()
                .setImageAdSize(UIUtils.getScreenWidth(mActivity), UIUtils.getScreenHeight(mActivity)) // 单位px
                .setTimeOut(AD_TIME_OUT)//设置超时
                .setSplashPreLoad(true)
//                .setSplashButtonType(GMAdConstant.SPLASH_BUTTON_TYPE_FULL_SCREEN)
//                .setDownloadType(GMAdConstant.DOWNLOAD_TYPE_POPUP)
                .setForceLoadBottom(mForceLoadBottom) //强制加载兜底开屏广告，只能在GroMore提供的demo中使用，其他情况设置无效
                .setBidNotify(true)//开启bidding比价结果通知，默认值为false
//                .setSplashShakeButton(true) //开屏摇一摇开关，默认开启，目前只有gdt支持
                .build();

        //自定义兜底方案 选择使用
        GMNetworkRequestInfo networkRequestInfo = SplashUtils.getGMNetworkRequestInfo();

        mSplashAd.loadAd(adSlot, networkRequestInfo, mGMSplashAdLoadCallback);
    }


    /**
     * ------------------------- 以下是非必要功能请选择性使用  --------------------------------------
     */

    /**
     * 打印其他加载信息
     */
    public void printInfo() {
        if (mSplashAd != null) {
            /**
             * 获取已经加载的clientBidding ，多阶底价广告的相关信息
             */
            List<GMAdEcpmInfo> gmAdEcpmInfos = mSplashAd.getMultiBiddingEcpm();
            if (gmAdEcpmInfos != null) {
                for (GMAdEcpmInfo info : gmAdEcpmInfos) {
                    Log.e(TAG, "多阶+client相关信息 AdNetworkPlatformId" + info.getAdNetworkPlatformId()
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
            GMAdEcpmInfo gmAdEcpmInfo = mSplashAd.getBestEcpm();
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
            List<GMAdEcpmInfo> gmCacheInfos = mSplashAd.getCacheList();
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

            /**
             * 获取获展示广告的部信息
             */
            GMAdEcpmInfo showGMAdEcpmInfo = mSplashAd.getShowEcpm();

            if (showGMAdEcpmInfo != null) {

                String s = "展示的广告信息 ：代码位id = " + showGMAdEcpmInfo.getAdNetworkRitId()
                        + "adnName = " + showGMAdEcpmInfo.getAdnName() + " ecpm = " + showGMAdEcpmInfo.getPreEcpm();
                Logger.e(TAG, s);
            }
            // 获取本次waterfall加载中，加载失败的adn错误信息。
            if (mSplashAd != null)
                Log.d(TAG, "ad load infos: " + mSplashAd.getAdLoadInfoList());
        }
    }


    /**
     * 在Activity onDestroy中需要调用清理资源
     */
    public void destroy() {
        if (mSplashAd != null) {
            mSplashAd.destroy();
        }
        mActivity = null;
        mGMSplashAdLoadCallback = null;
        mSplashAdListener = null;
    }

}
