//
//  RewardedVideoAd.swift
//  gromore_ad
//
//  Created by ywsj on 2022/7/22.
//

import Foundation
import ABUAdSDK
import SVProgressHUD
@objc public class RewardedVideoAd : UIViewController{
    
    private var rewardedVideoAd:ABURewardedVideoAd?

    public override func viewDidLoad() {
        SVProgressHUD.show()
        loadRewardedVideoAd()
    }
    
      var codeId :String = ""
    
     func loadRewardedVideoAd(){
         rewardedVideoAd = ABURewardedVideoAd(adUnitID: self.codeId)
         rewardedVideoAd?.delegate = self
         if(ABUAdSDKManager.configDidLoad()){
             rewardedVideoAd?.loadData()
        }else{
            weak var weakSelf = self
              ABUAdSDKManager.addConfigLoadSuccessObserver(self) { _Arg in
                  weakSelf?.rewardedVideoAd?.loadData()
            }
        }
    }
    
    private func disposeView() {
        self.dismiss(animated: false, completion: nil)
        SVProgressHUD.dismiss()
      }
}
extension RewardedVideoAd:ABURewardedVideoAdDelegate{
    /// 广告加载成功回调
    /// @param rewardedVideoAd 广告管理对象
     public func rewardedVideoAdDidLoad(_ rewardedVideoAd: ABURewardedVideoAd) {
         
         if(rewardedVideoAd.isReady){
             rewardedVideoAd.show(fromRootViewController: self)
         }
    }
    /// 广告加载失败回调
    /// @param rewardedVideoAd 广告管理对象
    /// @param error 错误信息
    public func rewardedVideoAd(_ rewardedVideoAd: ABURewardedVideoAd, didFailWithError error: Error?) {
        LogUtil.logInstance.printLog(message: "rewardedVideoAd 加载失败"+String(error.debugDescription))
        let map : NSDictionary = ["adType":"rewardedVideoAd",
                                  "onAdMethod":"onAdFail",
                                  "error":String(error.debugDescription)]
        GromoreAdPluginSwift.event?.sendEvent(event: map)
        disposeView()
        
    }
    /// 广告展示回调
    /// @param rewardedVideoAd 广告管理对象
    public func rewardedVideoAdDidVisible(_ rewardedVideoAd: ABURewardedVideoAd) {
        SVProgressHUD.dismiss()
        LogUtil.logInstance.printLog(message: "rewardedVideoAd 显示广告")
        let map : NSDictionary = ["adType":"rewardedVideoAd",
                                  "onAdMethod":"onAdShow"]
        GromoreAdPluginSwift.event?.sendEvent(event: map)
        
    }
    
    /// 广告展示失败回调
    /// @param rewardedVideoAd 广告管理对象
    /// @param error 展示失败的原因
    public func rewardedVideoAdDidShowFailed(_ rewardedVideoAd: ABURewardedVideoAd, error: Error) {
        LogUtil.logInstance.printLog(message: "rewardedVideoAd 加载失败")
        let map : NSDictionary = ["adType":"rewardedVideoAd",
                                  "onAdMethod":"onAdFail",
                                  "error":""]
        GromoreAdPluginSwift.event?.sendEvent(event: map)
    }

    /// 广告点击详情事件回调
    /// @param rewardedVideoAd 广告管理对象
     public func rewardedVideoAdDidClick(_ rewardedVideoAd: ABURewardedVideoAd) {
         LogUtil.logInstance.printLog(message: "rewardedVideoAd 点击")
         let map : NSDictionary = ["adType":"rewardedVideoAd",
                                   "onAdMethod":"onAdClick"]
         GromoreAdPluginSwift.event?.sendEvent(event: map)
    }
    /// 广告点击跳过事件回调
    /// @param rewardedVideoAd 广告管理对象
     public func rewardedVideoAdDidSkip(_ rewardedVideoAd: ABURewardedVideoAd) {
         LogUtil.logInstance.printLog(message: "rewardedVideoAd 点击跳过")
         let map : NSDictionary = ["adType":"rewardedVideoAd",
                                   "onAdMethod":"onAdSkip"]
         GromoreAdPluginSwift.event?.sendEvent(event: map)
    }
    /// 广告关闭事件回调
    /// @param rewardedVideoAd 广告管理对象
     public func rewardedVideoAdDidClose(_ rewardedVideoAd: ABURewardedVideoAd) {
         LogUtil.logInstance.printLog(message: "rewardedVideoAd 关闭")
         let map : NSDictionary = ["adType":"rewardedVideoAd",
                                   "onAdMethod":"onAdClose"]
         GromoreAdPluginSwift.event?.sendEvent(event: map)
         disposeView()
    }
    
    /// 广告视频播放完成或者出错回调
    /// @param rewardedVideoAd 广告管理对象
    /// @param error 播放出错时的信息，播放完成时为空
    public func rewardedVideoAd(_ rewardedVideoAd: ABURewardedVideoAd, didPlayFinishWithError error: Error?) {
        LogUtil.logInstance.printLog(message: "rewardedVideoAd 加载失败"+String(error.debugDescription))
        let map : NSDictionary = ["adType":"rewardedVideoAd",
                                  "onAdMethod":"onAdFail",
                                  "error":String(error.debugDescription)]
        GromoreAdPluginSwift.event?.sendEvent(event: map)
    
    }
}
