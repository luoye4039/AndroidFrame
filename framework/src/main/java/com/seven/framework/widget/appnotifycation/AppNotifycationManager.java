package com.seven.framework.widget.appnotifycation;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * App内通知
 */
public class AppNotifycationManager implements Application.ActivityLifecycleCallbacks {
    private long mShowTime;
    private static AppNotifycationManager sAppNotifycationManager;
    private Application mApplication;
    private View mCurrentShowView;//当前需要显示的view
    private LinkedBlockingDeque<View> mViewLinkedBlockingDeque = new LinkedBlockingDeque<View>(100);
    private Disposable mLoopQueueDisposable;
    private Activity mCurrentShowActivity;
    private Animation mEnterAnimation;//进入动画
    private Animation mOutAnimation;//退出动画
    private AlphaAnimation mProtectionAnimation;//view移除时保留动画

    private AppNotifycationManager() {
    }

    public static AppNotifycationManager getInstance() {
        if (sAppNotifycationManager == null) {
            synchronized (AppNotifycationManager.class) {
                if (sAppNotifycationManager == null)
                    sAppNotifycationManager = new AppNotifycationManager();
            }
        }
        return sAppNotifycationManager;
    }

    /**
     * 初始化消息通知
     *
     * @param application application
     */
    public void init(Application application) {
        application.registerActivityLifecycleCallbacks(this);
        TranslateAnimation enterTranslateAnimation = new TranslateAnimation(0, 0, -100, 0);
        enterTranslateAnimation.setDuration(300);
        TranslateAnimation outTranslateAnimation = new TranslateAnimation(0, 0, 0, -100);
        outTranslateAnimation.setDuration(200);
        AppNotifycationConfig appNotifycationConfig = new AppNotifycationConfig.Builder()
                .setShowTime(4 * 1000)
                .setEnterAnimation(enterTranslateAnimation)
                .setOutAnimation(outTranslateAnimation)
                .build();

        init(application, appNotifycationConfig);
    }

    /**
     * 初始化消息通知
     *
     * @param application application
     */
    public void init(Application application, AppNotifycationConfig appNotifycationConfig) {
        application.registerActivityLifecycleCallbacks(this);
        mApplication = application;
        mShowTime = appNotifycationConfig.getShowTime();
        mEnterAnimation = appNotifycationConfig.getEnterAnimation();
        mOutAnimation = appNotifycationConfig.getOutAnimation();
        initAnimotion();
    }

    /**
     * 初始化过渡动画
     */
    private void initAnimotion() {
        mProtectionAnimation = new AlphaAnimation(100, 90);
        mProtectionAnimation.setDuration(200);
    }


    /**
     * 添加需要显示的view到队列中
     *
     * @param view 显示是view
     */
    public void addShowView(View view) {
        if (mViewLinkedBlockingDeque.remainingCapacity() > 0) {
            if (mViewLinkedBlockingDeque.offer(view))
                loopQueue();
        }
    }

    /**
     * 添加需要显示的view到队列中
     *
     * @param layoutId view布局
     */
    public void addShowView(@LayoutRes int layoutId) {
        View inflate = LayoutInflater.from(mApplication).inflate(layoutId, null);
        addShowView(inflate);
    }


    /**
     * 添加需要显示的view到队列中
     *
     * @param view 显示是view
     */
    public void setShowView(View view) {
        if (mViewLinkedBlockingDeque.remainingCapacity() > 0) {
            mViewLinkedBlockingDeque.push(view);
            loopQueue();
        }
    }

    /**
     * 添加需要显示的view到队列中
     *
     * @param layoutId view布局
     */
    public void setShowView(@LayoutRes int layoutId) {
        View inflate = LayoutInflater.from(mApplication).inflate(layoutId, null);
        addShowView(inflate);
    }


    /**
     * 开始队列任务
     */
    private synchronized void loopQueue() {
        if (mLoopQueueDisposable == null) {
            mLoopQueueDisposable = Observable.interval(0, mShowTime, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            dismissView(mOutAnimation);
                            View pollView = mViewLinkedBlockingDeque.poll();
                            if (pollView != null) {
                                showViewToActivity(pollView, mEnterAnimation);
                            } else {
                                mLoopQueueDisposable.dispose();
                                mLoopQueueDisposable = null;
                            }
                            mCurrentShowView = pollView;
                        }
                    });
        }
    }

    /**
     * 显示通知view
     *
     * @param view      view
     * @param animation 动画
     */
    private synchronized void showViewToActivity(View view, Animation animation) {
        if (mCurrentShowActivity != null) {
            FrameLayout mRootView = mCurrentShowActivity.findViewById(android.R.id.content);
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) parent;
                viewGroup.removeView(view);
            }
            if (animation != null)
                view.startAnimation(animation);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams
                    (FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            mRootView.addView(view, layoutParams);
        }
    }


    /**
     * 关闭显示的通知
     */
    private void dismissView(Animation animation) {
        if (mCurrentShowActivity != null && mCurrentShowView != null) {
            FrameLayout mRootView = mCurrentShowActivity.findViewById(android.R.id.content);
            int childCount = mRootView.getChildCount();
            if (childCount > 0) {
                for (int count = childCount; count > 0; count--) {
                    View childAt = mRootView.getChildAt(count);
                    if (childAt == mCurrentShowView) {
                        if (animation != null)
                            mCurrentShowView.startAnimation(animation);
                        mRootView.removeView(mCurrentShowView);
                        break;
                    }
                }
            }
        }
    }


    /**
     * 释放app通知
     *
     * @param application
     */
    public void release(Application application) {
        application.unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        mCurrentShowActivity = activity;
        if (mCurrentShowView != null) {
            showViewToActivity(mCurrentShowView, null);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

}
