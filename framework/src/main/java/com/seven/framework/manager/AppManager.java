package com.seven.framework.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Stack;

/**
 * APP Activity管理
 */
public class AppManager {
    private static final String TAG = "ActivityStackManager";
    /**
     * Activity栈
     */
    private Stack<WeakReference<Activity>> mActivityStack;
    private static AppManager sAppManager;

    private AppManager() {
    }

    /***
     * 获得AppManager的实例
     *
     * @return AppManager实例
     */
    public static AppManager getInstance() {
        if (sAppManager == null) {
            synchronized (AppManager.class) {
                if (sAppManager == null)
                    sAppManager = new AppManager();
            }
        }
        return sAppManager;
    }

    /***
     * 栈中Activity的数
     *
     * @return Activity的数
     */
    public int stackSize() {
        return mActivityStack == null ? 0 : mActivityStack.size();
    }

    /***
     * 获得Activity栈
     *
     * @return Activity栈
     */
    public Stack<WeakReference<Activity>> getStack() {
        return mActivityStack;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(WeakReference<Activity> activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
        mActivityStack.add(activity);
    }

    /**
     * 删除ac
     *
     * @param activity 弱引用的ac
     */
    public void removeActivity(WeakReference<Activity> activity) {
        if (mActivityStack != null) {
            mActivityStack.remove(activity);
        }
    }

    /**
     * 删除ac
     *
     * @param activity 弱引用的ac
     */
    public void removeActivity(Activity activity) {
        if (mActivityStack != null) {
            Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator();
            while (iterator.hasNext()) {
                WeakReference<Activity> stackActivity = iterator.next();
                if (stackActivity.get() == null) {
                    iterator.remove();
                    continue;
                }
                if (stackActivity.get() == activity) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    /**
     * 判断某个页面是否存在
     *
     * @param cls cls，比如：MainActivity.class.getCanonicalName().
     * @return
     */
    public boolean isActivityRunning(String cls) {
        if (TextUtils.isEmpty(cls) || mActivityStack == null) {
            return false;
        }

        for (WeakReference<Activity> activity : mActivityStack) {
            if (activity.get().getClass().getCanonicalName().equals(cls)) {
                return true;
            }
        }

        return false;
    }


    /***
     * 获取栈顶Activity（堆栈中最后一个压入的）
     *
     * @return Activity
     */
    public Activity getTopActivity() {
        Activity activity = mActivityStack.lastElement().get();
        if (null == activity) {
            return null;
        } else {
            return mActivityStack.lastElement().get();
        }
    }

    /***
     * 通过class 获取栈顶Activity
     *
     * @param cls
     * @return Activity
     */
    public Activity getActivityByClass(Class<?> cls) {
        Activity return_activity = null;
        for (WeakReference<Activity> activity : mActivityStack) {
            if (activity.get().getClass().equals(cls)) {
                return_activity = activity.get();
                break;
            }
        }
        return return_activity;
    }

    /**
     * 结束栈顶Activity（堆栈中最后一个压入的）
     */
    public void finishTopActivity() {
        try {
            WeakReference<Activity> activity = mActivityStack.lastElement();
            finishActivity(activity);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /***
     * 结束指定的Activity
     *
     * @param activity
     */
    public void finishActivity(WeakReference<Activity> activity) {
        try {
            Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator();
            while (iterator.hasNext()) {
                WeakReference<Activity> stackActivity = iterator.next();
                if (stackActivity.get() == null) {
                    iterator.remove();
                    continue;
                }
                if (stackActivity.get().getClass().getName().equals(activity.get().getClass().getName())) {
                    iterator.remove();
                    stackActivity.get().finish();
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /***
     * 结束指定类名的Activity
     *
     * @param cls
     */
    public void finishActivity(Class<?> cls) {
        try {
            ListIterator<WeakReference<Activity>> listIterator = mActivityStack.listIterator();
            while (listIterator.hasNext()) {
                Activity activity = listIterator.next().get();
                if (activity == null) {
                    listIterator.remove();
                    continue;
                }
                if (activity.getClass() == cls) {
                    listIterator.remove();
                    activity.finish();
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        try {
            ListIterator<WeakReference<Activity>> listIterator = mActivityStack.listIterator();
            while (listIterator.hasNext()) {
                Activity activity = listIterator.next().get();
                if (activity != null) {
                    activity.finish();
                }
                listIterator.remove();
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * 移除除了某个activity的其他所有activity
     *
     * @param cls 界面
     */
    public void finishAllActivityOnlyOne(Class cls) {
        try {
            ListIterator<WeakReference<Activity>> listIterator = mActivityStack.listIterator();
            while (listIterator.hasNext()) {
                Activity activity = listIterator.next().get();
                if (activity != null) {
                    if (activity.getClass().getName().equals(cls.getName())) {
                        continue;
                    }
                    activity.finish();
                    listIterator.remove();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    /**
     * 移除除了某个activity的其他所有activity
     *
     * @param cls 界面
     */
    public void finishAllActivityOnlyOneAndTop(Class cls) {
        WeakReference<Activity> activityWeakReference = mActivityStack.lastElement();
        Activity lastActivity = activityWeakReference.get();
        try {
            ListIterator<WeakReference<Activity>> listIterator = mActivityStack.listIterator();
            while (listIterator.hasNext()) {
                Activity activity = listIterator.next().get();
                if (activity != null) {
                    if (activity.getClass().getName().equals(cls.getName()) ||
                            activity.getClass().getName().equals(lastActivity.getClass().getName())) {
                        continue;
                    }
                    activity.finish();
                    listIterator.remove();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * 退出应用程序
     */
    public void restartAPP(Context context) {
        try {
            finishAllActivity();
            Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

}
