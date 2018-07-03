package com.seven.framework.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;


/**
 * Created by wangbin on 2017/4/7.
 */

public class NetWorkUtil {

    public final static int NONE = 0;// 无网络
    public final static int WIFI = 1;// Wi-Fi
    public final static int MOBILE = 2;// 3G,GPRS


    private static NetWorkUtil sNetWorkUtil;
    private Activity mActivity;
    private NetChangeListner mNetChangeListner;
    private int mCurrentNetType;

    public int getCurrentNetType() {
        return mCurrentNetType;
    }

    private NetWorkUtil() {

    }

    public static NetWorkUtil getInstance() {
        if (sNetWorkUtil == null) {
            synchronized (NetWorkUtil.class) {
                if (sNetWorkUtil == null) {
                    sNetWorkUtil = new NetWorkUtil();
                }
            }
        }
        return sNetWorkUtil;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()) ||
                    WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction()) ||
                    WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                netChange(getNetworkState(context));
            }
        }
    };

    private void netChange(int netWorkState) {
        if (mNetChangeListner != null) {
            if (netWorkState != mCurrentNetType) {
                mCurrentNetType = netWorkState;
                mNetChangeListner.onChange(netWorkState);
            }
        }
    }

    /**
     * 设置网络监听 仅最后一个设置的有效
     * @param activity
     * @param netChangeListner
     */
    public void setNetChangeListner(Activity activity, NetChangeListner netChangeListner) {
        if(mActivity!=null){
            removeNetChangeListner();
        }
        mActivity = activity;
        mNetChangeListner = netChangeListner;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        activity.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    /**
     * 取消网络监听
     */
    public void removeNetChangeListner() {
        mActivity.unregisterReceiver(mBroadcastReceiver);
        mActivity = null;
        mNetChangeListner = null;
    }


    public interface NetChangeListner {
        void onChange(int netWorkState);
    }

    /**
     * 获取当前网络状态(wifi,3G)
     *
     * @param context
     * @return
     */
    public static int getNetworkState(Context context) {
        if (context == null) {
            return NONE;
        }
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null ||
                connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) == null ||
                connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) == null) {
            return NONE;
        }
        NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
            return MOBILE;
        }
        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
            return WIFI;
        }
        return NONE;

    }

    /**
     * 检测是否有网络
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo network : info) {
                    if (network.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断是否有可用的wifi
     * @param ctx
     * @return
     */
    public static boolean isWifiAvailable(Context ctx) {
        ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }

    /**
     * 获取网络类型
     * @param context
     * @return
     */
    public static String getNetworkTypeName(Context context) {
        if (context != null) {
            ConnectivityManager connectMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectMgr != null) {
                NetworkInfo info = connectMgr.getActiveNetworkInfo();
                if (info != null) {
                    switch (info.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            return "WIFI";
                        case ConnectivityManager.TYPE_MOBILE:
                            return getNetworkTypeName(info.getSubtype());
                    }
                }
            }
        }
        return getNetworkTypeName(TelephonyManager.NETWORK_TYPE_UNKNOWN);
    }

    /**
     * 获取网络类型
     * @param type
     * @return
     */

    public static String getNetworkTypeName(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "CDMA - EvDo rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "CDMA - EvDo rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "CDMA - EvDo rev. B";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "CDMA - 1xRTT";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "CDMA - eHRPD";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDEN";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            default:
                return "UNKNOWN";
        }
    }


}
