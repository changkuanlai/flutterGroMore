//
//  NativeAdView.swift
//  gromore_ad
//
//  Created by ywsj on 2022/7/21.
//

import Foundation
import Flutter
import ABUAdSDK

public class NativeAdView : NSObject,FlutterPlatformView{
  
    private let container : UIView
    private var channel : FlutterMethodChannel?
    private var adView : ABUNativeAdView?
    //广告需要的参数
    let mCodeId :String?
    var expressViewWidth : Int?
    let expressViewHeight :Int?
    lazy var adManager : ABUNativeAdsManager = {
        let slot = ABUAdUnit()
        slot.id = self.mCodeId ?? ""
        slot.adSize = CGSize(width: self.expressViewWidth!, height: self.expressViewHeight ?? 0)
        let m = ABUNativeAdsManager(slot: slot)
        m.rootViewController = UIApplication.shared.keyWindow?.rootViewController
        m.delegate = self
        m.startMutedIfCan = true
        m.getExpressAdIfCan = true
        return m
    }()
    
    init(_ frame : CGRect,binaryMessenger: FlutterBinaryMessenger , id : Int64, params :Any?) {
        self.container = UIView();
        let dict = params as! NSDictionary
        self.expressViewWidth = dict.value(forKey: "expressViewWidth") as? Int
        self.expressViewHeight = dict.value(forKey: "expressViewHeight") as? Int
        self.mCodeId = (dict.value(forKey: "codeId") as? String)
        if(self.expressViewWidth == 0){
            self.expressViewWidth = Int(MyUtils.getScreenSize().width)
        }
        super.init()
        self.channel = FlutterMethodChannel(name: FlutterAdConfig.view.nativeAdView+"_" + String(id), binaryMessenger: binaryMessenger)
        loadNativeAds()
    }
    
    public func view() -> UIView {
        return container
    }
    func loadNativeAds(){
        if(ABUAdSDKManager.configDidLoad()){
            self.adManager.loadAdData(withCount: 1)
        }else{
            weak var weakSelf = self
            ABUAdSDKManager.addConfigLoadSuccessObserver(self) { _Arg in
                weakSelf?.adManager .loadAdData(withCount: 1)
            }
        }
    }
    
    private func removeAllView(){
        self.container.removeFromSuperview()
    }
    
    private func disposeView() {
        self.removeAllView()
    }
    
}
extension NativeAdView:ABUNativeAdsManagerDelegate{
    
    /// Native 广告加载成功回调
    /// @param adsManager 广告管理对象
    /// @param nativeAdViewArray 广告视图，GroMore包装视图对象组，包括模板广告和自渲染广告
    public func nativeAdsManagerSuccess(toLoad adsManager: ABUNativeAdsManager, nativeAds nativeAdViewArray: [ABUNativeAdView]?) {
        
        LogUtil.logInstance.printLog(message: "nativeAdsManagerSuccess 加载成功")
        self.container.frame.size = self.adManager.adSize;
        self.adView = nativeAdViewArray?.first
        self.adView?.delegate = self
        self.adView?.rootViewController = UIApplication.shared.keyWindow?.rootViewController
        self.adView?.render()
    }
    /// Native 广告加载失败回调
    /// @param adsManager 广告管理对象
    /// @param error  加载出错信息
    public func nativeAdsManager(_ adsManager: ABUNativeAdsManager, didFailWithError error: Error?) {
        LogUtil.logInstance.printLog(message: "nativeAd 加载失败")
        self.channel?.invokeMethod("onAdFail", arguments:String(error.debugDescription))
    }
    
}
extension NativeAdView:ABUNativeAdViewDelegate{
    
    /// 模板广告渲染成功回调，非模板广告不会回调，模板广告可能不会回调
    /// @param nativeExpressAdView 模板广告对象
    public func nativeAdExpressViewRenderSuccess(_ nativeExpressAdView: ABUNativeAdView) {
        container.addSubview(self.adView!)
        LogUtil.logInstance.printLog(message: "nativeAd 开始显示")
        self.channel?.invokeMethod("onAdShow", arguments: "信息流显示")
    }
    /// 模板广告渲染成功回调，非模板广告不会回调，模板广告可能不会回调
    /// @param nativeExpressAdView 模板广告对象
    /// @param error 渲染出错原因
    public func nativeAdExpressViewRenderFail(_ nativeExpressAdView: ABUNativeAdView, error: Error?) {
        LogUtil.logInstance.printLog(message: "nativeAd 加载失败")
        self.channel?.invokeMethod("onAdFail", arguments:String(error.debugDescription))
    }
    /// 广告展示回调，不区分模板与非模板
    /// @param nativeAdView 广告对象
    public func nativeAdDidBecomeVisible(_ nativeAdView: ABUNativeAdView) {
        
    }
    /// 广告点击事件回调
    /// @param nativeAdView 广告对象
    /// @param view 广告展示视图
    public func nativeAdVideoDidClick(_ nativeAdView: ABUNativeAdView?) {
        LogUtil.logInstance.printLog(message: "nativeAd 被点击了")
        self.channel?.invokeMethod("onAdClick", arguments: "开屏广告点击")
     }

    /// 模板广告点击关闭时触发
    /// @param nativeAdView 广告视图
    /// @param filterWords 广告关闭原因，adapter开发者透传数据
    public func nativeAdExpressViewDidClosed(_ nativeAdView: ABUNativeAdView?, closeReason filterWords: [[AnyHashable : Any]]?) {
        self.channel?.invokeMethod("onAdDislike", arguments: "开屏广告点击")
     }
 
}
 
