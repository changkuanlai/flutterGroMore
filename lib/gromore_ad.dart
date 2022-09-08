import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:gromore_ad/splashad/splash_ad_view.dart';

import 'bannerad/banner_ad_view.dart';
import 'flutter_ad_callback.dart';
import 'flutter_ad_code.dart';
import 'native/native_ad_view.dart';

class GromoreAd {
  static const MethodChannel _channel =
  MethodChannel('com.disk.native.receive/gromore_ad');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// # SDK注册初始化
  ///[androidAppId] 穿山甲广告 Android appid 必填
  ///
  ///[androidAppId] 穿山甲广告 ios appid 必填
  ///
  ///[useTextureView] 使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView 选填
  ///
  ///[appname] 必填
  ///
  ///[allowShowNotify] 是否允许sdk展示通知栏提示 选填
  ///
  ///
  ///[debug] 是否显示debug日志
  ///
  /// [feedData] 初始化拉取feed数据
  ///
  ///[directDownloadNetworkType] 允许直接下载的网络状态集合 选填
  ///
  static Future<bool> register({
    required String appId,
    bool? useTextureView,
    required String appName,
    bool? allowShowNotify,
    bool? debug,
    List<int>? directDownloadNetworkType,
  }) async {
    return await _channel.invokeMethod("register", {
      "appId": appId,
      "useTextureView": useTextureView ?? false,
      "appName": appName,
      "allowShowNotify": allowShowNotify ?? true,
      "debug": debug ?? false,
      "directDownloadNetworkType": directDownloadNetworkType ??
          [
            FlutterAdNetCode.networkStateMobile,
            FlutterAdNetCode.networkState2g,
            FlutterAdNetCode.networkState3g,
            FlutterAdNetCode.networkState4g,
            FlutterAdNetCode.networkStateWifi
          ]
    });
  }

  /// # 获取sdk版本
  static Future<String> getSdkVersion() async {
    return await _channel.invokeMethod("getSDKVersion");
  }

  /// # 请求权限
  static Future<void> requestPermissionIfNecessary() async {
    return await _channel.invokeMethod("requestPermission");
  }

  /// # 开屏广告
  ///
  /// [mIsExpress] 是否使用个性化模版  设定widget宽高
  ///
  /// [codeId] codeId 开屏广告广告id 必填
  ///
  ///
  /// [expressViewWidth] 期望view 宽度 dp 选填 mIsExpress=true必填
  ///
  /// [expressViewHeight] 期望view高度 dp 选填 mIsExpress=true必填
  ///
  /// [FlutterUnionAdSplashCallBack] 开屏广告回调
  ///
  /// [adLoadType]用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，[FlutterUnionadSplashType]
  static Widget splashAdView({bool? mIsExpress,
    required String codeId,
    double? expressViewWidth,
    double? expressViewHeight,
    int? adLoadType,
    bool? forceLoadBottom,
    FlutterSplashCallBack? callBack}) {
    return SplashAdView(
      mIsExpress: mIsExpress ?? false,
      codeId: codeId,
      expressViewWidth: expressViewWidth ?? 0.0,
      expressViewHeight: expressViewHeight ?? 0.0,
      adLoadType: adLoadType ?? FlutterAdSplashType.load,
      forceLoadBottom: forceLoadBottom ?? false,
      callBack: callBack,
    );
  }

  /// # 激励视频广告预加载
  /// [codeId]   激励视频广告id 必填
  ///
  /// [supportDeepLink] 是否支持 DeepLink 选填
  ///
  /// [expressViewWidth] 期望view 宽度 dp 必填
  ///
  /// [expressViewHeight] 期望view高度 dp 必填
  ///
  static Future<bool> showRewardVideoAd({
    bool? mIsExpress,
    required String codeId,
    required String rewardName,
    int rewardAmount = 1,
    required String userID,
    int? orientation,
    double volume = 0.0,
  }) async {
    return await _channel.invokeMethod("showRewardVideoAd", {
      "codeId": codeId,
      "rewardName": rewardName,
      "rewardAmount": rewardAmount,
      "userID": userID,
      "orientation": orientation ?? 1,
      "volume": volume
    });
  }

  /// # 预加载新模板渲染插屏
  ///分为全屏和插屏，全屏和插屏场景下开发者都可以选择投放的广告类型，分别为图片+视频、仅视频、仅图片。
  static Future<bool> showFullScreenVideoAdInteraction({
    required String codeId,
    int? orientation,
    required String rewardName,
    int rewardAmount = 1,
    required String userID,
    int? expressViewWidth,
    int? expressViewHeight,
    double volume = 0.0,
  }) async {
    return await _channel.invokeMethod("showFullScreenVideoAdInteraction", {
      "codeId": codeId,
      "orientation": orientation ?? 1,
      "expressViewWidth": expressViewWidth ?? 750,
      "expressViewHeight": expressViewHeight ?? 1080,
      "rewardName": rewardName,
      "rewardAmount": rewardAmount,
      "userID": userID,
      "volume": volume
    });
  }

  /// # 模板信息流广告
  ///
  /// [codeId] android 信息流广告id 必填
  ///
  /// [styleType] 原生和模板2种不同渲染
  ///
  /// [expressViewWidth] 期望view 宽度 dp 必填
  ///
  /// [expressViewHeight] 期望view高度 dp 必填 模板高度0 自适应
  ///
  /// [FlutterUnionAdNativeCallBack] 信息流广告回调
  ///
  static Widget nativeAdView({required String codeId,
    required int expressViewWidth,
    required int expressViewHeight,
    required int adCount,
    int styleType = FlutterAdStyleType.typeExpressAd,
    FlutterAdNativeCallBack? callBack}) {
    return NativeAdView(
      codeId: codeId,
      styleType: styleType,
      expressViewWidth: expressViewWidth,
      expressViewHeight: expressViewHeight,
      adCount: adCount,
      callBack: callBack,
    );
  }

  /// # banner广告
  ///
  ///
  /// [expressViewWidth] 期望view宽度 dp 必填
  ///
  /// [expressViewHeight] 期望view高度 dp 必填
  ///
  /// [FlutterUnionAdBannerCallBack]  banner广告回调
  ///
  static Widget bannerAdView({required String codeId,
    required int expressViewWidth,
    required int expressViewHeight,
    FlutterAdBannerCallBack? callBack}) {
    return BannerAdView(
      codeId: codeId,
      expressViewWidth: expressViewWidth,
      expressViewHeight: expressViewHeight,
      callBack: callBack,
    );
  }

  /// 加载信息流初始化
  static Future<bool> loadAtiveAdInitData({
    required String codeId,
    required int expressViewWidth,
    required int expressViewHeight,
    int adCount = 1,
    int styleType = FlutterAdStyleType.typeExpressAd,
  }) async {
    return await _channel.invokeMethod("loadActiveAdInitData", {
      "codeId": codeId,
      "expressViewWidth": expressViewWidth,
      "expressViewHeight": expressViewHeight,
      "styleType": styleType,
      "adCount": adCount
    });
  }
}
