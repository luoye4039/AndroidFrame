package com.seven.framework.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import java.util.concurrent.ArrayBlockingQueue;
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
    private long mShowTime = 1000 * 4;
    private static AppNotifycationManager sAppNotifycationManager;
    private View mCurrentShowView;//当前需要显示的view
    private ArrayBlockingQueue<View> mViewArrayBlockingQueue = new ArrayBlockingQueue<View>(20);
    private Disposable mLoopQueueDisposable;
    private Activity mCurrentShowActivity;
    private TranslateAnimation mEnterTranslateAnimation;
    private TranslateAnimation mOutTranslateAnimation;

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
        mEnterTranslateAnimation = new TranslateAnimation(0, 0, -100, 0);
        mEnterTranslateAnimation.setDuration(300);
        mOutTranslateAnimation = new TranslateAnimation(0, 0, 0, -100);
        mOutTranslateAnimation.setDuration(300);
    }

    /**
     * 初始化消息通知
     *
     * @param application application
     * @param showTime    显示的时间
     */
    public void init(Application application, long showTime) {
        application.registerActivityLifecycleCallbacks(this);
        mShowTime = showTime;
    }


    /**
     * 添加需要显示的view到队列中
     *
     * @param view 显示是view
     */
    public void addShowView(View view) {
        mViewArrayBlockingQueue.add(view);
        loopQueue();
    }

    /**
     * 添加需要显示的view到队列中
     *
     * @param context  context
     * @param layoutId view布局
     */
    public void addShowView(Context context, @LayoutRes int layoutId) {
        View inflate = LayoutInflater.from(context).inflate(layoutId, null);
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
                            dismissView();
                            View pollView = mViewArrayBlockingQueue.poll();
                            if (pollView != null) {
                                showViewToActivity(pollView, mEnterTranslateAnimation);
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
    private void showViewToActivity(View view, Animation animation) {
        if (mCurrentShowActivity != null) {
            FrameLayout mRootView = mCurrentShowActivity.findViewById(android.R.id.content);
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) parent;
                viewGroup.removeView(view);
            }
            if (animation != null) {
                view.startAnimation(animation);
            }
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams
                    (FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            mRootView.addView(view, layoutParams);
        }
    }


    /**
     * 关闭显示的通知
     */
    private void dismissView() {
        if (mCurrentShowActivity != null && mCurrentShowView != null) {
            FrameLayout mRootView = mCurrentShowActivity.findViewById(android.R.id.content);
            int childCount = mRootView.getChildCount();
            if (childCount > 0) {
                for (int count = childCount; count > 0; count--) {
                    View childAt = mRootView.getChildAt(count);
                    if (childAt == mCurrentShowView) {
                        if (mOutTranslateAnimation != null)
                            mCurrentShowView.startAnimation(mOutTranslateAnimation);
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

    }

    @Override
    public void onActivityResumed(Activity activity) {
        mCurrentShowActivity = activity;
        if (mCurrentShowView != null) {
            showViewToActivity(mCurrentShowView, null);
        }
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
