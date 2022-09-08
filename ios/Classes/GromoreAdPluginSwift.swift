
import Flutter
import ABUAdSDK
import AppTrackingTransparency

public class GromoreAdPluginSwift: NSObject, FlutterPlugin {
    
  public static var event : FlutterAdEnentPlugin?
 var reward:RewardedVideoAd?
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "com.disk.native.receive/gromore_ad", binaryMessenger: registrar.messenger())
    let instance = GromoreAdPluginSwift()
    registrar.addMethodCallDelegate(instance, channel: channel)
    //注册广告view
    FlutterAdViewPlugin.register(viewRegistrar:registrar)
    //注册event
    event = FlutterAdEnentPlugin.init(registrar)
   }
    
  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    //注册初始化
    case "register":
        let param = call.arguments as! NSDictionary
        let appId = param.value(forKey: "appId") as! String;
        let debug = param.value(forKey: "debug") as! Bool
        ABUAdSDKManager.setupSDK(withAppId: appId) {  config in
            config.logEnable = debug
            result(true)
             return config
        };
 
        break
    //获取sdk版本号
    case "getSDKVersion":
        result(ABUAdSDKManager.sdkVersion)
        break
    //获取权限AAT ios14以上才有
    case "requestPermissionIfNecessary":
        if #available(iOS 14, *) {
            ATTrackingManager.requestTrackingAuthorization(completionHandler: { status in

                result(status.rawValue)
            })
        } else {
            result(3)
        }
    //显示激励广告
    case "showRewardVideoAd":
        let param = call.arguments as! NSDictionary
        let mAdId = param.value(forKey: "codeId") as! String;
        let vc = RewardedVideoAd()
        vc.codeId = mAdId
        vc.view.backgroundColor = .white
        UIApplication.shared.keyWindow?.rootViewController?.present(vc, animated: false, completion: nil)
         result(true)
        break
        //插屏广告
    case "interactionAd":
        let param = call.arguments as! NSDictionary

        result(true)
        break
        //显示新模版渲染插屏广告
    case "showFullScreenVideoAdInteraction":
        let param = call.arguments as! NSDictionary
        FullScreenVideoAdInteraction().showFullScreenVideoAdInteraction(params: param)
        result(false)
        break
    default:
        result(FlutterMethodNotImplemented)
    }
  }
}

