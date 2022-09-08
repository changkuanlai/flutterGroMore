package com.feijipan.gromore_ad;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;
import com.feijipan.gromore_ad.fullscreenvideoadinteraction.FullScreenVideoAdInteraction;
import com.feijipan.gromore_ad.nativead.AdFeedManager;
import com.feijipan.gromore_ad.nativead.AdNativeManager;
import com.feijipan.gromore_ad.rewardvideoad.RewardVideoAdView;

import java.lang.invoke.MethodHandle;
import java.util.List;
import java.util.Map;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** GromoreAdPlugin */
public class GromoreAdPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

  private MethodChannel channel;
  private static Activity _activity;
  private static Context _context;
  private static FlutterAdEventPlugin event;
  private FlutterPlugin.FlutterPluginBinding mFlutterPluginBinding;


  public static AdNativeManager getAdNativeManager() {
    return adNativeManager;
  }

  static private AdNativeManager adNativeManager;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "com.disk.native.receive/gromore_ad");
    mFlutterPluginBinding = flutterPluginBinding;
    channel.setMethodCallHandler(this);
    _context = flutterPluginBinding.getApplicationContext();
    event = new FlutterAdEventPlugin(flutterPluginBinding);
  }

  public static Context getAppContext() {
    return _context;
  }

  public static Activity getAppActivity() {
    return _activity;
  }

  public static FlutterAdEventPlugin getEventPlugin(){return event;}

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if(call.method.equals("register")){
      final Map  map = (Map) call.arguments;
      GMAdManagerHolder.init(_context,map,result);
     }else  if (call.method.equals("getSDKVersion")){
      String version = GMMediationAdSdk.getSdkVersion();
      result.success(version);
    }else if (call.method.equals("requestPermission")){
      GMMediationAdSdk.requestPermissionIfNecessary(_context);
    }else if(call.method.equals("showRewardVideoAd")){
       new RewardVideoAdView(_context,_activity, (Map<String, Object>) call.arguments);
    }else if(call.method.equals("showFullScreenVideoAdInteraction")){
       new FullScreenVideoAdInteraction(_context,_activity, (Map<String, Object>) call.arguments);
    }else if(call.method.equals("loadActiveAdInitData")){
//      feedManager = new AdFeedManager(_activity);
      adNativeManager = new AdNativeManager(_activity,(Map<String, Object>) call.arguments);
  
    }else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    _activity = binding.getActivity();
    FlutterAdViewPlugin.registerWith(mFlutterPluginBinding, _activity);
   }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    this.onDetachedFromActivity();
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    this.onAttachedToActivity(binding);
  }
  
  @Override
  public void onDetachedFromActivity() {
    _activity = null;
  }
}
