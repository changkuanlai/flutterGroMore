package com.feijipan.gromore_ad.rewardvideoad;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.bytedance.msdk.adapter.util.Logger;
import com.bytedance.msdk.api.GMAdEcpmInfo;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardAd;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdLoadCallback;
import com.bytedance.msdk.api.v2.slot.GMAdOptionUtil;
import com.bytedance.msdk.api.v2.slot.GMAdSlotRewardVideo;
import com.feijipan.gromore_ad.util.UIUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 激励管理类。
 * 只需要复制粘贴到项目中，通过回调处理相应的业务逻辑即可使用完成广告加载&展示
 */
public class AdRewardManager {
    private static final String TAG = "AdRewardManager";

    /**
     * 激励对应的广告对象
     * 每次加载全屏视频广告的时候需要新建一个GMRewardAd，否则可能会出现广告填充问题
     */
    private GMRewardAd mGMRewardAd;
    private Activity mActivity;
    private Map<String, Object> params;
    /**
     * 激励加载广告回调
     * 请在加载广告成功后展示广告
     */
    private GMRewardedAdLoadCallback mGMRewardedAdLoadCallback;
    /**
     * GMAdConstant.HORIZONTAL 或 GMAdConstant.VERTICAL
     */
    private String mAdUnitId; //广告位
    private  int orientation;
    private String rewardName; //激励名称
    private String userID; // 用户ID
    private float volume; //0-1 音量

    /**
     * ------------------------- 以下是必要实现，如果不实现会导致加载广告失败  --------------------------------------
     */

    /**
     * 管理类构造函数
     * @param activity 激励展示的Activity
     * @param rewardedAdLoadCallback 激励加载广告回调
     */
    public AdRewardManager(Activity activity, GMRewardedAdLoadCallback rewardedAdLoadCallback) {
        mActivity = activity;
        mGMRewardedAdLoadCallback = rewardedAdLoadCallback;
    }

    /**
     * 获取激励广告对象
     */
    public GMRewardAd getGMRewardAd() {
        return mGMRewardAd;
    }

    /**
     * 加载激励广告，如果没有config配置会等到加载完config配置后才去请求广告
     */
    public void loadAdWithCallback(Map<String, Object> params,Context context) {
        this.params = params;
        if (GMMediationAdSdk.configLoadSuccess()) {
            loadAd(params);
        } else {
            GMMediationAdSdk.registerConfigCallback(mSettingConfigCallback);
        }
    }

    /**
     * 加载激励广告，如果没有config配置会等到加载完config配置后才去请求广告
     */
    private void loadAd(Map<String, Object> params) {
        mAdUnitId = (String) params.get("codeId");
        orientation = (int) params.get("orientation");
        rewardName = params.get("rewardName").toString();
        userID = params.get("userID").toString();
        double v = (double)  params.get("volume");
        volume = (float)v;

        mGMRewardAd = new GMRewardAd(mActivity,mAdUnitId);

        /**
         * 激励视频服务端验证，GroMore会把设置的字符串透传给相应的ADN
         */
//        Map<String, String> customData = new HashMap<>();
//        customData.put(GMAdConstant.CUSTOM_DATA_KEY_PANGLE, "pangle media_extra");
//        customData.put(GMAdConstant.CUSTOM_DATA_KEY_GDT, "gdt custom data");
//        customData.put(GMAdConstant.CUSTOM_DATA_KEY_KS, "ks custom data");
//        customData.put(GMAdConstant.CUSTOM_DATA_KEY_SIGMOB, "sigmob custom data");
//        customData.put(GMAdConstant.CUSTOM_DATA_KEY_MINTEGRAL, "mintegral custom data");
//        customData.put(GMAdConstant.CUSTOM_DATA_KEY_BAIDU, "baidu custom data");
//        // 如果开启了gromre服务端激励验证，可以传以下信息，跟adn无关。
//        customData.put(GMAdConstant.CUSTOM_DATA_KEY_GROMORE_EXTRA, "gromore serverside verify extra data"); // 会透传给媒体的服务器

        /**
         * 创建激励广告请求类型参数GMAdSlotRewardVideo,具体参数含义参考文档
         */
        GMAdSlotRewardVideo adSlotRewardVideo = new GMAdSlotRewardVideo.Builder()
                .setMuted(true)//对所有SDK的激励广告生效，除需要在平台配置的SDK，如穿山甲SDK
                .setVolume(volume)//配合Admob的声音大小设置[0-1]
//                .setGMAdSlotGDTOption(GMAdOptionUtil.getGMAdSlotGDTOption().build())
//                .setGMAdSlotBaiduOption(GMAdOptionUtil.getGMAdSlotBaiduOption().build())
                .setRewardName(rewardName) //奖励的名称
                .setRewardAmount(1)  //奖励的数量
                .setUserID(userID)//用户id,必传参数
                .setOrientation(orientation)//必填参数，期望视频的播放方向：GMAdConstant.HORIZONTAL 或 GMAdConstant.VERTICAL
                .build();
        mGMRewardAd.loadAd(adSlotRewardVideo, mGMRewardedAdLoadCallback);
    }

    /**
     * 在Activity onDestroy中需要调用清理资源
     */
    public void destroy() {
        if (mGMRewardAd != null) {
            mGMRewardAd.destroy();
        }
        mActivity = null;
        mGMRewardedAdLoadCallback = null;
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
        if (mGMRewardAd == null) {
            return;
        }
        /**
         * 获取已经加载的clientBidding ，多阶底价广告的相关信息
         */
        List<GMAdEcpmInfo> gmAdEcpmInfos = mGMRewardAd.getMultiBiddingEcpm();
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
        GMAdEcpmInfo gmAdEcpmInfo = mGMRewardAd.getBestEcpm();
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
        List<GMAdEcpmInfo> gmCacheInfos = mGMRewardAd.getCacheList();
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
        if (mGMRewardAd == null) {
            return;
        }

        // 获取本次waterfall加载中，加载失败的adn错误信息。
        Log.d(TAG, "reward ad loadinfos: " + mGMRewardAd.getAdLoadInfoList());
    }

    /**
     * 打印已经展示的广告信息
     */
    public void printSHowAdInfo() {
        if (mGMRewardAd == null) {
            return;
        }
        GMAdEcpmInfo gmAdEcpmInfo = mGMRewardAd.getShowEcpm();
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
