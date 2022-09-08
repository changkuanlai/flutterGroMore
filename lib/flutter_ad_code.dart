

///网络类型
class FlutterAdNetCode {
  static const int networkStateMobile = 1;
  static const int networkState2g = 2;
  static const int networkState3g = 3;
  static const int networkStateWifi = 4;
  static const int networkState4g = 5;
}

///激励视频方向
class FlutterAdOrientation {
  ///竖屏
  static const int vertical = 1;

  ///横屏
  static const int horizontal = 2;
}

///数据类型
class FlutterAdType {
  static const String adType = "adType";

  ///激励广告
  static const String rewardedVideoAd = "rewardedVideoAd";

  ///全屏视频广告
  static const String fullVideoAd = "fullVideoAd";

  ///插屏广告
  static const String interactAd = "interactionAd";

  /// 新模板渲染插屏 分为全屏和插屏，全屏和插屏场景下开发者都可以选择投放的广告类型，分别为图片+视频、仅视频、仅图片
  static const String fullScreenVideoAdInteraction =
      "fullScreenVideoAdInteraction";
}

class FlutterAdMethod {
  ///stream中 广告方法
  static const String onAdMethod = "onAdMethod";

  ///广告加载状态 view使用
  ///显示view
  static const String onShow = "onAdShow";

  ///加载失败
  static const String onFail = "onAdFail";

  ///不感兴趣
  static const String onDislike = "onAdDislike";

  ///点击
  static const String onClick = "onAdClick";

  ///视频播放
  static const String onVideoPlay = "onAdVideoPlay";

  ///视频暂停
  static const String onVideoPause = "onAdVideoPause";

  ///视频结束
  static const String onVideoStop = "onAdVideoStop";

  ///跳过
  static const String onSkip = "onAdSkip";

  ///倒计时结束
  static const String onFinish = "onAdFinish";

  ///加载超时
  static const String onTimeOut = "onAdTimeOut";

  ///广告关闭
  static const String onClose = "onAdClose";

  ///广告奖励校验
  static const String onVerify = "onAdVerify";

  //
  static const String onRewardArrived = "onAdRewardArrived";

  ///广告预加载完成
  static const String onReady = "onAdReady";

  ///广告未预加载
  static const String onUnReady = "onAdUnReady";
}

///权限请求结果
class FlutterADPermissionCode {
  ///未确定
  static const int notDetermined = 0;

  ///限制
  static const int restricted = 1;

  ///拒绝
  static const int denied = 2;

  ///同意
  static const int authorized = 3;
}

class FlutterAdDownLoadType {
  /// 对于应用的下载不做特殊处理；
  static const int downloadTypeNoPopup = 0;

  /// 应用每次下载都需要触发弹窗披露应用信息（不含跳转商店的场景），该配置优先级高于下载网络弹窗配置；
  static const int downloadTypePopup = 1;
}

class FlutterAdRewardType {
  ///基础奖励
  static const int rewardTypeDefault = 0;

  ///进阶奖励-互动
  static const int rewardTypeInteract = 1;

  ///进阶奖励-超过30s的视频播放完成
  static const int rewardTypeVideoComplete = 2;
}

class FlutterAdPersonalise {
  ///屏蔽个性化推荐广告；
  static const String close = "0";

  ///不屏蔽个性化推荐广告
  static const String open = "1";
}

class FlutterAdSplashType {
  ///未知；
  static const int unknown = 0;
  ///实时
  static const int load = 1;
  ///预览
  static const int preload = 2;
}

class FlutterAdStyleType{
    static const int typeExpressAd = 1;
    static const int typeNativeAd = 2;
}