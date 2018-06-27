package com.seven.framework.manager;

import android.support.annotation.NonNull;

import com.seven.framework.BuildConfig;
import com.seven.framework.manager.uploadordown.DownFileBean;
import com.seven.framework.manager.uploadordown.OnDownFileObserver;
import com.seven.framework.manager.uploadordown.OnUploadFileObserver;
import com.seven.framework.net.FrameServiceApi;
import com.seven.framework.utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private static String sBaseUrl;
    private Retrofit mDefaultRetrofit;

    private RetrofitManager() {
        initConfig();
    }

    private static class RetrofitManagerInstance {
        private static final RetrofitManager sRetrofitManager = new RetrofitManager();
    }

    public static RetrofitManager getInstance() {
        return RetrofitManagerInstance.sRetrofitManager;
    }

    /**
     * 初始化配置
     */
    private void initConfig() {
        if (BuildConfig.DEBUG) {
            sBaseUrl = "";
        } else {
            sBaseUrl = "";
        }
        initDefaultRetrofit();
    }

    /**
     * 初始化Retrofit
     */
    private void initDefaultRetrofit() {
        List<Interceptor> interceptors = new ArrayList<>();
        if (BuildConfig.DEBUG) {
            interceptors.add(new HttpLoggingInterceptor());
        }
        mDefaultRetrofit = new Retrofit.Builder()
                .baseUrl(sBaseUrl)
                .client(OkHttpManager.getInstance().getOkHttpClient(interceptors))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 获得指定的ServiceApi
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public <T> T getDefaultServiceApi(Class<T> serviceClass) {
        if (mDefaultRetrofit == null) {
            initConfig();
        }
        return mDefaultRetrofit.create(serviceClass);
    }

    /**
     * 创建指定URL的Retrofit
     *
     * @param baseUrl
     * @return
     */
    public Retrofit creatRetrofit(String baseUrl) {
        List<Interceptor> interceptors = new ArrayList<>();
        if (BuildConfig.DEBUG) {
            interceptors.add(new HttpLoggingInterceptor());
        }
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpManager.getInstance().getOkHttpClient(interceptors))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 创建指定URL的Retrofit
     *
     * @param baseUrl
     * @param okHttpClient
     * @return
     */
    public Retrofit creatRetrofit(String baseUrl, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }


    /**
     * 上传文件
     *
     * @param urlPath
     * @param parmas
     * @param filePaths
     * @return
     */
    public Observable<Response> uploadFiles(@NonNull String urlPath, Map<String, String> parmas, @NonNull List<String> filePaths) {
        String boundary = "xx--------------------------------------------------------------xx";
        MultipartBody.Builder builder = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM);
        builder = OkHttpManager.setUploadFileParmas(builder, parmas);
        for (String filePath : filePaths) {
            File file = new File(filePath);
            String fileName = FileUtil.getFileNameNoFormat(filePath);
            builder.addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("application/octet-stream"), file));
        }
        MultipartBody build = builder.build();
        return getDefaultServiceApi(FrameServiceApi.class).uploadFile(urlPath, build);
    }




    /**
     * 下载文件
     *
     * @param url
     * @param filePath
     * @param onDownFileObserver
     * @return
     */
    public void downFile(@NonNull final String url, @NonNull final String filePath, OnDownFileObserver onDownFileObserver) {
        getDefaultServiceApi(FrameServiceApi.class).downFile(url).flatMap(new Function<Response, ObservableSource<DownFileBean>>() {
            @Override
            public ObservableSource<DownFileBean> apply(final Response response) throws Exception {
                Observable<DownFileBean> downFileBeanObservable = Observable.create(new ObservableOnSubscribe<DownFileBean>() {
                    @Override
                    public void subscribe(ObservableEmitter<DownFileBean> emitter) throws Exception {
                        ResponseBody body = response.body();
                        if (response.code() == 200) {
                            if (body != null) {
                                long allLength = body.contentLength();
                                long currentLength = 0;
                                InputStream inputStream = body.byteStream();
                                FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
                                DownFileBean downFileBean = new DownFileBean(url, allLength, currentLength, 0, filePath);
                                byte[] buffer = new byte[1024];
                                int length = -1;
                                while ((length = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, length);
                                    currentLength += length;
                                    int percent = (int) ((float) currentLength / (float) allLength * 100);
                                    downFileBean.downLength = currentLength;
                                    downFileBean.downPercent = percent;
                                    emitter.onNext(downFileBean);
                                }
                                inputStream.close();
                                fileOutputStream.close();
                                downFileBean.isCompleted = true;
                                emitter.onNext(downFileBean);
                            }
                        } else {
                            emitter.onError(new Throwable(response.code() + "" + (body == null ? "" : body.toString())));
                        }
                    }
                });
                return downFileBeanObservable;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onDownFileObserver);
    }
}
