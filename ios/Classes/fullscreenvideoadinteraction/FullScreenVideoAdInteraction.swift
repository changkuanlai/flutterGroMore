//
//  FullScreenVideoAdInteraction.swift
//  gromore_ad
//
//  Created by ywsj on 2022/7/22.
//

import Foundation
import ABUAdSDK

public class FullScreenVideoAdInteraction : NSObject{

    var fsvAd : ABUFullscreenVideoAd?
    
    public func showFullScreenVideoAdInteraction(params : NSDictionary){
        let mCodeId = (params.value(forKey: "codeId") as! String)
        self.loadFullscreenVideoAdWithAdUnitID(adUnitID: mCodeId)
    }
    
    func loadFullscreenVideoAdWithAdUnitID(adUnitID:String){
        
        self.fsvAd =  ABUFullscreenVideoAd(adUnitID: adUnitID)
        
        fsvAd!.delegate = self
        if(ABUAdSDKManager.configDidLoad()){
            fsvAd!.loadData()
        }else{
            weak var weakSelf = self
             ABUAdSDKManager.addConfigLoadSuccessObserver(self) { _Arg in
                 weakSelf?.fsvAd!.loadData()
            }
        }
    }
    

}
extension FullScreenVideoAdInteraction:ABUFullscreenVideoAdDelegate{
    /// 广告加载成功回调
    /// @param fullscreenVideoAd 广告管理对象
    public func fullscreenVideoAdDidLoad(_ fullscreenVideoAd: ABUFullscreenVideoAd) {
        self.fsvAd?.getShowEcpmInfo()
        self.fsvAd?.show(fromRootViewController: (UIApplication.shared.keyWindow?.rootViewController)!)
    }
    
    /// 广告加载失败回调
    /// @param fullscreenVideoAd 广告管理对象
    /// @param error 错误信息
    public func fullscreenVideoAd(_ fullscreenVideoAd: ABUFullscreenVideoAd, didFailWithError error: Error?) {
        LogUtil.logInstance.printLog(message: "fullScreenVideoAdInteraction 加载失败"+String(error.debugDescription))
        let map : NSDictionary = ["adType":"fullScreenVideoAdInteraction",
                                  "onAdMethod":"onAdFail",
                                  "error":String(error.debugDescription)]
        GromoreAdPluginSwift.event!.sendEvent(event: map)
     }
    
    /// 广告展示回调
    /// @param fullscreenVideoAd 广告管理对象
     public func fullscreenVideoAdDidVisible(_ fullscreenVideoAd: ABUFullscreenVideoAd) {
         LogUtil.logInstance.printLog(message: "fullScreenVideoAdInteraction 显示广告")
         let map : NSDictionary = ["adType":"fullScreenVideoAdInteraction",
                                   "onAdMethod":"onAdShow"]
         GromoreAdPluginSwift.event!.sendEvent(event: map)
    }
  
    /// 广告点击跳过事件回调
    /// @param fullscreenVideoAd 广告管理对象
     public func fullscreenVideoAdDidSkip(_ fullscreenVideoAd: ABUFullscreenVideoAd) {
         LogUtil.logInstance.printLog(message: "fullScreenVideoAdInteraction 点击跳过")
         let map : NSDictionary = ["adType":"fullScreenVideoAdInteraction",
                                   "onAdMethod":"onAdSkip"]
         GromoreAdPluginSwift.event!.sendEvent(event: map)
    }
    /// 广告关闭事件回调
    /// @param fullscreenVideoAd 广告管理对象
     public func fullscreenVideoAdDidClose(_ fullscreenVideoAd: ABUFullscreenVideoAd) {
         LogUtil.logInstance.printLog(message: "fullScreenVideoAdInteraction 关闭")
         let map : NSDictionary = ["adType":"fullScreenVideoAdInteraction",
                                   "onAdMethod":"onAdClose"]
         GromoreAdPluginSwift.event!.sendEvent(event: map)
     }
   
    /// 广告视频播放完成或者出错回调
    /// @param fullscreenVideoAd 广告管理对象
    /// @param error 播放出错时的信息，播放完成时为空
    public func fullscreenVideoAdDidPlayFinish(_ fullscreenVideoAd: ABUFullscreenVideoAd, didFailWithError error: Error?) {
        
        LogUtil.logInstance.printLog(message: "fullScreenVideoAdInteraction 播放完成")
        let map : NSDictionary = ["adType":"fullscreenVideoAd",
                                  "onAdMethod":"onAdFinish"]
        GromoreAdPluginSwift.event!.sendEvent(event: map)
    }
    /// 广告展示失败回调
    /// @param fullscreenVideoAd 广告管理对象
    /// @param error 展示失败的原因
    public func fullscreenVideoAdDidShowFailed(_ fullscreenVideoAd: ABUFullscreenVideoAd, error: Error) {
        LogUtil.logInstance.printLog(message: "fullScreenVideoAdInteraction 广告展示失败")
        let map : NSDictionary = ["adType":"fullScreenVideoAdInteraction",
                                  "onAdMethod":"onAdFail"]
        GromoreAdPluginSwift.event!.sendEvent(event: map)
    }
}
