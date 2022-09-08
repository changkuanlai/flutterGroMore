//
//  BannerAdView.swift
//  gromore_ad
//
//  Created by ywsj on 2022/7/21.
//

import Foundation
import Flutter
import ABUAdSDK

public class BannerAdView : NSObject,FlutterPlatformView{
    
   
    private var bannerAd : ABUBannerAd?
    private var channel : FlutterMethodChannel?
    private var container : UIView
    let mCodeId :String
    var expressViewWidth : Int
    let expressViewHeight :Int
    let expressTime : Int?
    init(_ frame : CGRect,binaryMessenger: FlutterBinaryMessenger , id : Int64, params :Any?) {
        self.container = UIView(frame: frame)
        let dict = params as! NSDictionary
        self.expressViewWidth = dict.value(forKey: "expressViewWidth") as! Int
        self.expressViewHeight = dict.value(forKey: "expressViewHeight") as! Int
        self.mCodeId = (dict.value(forKey: "codeId") as! String)
        self.expressTime = dict.value(forKey: "expressTime") as? Int
        super.init()
        self.channel = FlutterMethodChannel.init(name: FlutterAdConfig.view.bannerAdView + "_" + String(id), binaryMessenger: binaryMessenger)
        self.loadBannerExpressAd()
    }
    
    
    public func view() -> UIView {
        return container
    }
    
    private func loadBannerExpressAd(){
        
        let ad = ABUBannerAd(adUnitID: self.mCodeId, rootViewController: UIApplication.shared.keyWindow!.rootViewController!, adSize: CGSize(width: expressViewWidth, height: expressViewHeight))
        ad.delegate = self
        ad.startMutedIfCan = true
        self.bannerAd = ad
        if(ABUAdSDKManager.configDidLoad()){
            self.bannerAd?.loadData()
        }else{
            weak var weakSelf = self
            ABUAdSDKManager.addConfigLoadSuccessObserver(self) { _Arg in
                weakSelf?.bannerAd?.loadData()
            }
        }
    }
    
}

extension BannerAdView:ABUBannerAdDelegate{
    /// banner广告加载成功回调
    /// @param bannerAd 广告操作对象
    /// @param bannerView 广告视图
    public func bannerAdDidLoad(_ bannerAd: ABUBannerAd, bannerView: UIView) {
        LogUtil.logInstance.printLog(message: "bannerAd 加载成功")
        self.container.frame.size = self.bannerAd?.adSize ?? CGSize.zero
        self.container.addSubview(bannerView)
    }
    /// 广告加载失败回调
    /// @param bannerAd 广告操作对象
    /// @param error 错误信息
    public func bannerAd(_ bannerAd: ABUBannerAd, didLoadFailWithError error: Error?) {
        LogUtil.logInstance.printLog(message: "bannerAd 加载失败 "+String(error.debugDescription))
        self.channel?.invokeMethod("onAdFail", arguments:String(error.debugDescription))
    }
   
    /// 广告展示回调
    /// @param bannerAd 广告操作对象
    /// @param bannerView 广告视图
    public func bannerAdDidBecomeVisible(_ bannerAd: ABUBannerAd, bannerView: UIView) {
        LogUtil.logInstance.printLog(message: "bannerAd 开始显示")
        self.channel?.invokeMethod("onAdShow", arguments: "信息流显示")
    }
 
    /// 广告点击事件回调
    /// @param ABUBannerAd 广告操作对象
    /// @param bannerView 广告视图
    public func bannerAdDidClick(_ ABUBannerAd: ABUBannerAd, bannerView: UIView) {
        LogUtil.logInstance.printLog(message: "bannerAd 被点击了")
        self.channel?.invokeMethod("onAdClick", arguments: "开屏广告点击")
    }

    /// 广告关闭回调
    /// @param ABUBannerAd 广告操作对象
    /// @param bannerView 广告视图
    /// @param filterwords 不喜欢广告的原因，由adapter开发者配置，可能为空
    public func bannerAdDidClosed(_ ABUBannerAd: ABUBannerAd, bannerView: UIView, dislikeWithReason filterwords: [[AnyHashable : Any]]?) {
        self.channel?.invokeMethod("onAdDislike", arguments: "点击不喜欢")
    }
   
 
}
