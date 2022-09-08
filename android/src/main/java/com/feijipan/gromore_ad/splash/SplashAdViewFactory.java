package com.feijipan.gromore_ad.splash;

import android.content.Context;

import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class SplashAdViewFactory extends PlatformViewFactory{
    private BinaryMessenger messenger;
  
    public SplashAdViewFactory(BinaryMessenger messenger) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
    }

    @Override
    public PlatformView create(Context context, int viewId, Object args) {
        return new SplashAdView(context,viewId,messenger,(Map<String, Object>) args);
    }
}
