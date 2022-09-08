package com.feijipan.gromore_ad.fullscreenvideoadinteraction;

import android.app.Activity;
import android.content.Context;


import androidx.annotation.NonNull;

import com.bytedance.msdk.adapter.util.Logger;
import com.bytedance.msdk.api.GMAdEcpmInfo;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAd;
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAdLoadCallback;
import com.bytedance.msdk.api.v2.slot.GMAdSlotInterstitialFull;
import com.feijipan.gromore_ad.util.UIUtils;

import java.util.List;
import java.util.Map;

import io.flutter.Log;

/**
 * 插全屏管理类。
 * 只需要复制粘贴到项目中，通过回调处理相应的业务逻辑即可使用完成广告加载&展示
 */
public class AdInterstitialFullManager {
    private static final String TAG = "Full_Screen_Video_Ad_Interaction";
    /**
     * 插全屏对应的广告对象
     * 每次加载全屏视频广告的时候需要新建一个GMInterstitialFullAd，否则可能会出现广告填充问题
     */
    private GMInterstitialFullAd mGMInterstitialFullAd;
    private Activity mActivity;
    private Map<String, Object> params;
    /**
     * 插全屏加载广告回调
     * 请在加载广告成功后展示广告
     */
    private GMInterstitialFullAdLoadCallback mGMInterstitialFullAdLoadCallback;
    private String mAdUnitId; //广告位
    private  int orientation;
    private int expressViewWidth;
    private int expressViewHeight;
    private String rewardName; //激励名称
    private String userID; // 用户ID
    private float volume; //0-1 音量
    private Context context;

    /**
     * 管理类构造函数
     * @param activity 全屏展示的Activity
     * @param interstitialFullAdLoadCallback 插全屏加载广告回调
     */
    public AdInterstitialFullManager(Activity activity, GMInterstitialFullAdLoadCallback interstitialFullAdLoadCallback) {
        mActivity = activity;
        mGMInterstitialFullAdLoadCallback = interstitialFullAdLoadCallback;
    }

    /**
     * 获取插全屏广告对象
     */
    public GMInterstitialFullAd getGMInterstitialFullAd() {
        return mGMInterstitialFullAd;
    }

    /**
     * 加载插全屏广告，如果没有config配置会等到加载完config配置后才去请求广告
      */
    public void loadAdWithCallback(Map<String, Object> params,Context context) {
        this.context = context;

        if (GMMediationAdSdk.configLoadSuccess()) {
            loadAd(params);
        } else {
            GMMediationAdSdk.registerConfigCallback(mSettingConfigCallback);
        }
    }

    /**
     * 加载插全屏广告，如果没有config配置会等到加载完config配置后才去请求广告
     * @param params  参数
     */
    private void loadAd(@NonNull Map<String, Object> params) {
        this.params = params;
        mAdUnitId = (String) params.get("codeId");
        orientation = (int) params.get("orientation");
        int width = (int) params.get("expressViewWidth");
        int height = (int) params.get("expressViewHeight");
        rewardName = params.get("rewardName").toString();
        userID = params.get("userID").toString();
        double v = (double)  params.get("volume");
        volume = (float)v;
        if (width == 0) {
                expressViewWidth = UIUtils.getScreenWidth(context);
            } else {
                expressViewWidth = width;
            }
            if (height == 0) {
                expressViewHeight = UIUtils.px2dip(context, UIUtils.getRealHeight(context));
            } else {
                expressViewHeight = height;
        }


        //Context 必须传activity
        mGMInterstitialFullAd = new GMInterstitialFullAd(mActivity, mAdUnitId);

//        Map<String, String> customData = new HashMap<>();
//        customData.put(GMAdConstant.CUSTOM_DATA_KEY_GDT, "gdt custom data");//目前仅支持gdt

        /**
         * 创建全屏广告请求类型参数GMAdSlotInterstitialFull,具体参数含义参考文档
         */
        GMAdSlotInterstitialFull adSlotInterstitialFull = new GMAdSlotInterstitialFull.Builder()
//                .setGMAdSlotBaiduOption(GMAdOptionUtil.getGMAdSlotBaiduOption().build())
//                .setGMAdSlotGDTOption(GMAdOptionUtil.getGMAdSlotGDTOption().build())
                .setImageAdSize(expressViewWidth, expressViewHeight)  //设置宽高 （插全屏类型下_插屏广告使用）
                .setVolume(volume) //admob 声音配置，与setMuted配合使用
                .setUserID(userID)//用户id,必传参数 (插全屏类型下_全屏广告使用)
                .setRewardName(rewardName) //奖励的名称
                .setRewardAmount(1)  //奖励的数量
                .setOrientation(orientation)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL; (插全屏类型下_全屏广告使用)
                .setBidNotify(true)//开启bidding比价结果通知，默认值为false
                .build();

        mGMInterstitialFullAd.loadAd(adSlotInterstitialFull, mGMInterstitialFullAdLoadCallback);
    }

    /**
     * 在Activity onDestroy中需要调用清理资源
     */
    public void destroy() {
        if (mGMInterstitialFullAd != null) {
            mGMInterstitialFullAd.destroy();
        }
        mActivity = null;
        mGMInterstitialFullAdLoadCallback = null;
        GMMediationAdSdk.unregisterConfigCallback(mSettingConfigCallback); //注销config回调
    }

    /**
     * config配置回调
     */
    private GMSettingConfigCallback mSettingConfigCallback = new GMSettingConfigCallback() {
        @Override
        public void configLoad() {
            loadAd(params);
        }
    };


    /**
     * ------------------------- 以下是非必要功能请选择性使用  --------------------------------------
     */

    /**
     * 展示广告加载信息
     */
    public void printLoadAdInfo() {
        if (mGMInterstitialFullAd == null) {
            return;
        }
        /**
         * 获取已经加载的clientBidding ，多阶底价广告的相关信息
         */
        List<GMAdEcpmInfo> gmAdEcpmInfos = mGMInterstitialFullAd.getMultiBiddingEcpm();
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
        GMAdEcpmInfo gmAdEcpmInfo = mGMInterstitialFullAd.getBestEcpm();
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
        List<GMAdEcpmInfo> gmCacheInfos = mGMInterstitialFullAd.getCacheList();
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
        if (mGMInterstitialFullAd == null) {
            return;
        }

        // 获取本次waterfall加载中，加载失败的adn错误信息。
        Log.d(TAG, "InterstitialFull ad loadinfos: " + mGMInterstitialFullAd.getAdLoadInfoList());
    }

    /**
     * 打印已经展示的广告信息
     */
    public void printSHowAdInfo() {
        if (mGMInterstitialFullAd == null) {
            return;
        }
        GMAdEcpmInfo gmAdEcpmInfo = mGMInterstitialFullAd.getShowEcpm();
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
