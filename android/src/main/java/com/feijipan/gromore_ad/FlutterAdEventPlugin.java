package com.feijipan.gromore_ad;


import androidx.annotation.NonNull;

import com.feijipan.gromore_ad.util.ViewConfig;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;

public class FlutterAdEventPlugin implements EventChannel.StreamHandler {

    private EventChannel.EventSink eventSink;
    private EventChannel eventChannel;


    FlutterAdEventPlugin(@NonNull FlutterPlugin.FlutterPluginBinding binding){
        eventChannel = new EventChannel(binding.getBinaryMessenger(), ViewConfig.adevent);
        eventChannel.setStreamHandler(this);
    }


    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
         eventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
       eventSink = null;
    }



   public void sendContent(Map<String,Object> prm){
        if(eventChannel != null&&eventSink != null)
         eventSink.success(prm);
    }

}
