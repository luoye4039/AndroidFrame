package com.seven.framework.net;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface FrameServiceApi {
    //上传文件
    @POST
    Observable<Response> uploadFile(@Url String url, @Body MultipartBody body);


    //下载文件
    @GET
    Observable<Response> downFile(@Url String url);
}
