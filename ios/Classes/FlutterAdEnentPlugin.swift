//
//  FlutterAdEnentPlugin.swift
//  gromore_ad
//
//  Created by ywsj on 2022/7/20.
//

import Foundation
import Flutter

public class FlutterAdEnentPlugin : NSObject, FlutterStreamHandler{
    
    private var eventSink:FlutterEventSink? = nil
    
    init(_ registrar: FlutterPluginRegistrar){
        super.init()
        let eventChannel = FlutterEventChannel.init(name: FlutterAdConfig.event.adevent, binaryMessenger:registrar.messenger())
        eventChannel.setStreamHandler((self as FlutterStreamHandler & NSObjectProtocol))
    }
    
    
    public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        eventSink = events
        return nil
    }
    
    public func onCancel(withArguments arguments: Any?) -> FlutterError? {
        eventSink = nil
        return nil
    }
    
    //发送event
     public func sendEvent(event:NSDictionary) {
        eventSink?(event)
    }

}
