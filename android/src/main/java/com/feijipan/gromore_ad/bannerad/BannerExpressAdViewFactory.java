package com.feijipan.gromore_ad.bannerad;

import android.app.Activity;
import android.content.Context;

import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class BannerExpressAdViewFactory extends PlatformViewFactory {
    private BinaryMessenger messenger;
    private final Activity activity;

    public BannerExpressAdViewFactory(BinaryMessenger messenger,Activity activity) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
        this.activity = activity;
    }

    @Override
    public PlatformView create(Context context, int viewId, Object args) {
        return new BannerExpressAdView(context,viewId,messenger, (Map<String, Object>) args,activity);
    }
}
