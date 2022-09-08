import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../flutter_ad_callback.dart';
import '../flutter_ad_code.dart';

class SplashAdView extends StatefulWidget {
  final bool mIsExpress;
  final String codeId;
  final double expressViewWidth;
  final double expressViewHeight;
  final int? adLoadType;
  final bool forceLoadBottom;
  final FlutterSplashCallBack? callBack;

  const SplashAdView(
      {Key? key,
      required this.mIsExpress,
      required this.codeId,
      required this.expressViewWidth,
      required this.expressViewHeight,
      required this.adLoadType,
      this.callBack, this.forceLoadBottom = false})
      : super(key: key);

  @override
  _SplashAdViewState createState() => _SplashAdViewState();
}

class _SplashAdViewState extends State<SplashAdView> {
  final String _viewType = "com.xiaofeiji.flutter_ad/SplashAdView";

  MethodChannel? _channel;

  //广告是否显示
  bool _isShowAd = true;

  @override
  void initState() {
    super.initState();
    _isShowAd = true;
  }

  @override
  Widget build(BuildContext context) {
    if (!_isShowAd) {
      return Container();
    }
    if (defaultTargetPlatform == TargetPlatform.android) {
      return SizedBox(
        width: widget.mIsExpress
            ? widget.expressViewWidth
            : MediaQuery.of(context).size.width,
        height: widget.mIsExpress
            ? widget.expressViewHeight
            : MediaQuery.of(context).size.height,
        child: AndroidView(
          viewType: _viewType,
          creationParams: {
            "mIsExpress": widget.mIsExpress,
            "codeId": widget.codeId,
            "expressViewWidth": widget.expressViewWidth,
            "expressViewHeight": widget.expressViewHeight,
            "adLoadType": widget.adLoadType,
            "forceLoadBottom":widget.forceLoadBottom,
          },
          onPlatformViewCreated: _registerChannel,
          creationParamsCodec: const StandardMessageCodec(),
        ),
      );
    } else if (defaultTargetPlatform == TargetPlatform.iOS) {
      return SizedBox(
        width: widget.mIsExpress
            ? widget.expressViewWidth
            : MediaQuery.of(context).size.width,
        height: widget.mIsExpress
            ? widget.expressViewHeight
            : MediaQuery.of(context).size.height,
        child: UiKitView(
          viewType: _viewType,
          creationParams: {
            "mIsExpress": widget.mIsExpress,
            "codeId": widget.codeId,
            "expressViewWidth": widget.expressViewWidth,
            "expressViewHeight": widget.expressViewHeight,
            "adLoadType": widget.adLoadType,
            "forceLoadBottom":widget.forceLoadBottom,
          },
          onPlatformViewCreated: _registerChannel,
          creationParamsCodec: const StandardMessageCodec(),
        ),
      );
    } else {
      return Container();
    }
  }

  //注册cannel
  void _registerChannel(int id) {
    _channel = MethodChannel("${_viewType}_$id");
    _channel?.setMethodCallHandler(_platformCallHandler);
  }

  //监听原生view传值
  Future<dynamic> _platformCallHandler(MethodCall call) async {
    switch (call.method) {
      //显示广告
      case FlutterAdMethod.onShow:
        if (widget.callBack != null) {
          widget.callBack?.onShow!();
        }
        break;
      //广告加载失败
      case FlutterAdMethod.onFail:
        if (mounted) {
          setState(() {
            _isShowAd = false;
          });
        }
        if (widget.callBack != null) {
          widget.callBack?.onFail!(call.arguments);
        }
        break;
      //开屏广告点击
      case FlutterAdMethod.onClick:
        if (widget.callBack != null) {
          widget.callBack?.onClick!();
        }
        break;
      //开屏广告跳过
      case FlutterAdMethod.onSkip:
        if (widget.callBack != null) {
          widget.callBack?.onSkip!();
        }
        break;
      //开屏广告倒计时结束
      case FlutterAdMethod.onFinish:
        if (widget.callBack != null) {
          widget.callBack?.onFinish!();
        }
        break;
      //开屏广告加载超时
      case FlutterAdMethod.onTimeOut:
        if (mounted) {
          setState(() {
            _isShowAd = false;
          });
        }
        if (widget.callBack != null) {
          widget.callBack?.onTimeOut!();
        }
        break;
    }
  }
}
