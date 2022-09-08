import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../flutter_ad_callback.dart';
import '../flutter_ad_code.dart';

class NativeAdView extends StatefulWidget {
  final String codeId;
  final int expressViewWidth;
  final int expressViewHeight;
  final int adCount;
  final int styleType;
  final FlutterAdNativeCallBack? callBack;

  const NativeAdView(
      {Key? key,
        required this.expressViewWidth,
        required this.expressViewHeight,
        required this.adCount,
        this.callBack, required this.codeId, required this.styleType})
      : super(key: key);

  @override
  _NativeAdViewState createState() => _NativeAdViewState();
}

class _NativeAdViewState extends State<NativeAdView> {
  final String _viewType = "com.xiaofeiji.flutter_ad/NativeAdView";

  MethodChannel? _channel;

  //广告是否显示
  bool _isShowAd = true;

  //宽高
  int _width = 0;
  int _height = 0;

  @override
  void initState() {
    super.initState();
    _isShowAd = true;
    _width = widget.expressViewWidth;
    _height = widget.expressViewHeight;
  }

  @override
  Widget build(BuildContext context) {
    if (!_isShowAd) {
      return Container();
    }
    if (defaultTargetPlatform == TargetPlatform.android) {
      return SizedBox(
        width: _width.toDouble(),
        height: _height == 0 ? 1 : _height.toDouble(),//高为0的时候不会原生不会加载 默认设为0.5
        child: AndroidView(
          viewType: _viewType,
          creationParams: {
            "codeId": widget.codeId,
            "styleType": widget.styleType,
            "expressViewWidth": widget.expressViewWidth,
            "expressViewHeight": widget.expressViewHeight,
            "adCount": widget.adCount,
          },
          onPlatformViewCreated: _registerChannel,
          creationParamsCodec: const StandardMessageCodec(),
        ),
      );
    } else if (defaultTargetPlatform == TargetPlatform.iOS) {
      return SizedBox(
        width: _width.toDouble(),
        height: _height == 0 ? 0.5 : _height.toDouble(),//高为0的时候原生不会加载 默认设为0.5
        child: UiKitView(
          viewType: _viewType,
          creationParams: {
            "codeId": widget.codeId,
            "styleType": widget.styleType,
            "expressViewWidth": widget.expressViewWidth,
            "expressViewHeight": widget.expressViewHeight,
            "adCount": widget.adCount,
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
        Map map = call.arguments;
        if (mounted) {
          setState(() {
            _isShowAd = true;
            _width = (map["width"]).toDouble();
            _height = (map["height"]).toDouble();
          });
        }
        widget.callBack?.onShow!();
        break;
    //广告加载失败
      case FlutterAdMethod.onFail:
        if (mounted) {
          setState(() {
            _isShowAd = false;
          });
        }
        widget.callBack?.onFail!(call.arguments);
        break;
    //广告不感兴趣
      case FlutterAdMethod.onDislike:
        if (mounted) {
          setState(() {
            _isShowAd = false;
          });
        }
        widget.callBack?.onDislike!(call.arguments);
        break;
    //点击
      case FlutterAdMethod.onClick:
        widget.callBack?.onClick!();
        break;
    }
  }
}
