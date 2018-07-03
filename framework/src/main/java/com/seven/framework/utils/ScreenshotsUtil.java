package com.seven.framework.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * 获取截图
 * Created by wangbin on 2018/2/7.
 */

public class ScreenshotsUtil {
    /**
     *
     * @param view
     * @return
     */
    public static Bitmap getViewScreenshots(View view) {
        if (view == null) {
            return null;
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        if (Build.VERSION.SDK_INT >= 11) {
            view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY));
            view.layout((int) view.getX(), (int) view.getY(), (int) view.getX() + view.getMeasuredWidth(), (int) view.getY() + view.getMeasuredHeight());
        } else {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
        Bitmap b = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return b;
    }


    /**
     *
     * @param view
     * @return
     */
    private static Bitmap getViewBitmapWithoutBottom(View view) {
        if (null == view) {
            return null;
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        if (Build.VERSION.SDK_INT >= 11) {
            view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY));
            view.layout((int) view.getX(), (int) view.getY(), (int) view.getX() + view.getMeasuredWidth(), (int) view.getY() + view.getMeasuredHeight());
        } else {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
        Bitmap bp = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() - view.getPaddingBottom());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取webview的长图
     *
     * @param webView
     * @return
     */
    public static Bitmap getWebViewScreenshots(WebView webView) {
        // WebView 生成长图，也就是超过一屏的图片，代码中的 longImage 就是最后生成的长图
        webView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        webView.layout(0, 0, webView.getMeasuredWidth(), webView.getMeasuredHeight());
        webView.setDrawingCacheEnabled(true);
        webView.buildDrawingCache();
        Bitmap longImage = Bitmap.createBitmap(webView.getMeasuredWidth(),
                webView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(longImage);  // 画布的宽高和 WebView 的网页保持一致
        Paint paint = new Paint();
        canvas.drawBitmap(longImage, 0, webView.getMeasuredHeight(), paint);
        webView.draw(canvas);
        return longImage;
    }



    /**
     *
     * @param context
     * @param webView
     * @return
     */
    public static Bitmap getWebViewLongScreenshots(Context context, WebView webView) {
        if (webView == null)
            return null;
        webView.scrollTo(0, 0);
        webView.buildDrawingCache(true);
        webView.setDrawingCacheEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        Bitmap b = getViewBitmapWithoutBottom(webView);
        // 可见高度
        int vh = webView.getHeight();
        // 容器内容实际高度
        int  th = (int)(webView.getContentHeight()*webView.getScale());
        Bitmap temp = null;
        if (th > vh) {
            int w = DeviceInfo.getAppScreenSize(context)[0];
            int absVh = vh - webView.getPaddingTop() - webView.getPaddingBottom();
            do {
                int restHeight = th - vh;
                if (restHeight <= absVh) {
                    webView.scrollBy(0, restHeight);
                    vh += restHeight;
                    temp = getViewScreenshots(webView);
                } else {
                    webView.scrollBy(0, absVh);
                    vh += absVh;
                    temp = getViewBitmapWithoutBottom(webView);
                }
                b = mergeBitmap(vh, w, temp, 0, webView.getScrollY(), b, 0, 0);
            } while (vh < th);
        }
        // 回滚到顶部
        webView.scrollTo(0, 0);
        webView.setVerticalScrollBarEnabled(true);
        webView.setDrawingCacheEnabled(false);
        webView.destroyDrawingCache();
        return b;
    }


    /**
     * 拼接图片
     * @param newImageH
     * @param newImageW
     * @param background
     * @param backX
     * @param backY
     * @param foreground
     * @param foreX
     * @param foreY
     * @return
     */
    private static Bitmap mergeBitmap(int newImageH, int newImageW, Bitmap background, float backX, float backY, Bitmap foreground, float foreX, float foreY) {
        if (null == background || null == foreground) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(newImageW, newImageH, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(bitmap);
        cv.drawBitmap(background, backX, backY, null);
        cv.drawBitmap(foreground, foreX, foreY, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        return bitmap;
    }


    public static Bitmap getScrollViewScreenshots(ScrollView scrollView) {
        return null;
    }

    public static Bitmap getListViewScreenshots(ListView listView) {
        return null;
    }

    public static Bitmap getRecycleViewScreenshots(RecyclerView recyclerView) {
        return null;
    }
}
