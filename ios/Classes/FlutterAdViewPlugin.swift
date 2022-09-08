//
//  FlutterAdViewPlugin.swift
//  gromore_ad
//
//  Created by ywsj on 2022/7/20.
//

import Foundation
import Flutter

public class FlutterAdViewPlugin:NSObject{
    
    static func register(viewRegistrar : FlutterPluginRegistrar){
         let bannerAdViewFactory = BannerAdViewFactory(messenger: viewRegistrar.messenger())
        viewRegistrar.register(bannerAdViewFactory, withId: FlutterAdConfig.view.bannerAdView)
        
         let splashAdViewFactory = SplashAdViewFactory(messenger: viewRegistrar.messenger())
        viewRegistrar.register(splashAdViewFactory, withId: FlutterAdConfig.view.splashAdView)
        //draw广告
//        let drawfeedAdViewFactory = DrawFeedAdViewFactory(messenger: viewRegistrar.messenger())
//        viewRegistrar.register(drawfeedAdViewFactory, withId: FlutterUnionadConfig.view.drawFeedAdView)
         let nativeAdviewFactory = NativeAdViewFactory(messenger: viewRegistrar.messenger())
        viewRegistrar.register(nativeAdviewFactory, withId: FlutterAdConfig.view.nativeAdView)
    }
    
}

