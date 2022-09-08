//
//  SplashAdView.swift
//  gromore_ad
//
//  Created by ywsj on 2022/7/20.
//

import Foundation
import Flutter
import ABUAdSDK
import UIKit
public class SplashAdView:NSObject,FlutterPlatformView{
    private var container : UIView?
    private var channel : FlutterMethodChannel?
    private let splashAd : ABUSplashAd
    var frame: CGRect;
    var error: NSError?
    let adUnitId:String
    let expressViewWidth : Float
    let expressViewHeight :Float
    
      init(_ frame : CGRect,binaryMessenger: FlutterBinaryMessenger , id : Int64, params :Any?) {
        self.frame = frame
        let dict = params as! NSDictionary
        self.expressViewWidth = Float(dict.value(forKey: "expressViewWidth") as! Double)
        self.expressViewHeight = Float(dict.value(forKey: "expressViewHeight") as! Double)
        self.container = UIView(frame: frame)
        self.adUnitId = dict.value(forKey: "codeId")  as! String
          self.splashAd = ABUSplashAd(adUnitID: self.adUnitId)
        super.init()
        self.channel = FlutterMethodChannel.init(name:FlutterAdConfig.view.splashAdView+"_" + String(id),binaryMessenger:  binaryMessenger);
        self.loadSplash()
    }
    
    public func view() -> UIView {
        return self.container!
    }
    func loadSplash(){
        let size : CGSize
        if(self.expressViewWidth == 0 || self.expressViewHeight == 0){
            size = CGSize(width: MyUtils.getScreenSize().width, height: MyUtils.getScreenSize().height)
        }else{
            size = CGSize(width: CGFloat(self.expressViewWidth), height: CGFloat(self.expressViewHeight))
        }
        container!.frame.size = size;
    
        let userData =  ABUSplashUserData()
        userData.adnName = "pangle";
        userData.appID = FlutterAdConfig.appID;
        userData.rit = "887853704";
        
        self.splashAd.delegate = self;
        self.splashAd.rootViewController = UIApplication.shared.keyWindow?.rootViewController;
        self.splashAd.setUserData(userData, error: &error);
        self.splashAd.loadData();
     }
 
     
    
    private func disposeView() {
        self.container?.removeFromSuperview();
         self.splashAd.destoryAd()
     }
    
    
    
    
}
extension SplashAdView:ABUSplashAdDelegate{
    /// 广告加载成功回调
    public func splashAdDidLoad(_ splashAd: ABUSplashAd) {
        LogUtil.logInstance.printLog(message: "加载完成")
        self.splashAd.show(in: UIApplication.shared.keyWindow!);
    }
    /// 广告加载失败回调
    public func splashAd(_ splashAd: ABUSplashAd, didFailWithError error: Error?) {
        LogUtil.logInstance.printLog(message: "加载失败")
        self.channel?.invokeMethod("onAdFail", arguments:String(error.debugDescription))
    }
    
    /// 广告关闭
    public func splashAdDidClose(_ splashAd: ABUSplashAd) {
        self.disposeView()
        self.channel?.invokeMethod("onAdClick", arguments: "开屏广告点击")
    }
    /// 广告点击
    public func splashAdDidClick(_ splashAd: ABUSplashAd) {
        self.channel?.invokeMethod("onAdSkip", arguments: "")
    }
    ///广告展示
    public func splashAdWillVisible(_ splashAd: ABUSplashAd) {
        LogUtil.logInstance.printLog(message: "显示")
        self.channel?.invokeMethod("onAdShow", arguments: "")
    }
    
    /// 倒计时
    public func splashAdCountdown(toZero splashAd: ABUSplashAd) {
        LogUtil.logInstance.printLog(message: "倒计时结束")
        self.channel?.invokeMethod("onAdDismiss", arguments: "开屏广告倒计时结束")
        self.disposeView()
    }
}
