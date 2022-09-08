import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../flutter_ad_callback.dart';
import '../flutter_ad_code.dart';

class BannerAdView extends StatefulWidget {
  final String codeId;
  final int expressViewWidth;
  final int expressViewHeight;
  final FlutterAdBannerCallBack? callBack;

  const BannerAdView(
      {Key? key,
      required this.codeId,
      required this.expressViewWidth,
      required this.expressViewHeight,
      this.callBack})
      : super(key: key);

  @override
  _BannerAdViewState createState() => _BannerAdViewState();
}

class _BannerAdViewState extends State<BannerAdView> {
  final String _viewType = "com.xiaofeiji.flutter_ad/BannerAdView";

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
        height: _height.toDouble(),
        child: AndroidView(
          viewType: _viewType,
          creationParams: {
            "codeId": widget.codeId,
            "expressViewWidth": widget.expressViewWidth,
            "expressViewHeight": widget.expressViewHeight,
          },
          onPlatformViewCreated: _registerChannel,
          creationParamsCodec: const StandardMessageCodec(),
        ),
      );
    } else if (defaultTargetPlatform == TargetPlatform.iOS) {
      return SizedBox(
        width: _width.toDouble(),
        height: _height.toDouble(),
        child: UiKitView(
          viewType: _viewType,
          creationParams: {
            "codeId": widget.codeId,
            "expressViewWidth": widget.expressViewWidth,
            "expressViewHeight": widget.expressViewHeight,
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
        if (mounted) {
          setState(() {
            _isShowAd = true;
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
        if (widget.callBack != null) {
          widget.callBack?.onDislike!(call.arguments);
        }
        break;
      case FlutterAdMethod.onClick:
        widget.callBack?.onClick!();
        break;
    }
  }
}
