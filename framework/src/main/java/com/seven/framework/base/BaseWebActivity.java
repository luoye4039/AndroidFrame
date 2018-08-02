package com.seven.framework.base;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.seven.framework.R;
import com.seven.framework.entity.SelectDialogBean;
import com.seven.framework.manager.RetrofitManager;
import com.seven.framework.manager.uploadordown.DownFileBean;
import com.seven.framework.manager.uploadordown.OnDownFileObserver;
import com.seven.framework.utils.FileUtil;
import com.seven.framework.utils.LogUtils;
import com.seven.framework.utils.SystemUtil;
import com.seven.framework.utils.ToastUtil;
import com.seven.framework.view.dialog.CommonSelectDialog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BaseWebActivity extends BaseSelectPictureActivity {
    private static final String LOG_TAG = "web_log";
    private static final String H5_TO_NATIVE_NAME = "Android";
    private ProgressBar mBaseWebPbLoadingRrogress;
    private FrameLayout mBaseWebFlWv;
    protected WebView mWebView;
    private String mCurrentUrl;
    private boolean mClearPreviousRecords;
    private String mLoadingErrorData;
    private ValueCallback<Uri[]> mUploadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_web);
    }

    @Override
    public void selectPictureFilePath(String filePath, String pictureFromType) {
        if (TextUtils.isEmpty(filePath)) {
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }
        } else {
            Uri uri = Uri.fromFile(new File(filePath));
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(new Uri[]{uri});
            }
        }
    }

    @Override
    public void cancleSelectPicture(String pictureFromType) {
        if (mUploadMessage != null)
            mUploadMessage.onReceiveValue(null);
    }

    @Override
    public void onCreatPresenter() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        mBaseWebPbLoadingRrogress = findViewById(R.id.base_web_pb_loading_progress);
        mBaseWebFlWv = findViewById(R.id.base_web_fl_wv);
        mWebView = new WebView(getApplicationContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBaseWebFlWv.addView(mWebView, layoutParams);
        initWebView();
    }


    private WebViewClient mWebViewClient = new WebViewClient() {
        //确保网页在自己webview打开，而不是跳转到系统浏览器
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mCurrentUrl = url;
            view.loadUrl(url);
            LogUtils.i(LOG_TAG, "shouldOverrideUrlLoading   " + url);
            return true;
        }

        //更新历史记录
        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            LogUtils.i(LOG_TAG, "doUpdateVisitedHistory    " + url + "  isReload  " + isReload);
            if (mClearPreviousRecords) {
                view.clearHistory();
                mClearPreviousRecords = false;
            }
        }

        //应用程序重新请求网页数据
        @Override
        public void onFormResubmission(WebView view, Message dontResend, Message resend) {
            super.onFormResubmission(view, dontResend, resend);
            LogUtils.e("onFormResubmission" + "");
        }

        //在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            LogUtils.e("onLoadResource" + url);
        }

        //开始载入页面调用的，通常我们可以在这设定一个loading的页面，告诉用户程序在等待网络响应
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mBaseWebPbLoadingRrogress.setVisibility(VISIBLE);
        }

        //在页面加载结束时调用。同样道理，我们知道一个页面载入完成，于是我们可以关闭loading 条，切换程序动作
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mBaseWebPbLoadingRrogress.setVisibility(GONE);
        }

        //报告错误信息
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (errorCode != -6) {
                loadingError(view, failingUrl);
            }
        }

        //报告错误信息
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            String url = view.getUrl();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int errorCode = error.getErrorCode();
                if (errorCode != -6) {
                    loadingError(view, url);
                }
            }
        }
    };

    /**
     * 加载错误界面
     *
     * @param failingUrl
     */
    private void loadingError(WebView webView, String failingUrl) {
        if (TextUtils.isEmpty(mLoadingErrorData)) {
            try {
                AssetManager assets = getAssets();
                // TODO: 2018/8/2 网络异常或者加载失败设置本地网络异常界面
                InputStream open = assets.open("pageerror.html");
                mLoadingErrorData = FileUtil.readInStream(open);
                mLoadingErrorData = String.format(mLoadingErrorData, failingUrl);
                webView.loadDataWithBaseURL(null, mLoadingErrorData, "text/html", "UTF-8", null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            webView.loadDataWithBaseURL(null, mLoadingErrorData, "text/html", "UTF-8", null);
        }
    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        //通知应用程序当前网页加载的进度
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mBaseWebPbLoadingRrogress.setProgress(newProgress);
        }

        //获取网页title标题
        @Override
        public void onReceivedTitle(WebView view, String titleStr) {
            super.onReceivedTitle(view, titleStr);
            if (!TextUtils.isEmpty(titleStr)) {
                setTitle(titleStr);
            }
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams
                fileChooserParams) {
            openFileChooserImpl(filePathCallback);
            return true;
        }
    };


    /**
     * 从手机选择上文件
     *
     * @param uploadMsg 返回信息
     */
    public void openFileChooserImpl(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessage = uploadMsg;
        selectPictureFromAlumOrCamera();
    }


    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            WebView.HitTestResult hitTestResult = ((WebView) v).getHitTestResult();
            if (hitTestResult == null)
                return false;
            int type = hitTestResult.getType();
            switch (type) {
                case WebView.HitTestResult.PHONE_TYPE: // 处理拨号
                    LogUtils.i("onLongClick" + "处理拨号");
                    break;
                case WebView.HitTestResult.EMAIL_TYPE: // 处理Email
                    LogUtils.i("onLongClick" + "处理Email");
                    break;
                case WebView.HitTestResult.GEO_TYPE: //
                    LogUtils.i("onLongClick" + "GEO_TYPE");
                    break;
                case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
                    LogUtils.i("onLongClick" + "超链接");
                    break;
                case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                    LogUtils.i("onLongClick" + "SRC_IMAGE_ANCHOR_TYPE");
                    break;
                case WebView.HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项
                    LogUtils.i("onLongClick" + "处理长按图片的菜单项");
                    String imagUrl = hitTestResult.getExtra();
                    String fileName = FileUtil.getFileName(imagUrl);
                    String mSaveImagePath = BaseConfig.SYSTEM_PHOTO_PATH + fileName;
                    new CommonSelectDialog.Builder(BaseWebActivity.this)
                            .setData(new String[]{getString(R.string.save_to_phone), getString(R.string.cancle)})
                            .setOnItemClickListener(new MyDownClickListener(imagUrl, mSaveImagePath))
                            .build().show();
                    break;
                default:
                    break;
            }
            return false;
        }
    };


    /**
     * 初始化WebView
     */
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        //设置WebView是否允许执行JavaScript脚本，默认false，不允许。
        settings.setJavaScriptEnabled(true);
        //是否允许访问文件，默认允许
//        settings.setAllowFileAccess(true);
        //应用缓存API是否可用，默认值false,
//        settings.setAppCacheEnabled(true);
        //设置应用缓存文件的路径。
//        settings.setAppCachePath("");
        //设置WebView字体库字体，默认“cursive”
//        settings.setCursiveFontFamily("cursive");
        //设置默认固定的字体大小，默认为16，可取值1到72
//        settings.setDefaultFixedFontSize(16);
        //设置默认的字体大小，默认16，可取值1到72
//        settings.setDefaultFontSize(16);
        //设置默认的字符编码集，默认”UTF-8”.
        settings.setDefaultTextEncodingName("UTF-8");
        //设置页面上的文本缩放百分比，默认100。
//        settings.setTextZoom(100);
        mWebView.addJavascriptInterface(this, H5_TO_NATIVE_NAME);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setOnLongClickListener(mOnLongClickListener);
    }


    @Override
    public void initServiceData() {

    }


    private class MyDownClickListener implements CommonSelectDialog.OnItemClickListener {
        private String mImageUrl;
        private String mSaveImagePath;

        private MyDownClickListener(String imageUrl, String saveImagePath) {
            mImageUrl = imageUrl;
            mSaveImagePath = saveImagePath;
        }

        @Override
        public void onClickItem(View view, int position, SelectDialogBean selectBean) {
            RetrofitManager.getInstance().downFile(mImageUrl, mSaveImagePath, new OnDownFileObserver() {
                @Override
                public void onSuccess(DownFileBean t) {
                    ToastUtil.showMessage(R.string.save_success);
                    SystemUtil.scanPhoto(getApplicationContext(), mSaveImagePath);
                }

                @Override
                public void onDownProgress(long readLength, long contentLength, int percent) {

                }

                @Override
                public void onFail(Throwable throwable) {
                    ToastUtil.showMessage(R.string.save_fail + (throwable != null ? throwable.toString() : ""));
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.loadUrl("about:blank");
            mWebView.freeMemory();
            mWebView.pauseTimers();
            mWebView = null;
        }
        mBaseWebFlWv.removeAllViews();
    }
}
