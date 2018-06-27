package com.seven.framework.widget.floatnotifycation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by wangbin on 2017/8/31.
 */

public class MeizuMiuiFloatViewPermission {


    /**
     * 判断MIUI的悬浮窗权限
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isMiuiFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24);  // AppOpsManager.OP_SYSTEM_ALERT_WINDOW
        } else {
            if ((context.getApplicationInfo().flags & 1 << 27) == 1 << 27) {
                return true;
            } else {
                return false;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class<?> spClazz = Class.forName(manager.getClass().getName());
                Method method = manager.getClass().getDeclaredMethod("checkOp", int.class, int.class, String.class);
                int property = (Integer) method.invoke(manager, op,
                        Binder.getCallingUid(), context.getPackageName());
                if (AppOpsManager.MODE_ALLOWED == property) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {

            }
        } else {

        }
        return false;
    }

    /**
     * 经测试V5版本是有区别的
     *
     * @param context
     */
    public static void openMiuiPermissionActivity(Context context, int requestCode) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");

        if ("V5".equals(getProperty())) {
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("canking", "error");
            }
            intent.setClassName("com.miui.securitycenter", "com.miui.securitycenter.permission.AppPermissionsEditor");
            intent.putExtra("extra_package_uid", pInfo.applicationInfo.uid);
        } else {
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
        }

        if (isActivityAvailable(context, intent)) {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                activity.startActivityForResult(intent, requestCode);
            }
        } else {
            Log.e("canking", "Intent is not available!");
        }
    }

    private static boolean isActivityAvailable(Context cxt, Intent intent) {
        PackageManager pm = cxt.getPackageManager();
        if (pm == null) {
            return false;
        }
        List<ResolveInfo> list = pm.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }

    private static String getProperty() {
        String property = "null";
        if (!"Xiaomi".equalsIgnoreCase(Build.MANUFACTURER)) {
            return property;
        }
        try {
            Class<?> spClazz = Class.forName("android.os.SystemProperties");
            Method method = spClazz.getDeclaredMethod("get", String.class, String.class);
            property = (String) method.invoke(spClazz, "ro.miui.ui.version.name", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return property;
    }
}
