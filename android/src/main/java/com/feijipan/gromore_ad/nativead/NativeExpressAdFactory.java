package com.feijipan.gromore_ad.nativead;

import android.app.Activity;
import android.content.Context;

import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class NativeExpressAdFactory extends PlatformViewFactory {

    private final BinaryMessenger messenger;
    private final Activity activity;

    public NativeExpressAdFactory(BinaryMessenger messenger,Activity activity) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
        this.activity = activity;
    }

    @Override
    public PlatformView create(Context context, int viewId, Object args) {
        return new NativeExpressAdView(context,viewId,messenger, (Map<String, Object>) args,activity);
    }
}
