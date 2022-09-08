package com.feijipan.gromore_ad;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.feijipan.gromore_ad.bannerad.BannerExpressAdViewFactory;
import com.feijipan.gromore_ad.nativead.NativeExpressAdFactory;
import com.feijipan.gromore_ad.splash.SplashAdViewFactory;
import com.feijipan.gromore_ad.util.ViewConfig;

import io.flutter.embedding.engine.plugins.FlutterPlugin;


public class FlutterAdViewPlugin {

    public static void registerWith(@NonNull FlutterPlugin.FlutterPluginBinding binding, Activity activity) {
        binding.getPlatformViewRegistry().registerViewFactory(ViewConfig.splashAdView,
                new SplashAdViewFactory(binding.getBinaryMessenger()));

        binding.getPlatformViewRegistry().registerViewFactory(ViewConfig.nativeAdView,
                new NativeExpressAdFactory(binding.getBinaryMessenger(), activity));

        binding.getPlatformViewRegistry().registerViewFactory(ViewConfig.bannerAdView,new
                BannerExpressAdViewFactory(binding.getBinaryMessenger(),activity));
    }

}
