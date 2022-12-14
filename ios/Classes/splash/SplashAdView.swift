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
    /// ????????????????????????
    public func splashAdDidLoad(_ splashAd: ABUSplashAd) {
        LogUtil.logInstance.printLog(message: "????????????")
        self.splashAd.show(in: UIApplication.shared.keyWindow!);
    }
    /// ????????????????????????
    public func splashAd(_ splashAd: ABUSplashAd, didFailWithError error: Error?) {
        LogUtil.logInstance.printLog(message: "????????????")
        self.channel?.invokeMethod("onAdFail", arguments:String(error.debugDescription))
    }
    
    /// ????????????
    public func splashAdDidClose(_ splashAd: ABUSplashAd) {
        self.disposeView()
        self.channel?.invokeMethod("onAdClick", arguments: "??????????????????")
    }
    /// ????????????
    public func splashAdDidClick(_ splashAd: ABUSplashAd) {
        self.channel?.invokeMethod("onAdSkip", arguments: "")
    }
    ///????????????
    public func splashAdWillVisible(_ splashAd: ABUSplashAd) {
        LogUtil.logInstance.printLog(message: "??????")
        self.channel?.invokeMethod("onAdShow", arguments: "")
    }
    
    /// ?????????
    public func splashAdCountdown(toZero splashAd: ABUSplashAd) {
        LogUtil.logInstance.printLog(message: "???????????????")
        self.channel?.invokeMethod("onAdDismiss", arguments: "???????????????????????????")
        self.disposeView()
    }
}
