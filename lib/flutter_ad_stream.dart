import 'dart:async';

import 'package:flutter/services.dart';
 
import 'flutter_ad_callback.dart';
import 'flutter_ad_code.dart';

const EventChannel adEventEvent =
    EventChannel("com.xiaofeiji.flutter_ad/adevent");

class FlutterAdStream {
  ///注册stream监听原生返回的信息
  static StreamSubscription initAdStream(
      {FlutterAdFullCallBack? flutterAdFullVideoCallBack,
      FlutterAdInteractionCallBack? flutterAdInteractionCallBack,
      FlutterAdNewInteractionCallBack?
          flutterAdNewInteractionCallBack,
      FlutterAdRewardAdCallBack? flutterAdRewardAdCallBack}) {
    StreamSubscription _adStream =
        adEventEvent.receiveBroadcastStream().listen((data) {
      switch (data[FlutterAdType.adType]) {

        ///全屏广告
        case FlutterAdType.fullVideoAd:
          switch (data[FlutterAdMethod.onAdMethod]) {
            case FlutterAdMethod.onShow:
              if(flutterAdFullVideoCallBack?.onShow != null){
                flutterAdFullVideoCallBack?.onShow!();
              }
              break;
            case FlutterAdMethod.onSkip:
              if(flutterAdFullVideoCallBack?.onSkip != null){
                flutterAdFullVideoCallBack?.onSkip!();
              }
              break;
            case FlutterAdMethod.onFinish:
              if(flutterAdFullVideoCallBack?.onFinish != null){
                flutterAdFullVideoCallBack?.onFinish!();
              }
              break;
            case FlutterAdMethod.onClose:
              if(flutterAdFullVideoCallBack?.onClose != null){
                flutterAdFullVideoCallBack?.onClose!();
              }
              break;
            case FlutterAdMethod.onFail:
              if(flutterAdFullVideoCallBack?.onFail != null){
                flutterAdFullVideoCallBack?.onFail!(data["error"]);
              }
              break;
            case FlutterAdMethod.onClick:
              if(flutterAdFullVideoCallBack?.onClick != null){
                flutterAdFullVideoCallBack?.onClick!();
              }
              break;
          }
          break;

        ///插屏广告
        case FlutterAdType.interactAd:
          switch (data[FlutterAdMethod.onAdMethod]) {
            case FlutterAdMethod.onShow:
              if(flutterAdInteractionCallBack?.onShow != null){
                flutterAdInteractionCallBack?.onShow!();
              }
              break;
            case FlutterAdMethod.onDislike:
              if(flutterAdInteractionCallBack?.onDislike != null){
                flutterAdInteractionCallBack?.onDislike!(data["message"]);
              }
              break;
            case FlutterAdMethod.onClose:
              if(flutterAdInteractionCallBack?.onClose != null){
                flutterAdInteractionCallBack?.onClose!();
              }
              break;
            case FlutterAdMethod.onFail:
              if(flutterAdInteractionCallBack?.onFail != null){
                flutterAdInteractionCallBack?.onFail!(data["error"]);
              }
              break;
            case FlutterAdMethod.onClick:
              if(flutterAdInteractionCallBack?.onClick != null){
                flutterAdInteractionCallBack?.onClick!();
              }
              break;
          }
          break;

        /// 新模板渲染插屏
        case FlutterAdType.fullScreenVideoAdInteraction:
          switch (data[FlutterAdMethod.onAdMethod]) {
            case FlutterAdMethod.onShow:
              if(flutterAdNewInteractionCallBack?.onShow != null){
                flutterAdNewInteractionCallBack?.onShow!();
              }
              break;
            case FlutterAdMethod.onClose:
              if(flutterAdNewInteractionCallBack?.onClose != null){
                flutterAdNewInteractionCallBack?.onClose!();
              }
              break;
            case FlutterAdMethod.onFail:
              if(flutterAdNewInteractionCallBack?.onFail != null){
                flutterAdNewInteractionCallBack?.onFail!(data["error"]);
              }
              break;
            case FlutterAdMethod.onClick:
              if(flutterAdNewInteractionCallBack?.onClick != null){
                flutterAdNewInteractionCallBack?.onClick!();
              }
              break;
            case FlutterAdMethod.onSkip:
              if(flutterAdNewInteractionCallBack?.onSkip != null){
                flutterAdNewInteractionCallBack?.onSkip!();
              }
              break;
            case FlutterAdMethod.onFinish:
              if(flutterAdNewInteractionCallBack?.onFinish != null){
                flutterAdNewInteractionCallBack?.onFinish!();
              }
              break;
            case FlutterAdMethod.onReady:
              if(flutterAdNewInteractionCallBack?.onReady != null){
                flutterAdNewInteractionCallBack?.onReady!();
              }
              break;
            case FlutterAdMethod.onUnReady:
              if(flutterAdNewInteractionCallBack?.onUnReady != null){
                flutterAdNewInteractionCallBack?.onUnReady!();
              }
              break;
          }
          break;

        ///激励广告
        case FlutterAdType.rewardedVideoAd:
          switch (data[FlutterAdMethod.onAdMethod]) {
            case FlutterAdMethod.onShow:
              if(flutterAdRewardAdCallBack?.onShow != null){
                flutterAdRewardAdCallBack?.onShow!();
              }
              break;
            case FlutterAdMethod.onSkip:
              if(flutterAdRewardAdCallBack?.onSkip != null){
                flutterAdRewardAdCallBack?.onSkip!();
              }
              break;
            case FlutterAdMethod.onClose:
              if(flutterAdRewardAdCallBack?.onClose != null){
                flutterAdRewardAdCallBack?.onClose!();
              }
              break;
            case FlutterAdMethod.onFail:
              if(flutterAdRewardAdCallBack?.onFail != null){
                flutterAdRewardAdCallBack?.onFail!(data["error"]);
              }
              break;
            case FlutterAdMethod.onClick:
              if(flutterAdRewardAdCallBack?.onClick != null){
                flutterAdRewardAdCallBack?.onClick!();
              }
              break;
            case FlutterAdMethod.onVerify:
              if(flutterAdRewardAdCallBack?.onVerify != null){
                flutterAdRewardAdCallBack?.onVerify!(
                    data["rewardVerify"],
                    data["rewardAmount"] ?? 0,
                    data["rewardName"] ?? "",
                    data["errorCode"] ?? 0,
                    data["error"] ?? "");
              }
              break;
            case FlutterAdMethod.onRewardArrived:
              if(flutterAdRewardAdCallBack?.onRewardArrived != null){
                flutterAdRewardAdCallBack?.onRewardArrived!(
                    data["rewardVerify"],
                    data["rewardType"],
                    data["rewardAmount"] ?? 0,
                    data["rewardName"] ?? "",
                    data["errorCode"] ?? 0,
                    data["error"] ?? "",
                    data["propose"] ?? 1);
              }
              break;
            case FlutterAdMethod.onReady:
              if(flutterAdRewardAdCallBack?.onReady != null){
                flutterAdRewardAdCallBack?.onReady!();
              }
              break;
            case FlutterAdMethod.onUnReady:
              if(flutterAdRewardAdCallBack?.onUnReady != null){
                flutterAdRewardAdCallBack?.onUnReady!();
              }
              break;
          }
      }
    });
    return _adStream;
  }

  static void deleteAdStream(StreamSubscription stream) {
    stream.cancel();
  }
}
