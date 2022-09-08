package com.feijipan.gromore_ad.splash;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAd;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAdListener;
import com.feijipan.gromore_ad.GromoreAdPlugin;
import com.feijipan.gromore_ad.util.UIUtils;

import java.lang.ref.SoftReference;

/**
 * 开屏广告——开屏小窗模式操作类
 * 开屏广告小窗模式针对的是：穿山甲开屏点睛广告，腾讯优量汇（gdt）开屏V+广告, 快手开屏小窗模式
 */
public class SplashMinWindowManager {
    private static final String TAG = "SplashMinWindowManager";
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private int mMinWindowViewWidth;//悬浮窗的宽度
    private int mMinWindowViewHeight;//悬浮窗的高度
    private int mMinWindowViewMargin;//悬浮窗最小离屏幕边缘的距离
    private int mMinWindowViewMarginBottom;//悬浮窗默认距离屏幕底端的高度
    private int mMinWindowViewPos;//悬浮窗默认位于屏幕左面或右面
    private int mMinWindowViewAnimationTime;//悬浮窗缩放动画的，单位ms
    private SoftReference<GMSplashAd> mSplashAd;
    private SoftReference<View> mSplashShowView;
    private int[] mOriginSplashPos = new int[2];
    private int mDecorViewWidth;
    private int mDecorViewHeight;
    private volatile static SplashMinWindowManager mInstance;
    private boolean mIsSupportSplashMinWindow = false; //是否支持开屏小窗

    private Rect rect = new Rect(); //ks 开屏小窗专用  minWindow窗口展示的区域，单位是px

    public interface AnimationCallBack {
        void animationStart(int animationTime);

        void animationEnd();
    }

    /**
     * 单例获取SplashMinWindowManager对象
     *
     * @return
     */
    public static SplashMinWindowManager getInstance() {
        if (mInstance == null) {
            synchronized (SplashMinWindowManager.class) {
                if (mInstance == null) {
                    mInstance = new SplashMinWindowManager();
                }
            }
        }
        return mInstance;
    }

    private SplashMinWindowManager() {
        Context context = GromoreAdPlugin.getAppContext();
        initMinWindowViewData(context);
        mMinWindowViewMargin = UIUtils.dip2px(context, 16);
        mMinWindowViewMarginBottom = UIUtils.dip2px(context, 100);
        mMinWindowViewPos = RIGHT;
        mMinWindowViewAnimationTime = 300;
    }

    private void initMinWindowViewData(Context context) {
        int deviceWidth = Math.min(UIUtils.getScreenHeight(context), UIUtils.getScreenWidth(context));
        GMSplashAd splashAd = null;
        if(mSplashAd != null){
            splashAd = mSplashAd.get();
        }
        if (splashAd != null && splashAd.getMinWindowSize() != null && splashAd.getMinWindowSize().length >= 2) {
            //使用推荐的点睛宽高
            mMinWindowViewWidth = UIUtils.dip2px(context, splashAd.getMinWindowSize()[0]);
            mMinWindowViewHeight = UIUtils.dip2px(context, splashAd.getMinWindowSize()[1]);
        } else {
            //默认的点睛宽高
            mMinWindowViewWidth = Math.round(deviceWidth * 0.3f);//屏幕宽度的30%，之前使用PxUtils.dpToPx(context, 90);
            mMinWindowViewHeight = Math.round(mMinWindowViewWidth * 16 / 9);//根据宽度计算高度，之前使用PxUtils.dpToPx(context, 160);
        }

        rect.right = deviceWidth - mMinWindowViewMargin;
        rect.left = rect.right - mMinWindowViewWidth;
        rect.bottom = UIUtils.getScreenHeight(context) - mMinWindowViewMarginBottom;
        rect.top = rect.bottom - mMinWindowViewHeight;
    }

    public void setSplashInfo(GMSplashAd splashAd, View splashView, View decorView) {
        this.mSplashAd = new SoftReference<>(splashAd);
        this.mSplashShowView = new SoftReference<>(splashView);
        splashView.getLocationOnScreen(mOriginSplashPos);
        mDecorViewWidth = decorView.getWidth();
        mDecorViewHeight = decorView.getHeight();
        initMinWindowViewData(GromoreAdPlugin.getAppContext());
    }

    public void clearSplashStaticData() {
        mSplashAd = null;
        mSplashShowView = null;
    }

    public GMSplashAd getSplashAd() {
        if(mSplashAd == null){
            return null;
        }
        return mSplashAd.get();
    }


    /**
     * 在两个不同的页面展示开屏小窗模式
     *
     * @param decorView
     * @param splashViewContainer
     * @param callBack
     * @return
     */
    public ViewGroup showMinWindowInTwoActivity(final ViewGroup decorView,
                                                final ViewGroup splashViewContainer,
                                                final AnimationCallBack callBack) {


        if (decorView == null || splashViewContainer == null) {
            return null;
        }

        if (mSplashAd == null || mSplashShowView == null) {
            return null;
        }

        GMSplashAd splashAd = mSplashAd.get();
        if(splashAd == null){
            return null;
        }

        /**
         * 不需要动画操作，直接调用展示小窗方法  ps:快手开屏小窗模式不需要动画操作
         */
        if (splashAd.showWindowDirect(rect, mTTSplashAdListener)) {
            return null;
        }

        if (!mIsSupportSplashMinWindow) {
            return null;
        }

        return startSplashClickEyeAnimationInTwoActivity(decorView, splashViewContainer, callBack);
    }

    /**
     * 在两个不同的页面展示开屏小窗动画
     *
     * @param decorView
     * @param splashViewContainer
     * @param callBack
     * @return
     */
    private ViewGroup startSplashClickEyeAnimationInTwoActivity(final ViewGroup decorView,
                                                                final ViewGroup splashViewContainer,
                                                                final AnimationCallBack callBack) {
        if (decorView == null || splashViewContainer == null) {
            return null;
        }
        if (mSplashAd == null || mSplashShowView == null) {
            return null;
        }
        GMSplashAd splashAd = mSplashAd.get();
        if(splashAd == null) {
            return null;
        }
        View view = mSplashShowView.get();
        if(view == null){
            return null;
        }
        return startSplashClickEyeAnimation(splashAd, view, decorView, splashViewContainer, callBack);
    }


    /**
     * 在开屏广告当前页面展示开屏小窗模式
     *
     * @param splashAd
     * @param splash
     * @param decorView
     * @param splashViewContainer
     * @param callBack
     * @return
     */
    public ViewGroup showMinWindow(final GMSplashAd splashAd, final View splash, final ViewGroup decorView,
                                   final ViewGroup splashViewContainer,
                                   final AnimationCallBack callBack) {
        if (splashAd == null || splash == null || splashViewContainer == null) {
            return null;
        }


        if (splashAd.showWindowDirect(rect, mTTSplashAdListener)) {
            return null;
        }

        return startSplashClickEyeAnimation(splashAd, splash, decorView, splashViewContainer, callBack);
    }

    /**
     * 在开屏广告当前页面展示开屏小窗动画
     *
     * @param splash
     * @param decorView
     * @param splashViewContainer
     * @param callBack
     * @return
     */
    private ViewGroup startSplashClickEyeAnimation(final GMSplashAd splashAd, final View splash, final ViewGroup decorView,
                                                   final ViewGroup splashViewContainer,
                                                   final AnimationCallBack callBack) {
        if (splashAd == null || splash == null || splashViewContainer == null) {
            return null;
        }

        int[] minWindowSize = splashAd.getMinWindowSize();
        if (minWindowSize != null && minWindowSize.length >= 2) {
            //使用推荐的开屏小窗宽高
            mMinWindowViewWidth = UIUtils.dip2px(GromoreAdPlugin.getAppContext(), minWindowSize[0]);
            mMinWindowViewHeight = UIUtils.dip2px(GromoreAdPlugin.getAppContext(), minWindowSize[1]);
        }

        final int[] splashScreenPos = new int[2];
        splash.getLocationOnScreen(splashScreenPos);
        final Context context = splashViewContainer.getContext();
        int splashViewWidth = splash.getWidth();
        int splashViewHeight = splash.getHeight();
        int animationContainerWidth = decorView.getWidth();
        int animationContainerHeight = decorView.getHeight();

        if (animationContainerWidth == 0) {
            animationContainerWidth = mDecorViewWidth;
        }
        if (animationContainerHeight == 0) {
            animationContainerHeight = mDecorViewHeight;
        }
        float xScaleRatio = (float) mMinWindowViewWidth / splashViewWidth;
        float yScaleRation = (float) mMinWindowViewHeight / splashViewHeight;
        final float animationDistX = mMinWindowViewPos == LEFT ? mMinWindowViewMargin :
                animationContainerWidth - mMinWindowViewMargin - mMinWindowViewWidth;
        final float animationDistY = animationContainerHeight - mMinWindowViewMarginBottom - mMinWindowViewHeight;  //最终位于container的y坐标
        UIUtils.removeFromParent(splash);
        FrameLayout.LayoutParams animationParams = new FrameLayout.LayoutParams(splashViewWidth, splashViewHeight);
        decorView.addView(splash, animationParams);
        final FrameLayout splashViewLayout = new FrameLayout(context);
        splash.setPivotX(0);
        splash.setPivotY(0);
        splash.animate()
                .scaleX(xScaleRatio)
                .scaleY(yScaleRation)
                .x(animationDistX)
                .y(animationDistY)
                .setInterpolator(new OvershootInterpolator(0))
                .setDuration(mMinWindowViewAnimationTime)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (callBack != null) {
                            callBack.animationStart(mMinWindowViewAnimationTime);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        UIUtils.removeFromParent(splash);
                        splash.setScaleX(1);
                        splash.setScaleY(1);
                        splash.setX(0);
                        splash.setY(0);
                        int[] clickEyeContainerScreenPos = new int[2];
                        splashViewContainer.getLocationOnScreen(clickEyeContainerScreenPos);
                        float distX = animationDistX - clickEyeContainerScreenPos[0] + splashScreenPos[0];
                        float distY = animationDistY - clickEyeContainerScreenPos[1] + splashScreenPos[1];

                        splashViewLayout.addView(splash, FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT);
                        FrameLayout.LayoutParams clickEyeParams = new FrameLayout.LayoutParams(mMinWindowViewWidth,
                                mMinWindowViewHeight);
                        splashViewContainer.addView(splashViewLayout, clickEyeParams);
                        splashViewLayout.setTranslationX(distX);
                        splashViewLayout.setTranslationY(distY);
                        if (callBack != null) {
                            callBack.animationEnd();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
        return splashViewLayout;
    }

    /**
     * 该开屏广告是否支持小窗模式
     *
     * @return
     */
    public boolean isSupportSplashMinWindow() {
        return mIsSupportSplashMinWindow;
    }

    /**
     * 设置该开屏广告是否支持小窗模式
     *
     * @param isSupportSplashMinWindow
     */
    public void setSupportSplashMinWindow(boolean isSupportSplashMinWindow) {
        this.mIsSupportSplashMinWindow = isSupportSplashMinWindow;
    }


    /**
     * 开屏广告监听，针对快手开屏小窗视图使用
     */
    GMSplashAdListener mTTSplashAdListener = new GMSplashAdListener() {
        @Override
        public void onAdClicked() {

        }

        @Override
        public void onAdShow() {

        }

        @Override
        public void onAdShowFail(AdError adError) {

        }

        @Override
        public void onAdSkip() {

        }

        @Override
        public void onAdDismiss() {

        }
    };
}
