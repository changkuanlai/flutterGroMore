package com.feijipan.gromore_ad;

import android.content.Context;
import android.provider.Settings;
import androidx.annotation.NonNull;

import com.bytedance.msdk.api.v2.GMAdConfig;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMConfigUserInfoForSegment;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMPangleOption;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.sdk.openadsdk.*;

import java.util.HashMap;
import java.util.Map;

import io.flutter.Log;
import io.flutter.plugin.common.MethodChannel;


/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class GMAdManagerHolder {

    private static final String TAG = "GMAdManagerHolder.regesig";
     private static boolean sInit;

    public static void init(Context context,Map  map,@NonNull MethodChannel.Result result) {
        doInit(context,map,result);
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private static void doInit(@NonNull Context context,Map map,@NonNull MethodChannel.Result result) {
        if (!sInit) {
            GMMediationAdSdk.initialize(context, buildV2Config(context,map));

            GMMediationAdSdk.registerConfigCallback(new GMSettingConfigCallback() {
                @Override
                public void configLoad() {
                    Log.e(TAG, "注册成功");
                   result.success(true);
                }
            });
            sInit = true;
        }
    }

    public static GMAdConfig buildV2Config(Context context,Map map) {
        String appId = map.get("appId").toString();
        String appName = map.get("appName").toString();
        boolean debug = (boolean) map.get("debug");
        boolean useTextureView = (boolean) map.get("useTextureView");

        /**
         * GMConfigUserInfoForSegment设置流量分组的信息
         * 注意：
         * 1、请每次都传入新的info对象
         * 2、字符串类型的值只能是大小写字母，数字，下划线，连字符，字符个数100以内 ( [A-Za-z0-9-_]{1,100} ) ，不符合规则的信息将被过滤掉，不起作用。
         */
        GMConfigUserInfoForSegment userInfo = new GMConfigUserInfoForSegment();

        Map<String, Object> initConfig = new HashMap<>();
        initConfig.put("1111", "22222");

        return new GMAdConfig.Builder()
                .setAppId(appId)
                .setAppName(appName)
                .setDebug(debug)
                .setOpenAdnTest(false)
                .setPangleOption(new GMPangleOption.Builder()
                        .setIsPaid(false)
                        .setTitleBarTheme(GMAdConstant.TITLE_BAR_THEME_DARK)
                        .setAllowShowPageWhenScreenLock(true)///pangle设置是否允许落地页出现在锁屏上面：true允许、false禁止。默认为false禁止
                        .setDirectDownloadNetworkType(GMAdConstant.NETWORK_STATE_WIFI, GMAdConstant.NETWORK_STATE_4G)
                        .setIsUseTextureView(useTextureView)
                        .build())
                .build();
    }

    public static String getAndroidId(Context context) {
        String androidId = null;
        try {
            androidId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return androidId;
    }

}
