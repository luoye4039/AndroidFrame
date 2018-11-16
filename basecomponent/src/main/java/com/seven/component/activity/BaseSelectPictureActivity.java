package com.seven.component.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.seven.component.R;
import com.seven.component.dialog.CommonSelectDialog;
import com.seven.framework.base.FrameworkActivity;
import com.seven.framework.base.BaseConfig;
import com.seven.framework.base.mvp.BasePresenter;
import com.seven.framework.base.mvp.BaseView;
import com.seven.framework.entity.SelectDialogBean;
import com.seven.framework.permissions.rxpermissions.RxPermissions;
import com.seven.framework.utils.BitmapUtil;
import com.seven.framework.utils.DateUtil;
import com.seven.framework.utils.FileUtil;
import com.seven.framework.utils.SystemAppUtil;
import com.seven.framework.utils.SystemUtil;

import java.io.File;

import io.reactivex.functions.Consumer;

public abstract class BaseSelectPictureActivity<V extends BaseView, P extends BasePresenter<V>> extends BaseActivity<V, P> {

    private static final int MY_CAMERA_REQUEST = 0x12;
    private static final int MY_PHOTO_ALUM_REQUEST = 0x13;
    private static final int MY_CROP_PICTURE_REQUEST = 0x14;
    private RxPermissions mRxPermissions;
    private String mCameraFilePath;
    private File mTarFile;
    private CommonSelectDialog mCommonSelectDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRxPermissions = new RxPermissions(this);
    }

    /**
     * 选择的图片地址和来源
     *
     * @param filePath
     * @param pictureFromType
     */
    public abstract void selectPictureFilePath(String filePath, String pictureFromType);

    /**
     * 取消和取消来源
     *
     * @param pictureFromType
     */
    public abstract void cancleSelectPicture(String pictureFromType);


    @SuppressLint("CheckResult")
    public void selectPictureFromCamera() {
        mRxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            String fileName = DateUtil.formLocalTime("yyyyMMddhhmmss", System.currentTimeMillis()) + ".png";
                            mCameraFilePath = BaseConfig.SYSTEM_PHOTO_PATH + fileName;
                            SystemAppUtil.openCamera(BaseSelectPictureActivity.this, BaseConfig.SYSTEM_PHOTO_PATH + fileName, MY_CAMERA_REQUEST);
                        }
                    }
                });
    }

    /**
     * 从相册选择图片
     */
    @SuppressLint("CheckResult")
    public void selectPictureFromAlum() {
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            SystemAppUtil.openPhotoAlum(BaseSelectPictureActivity.this, MY_PHOTO_ALUM_REQUEST);
                        }
                    }
                });
    }

    /**
     * 从相册或者相机种获取图片
     */
    public void selectPictureFromAlumOrCamera() {
        mCommonSelectDialog = new CommonSelectDialog.Builder(this)
                .setData(new String[]{getString(R.string.ablum), getString(R.string.camera), getString(R.string.cancle)})
                .setOnItemClickListener(new CommonSelectDialog.OnItemClickListener() {
                    @Override
                    public void onClickItem(View view, int position, SelectDialogBean selectBean) {
                        switch (position) {
                            case 0:
                                selectPictureFromAlum();
                                break;
                            case 1:
                                selectPictureFromCamera();
                                break;
                        }
                        mCommonSelectDialog = null;
                    }
                }).build();


    }

    /**
     * 从相册选择图片
     */
    @SuppressLint("CheckResult")
    public void cropPicture(final String filePath) {
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            int canCropSize = BitmapUtil.pictureCanCropSize(getApplicationContext(), filePath);
                            mTarFile = new File(BaseConfig.PICTURE_PATH, "crop_" + FileUtil.getFileName(filePath));
                            SystemAppUtil.cropPhoto(BaseSelectPictureActivity.this, canCropSize, new File(filePath), mTarFile, MY_CROP_PICTURE_REQUEST);
                        }
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取来自相册的信息
        if (requestCode == MY_PHOTO_ALUM_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            File file = FileUtil.getFileByUri(BaseSelectPictureActivity.this, fileUri);
            if (file != null && file.exists() && file.length() > 0) {
                selectPictureFilePath(file.getAbsolutePath(), "Alum");
            }
            return;
        }
        //信息来自相机
        if (requestCode == MY_CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (TextUtils.isEmpty(mCameraFilePath))
                return;
            File file = new File(mCameraFilePath);
            if (file.exists() && file.length() > 0) {
                selectPictureFilePath(file.getAbsolutePath(), "Camera");
                SystemUtil.scanPhoto(getApplicationContext(), file.getAbsolutePath());
            }
            return;
        }

        //信息来自剪切图片
        if (requestCode == MY_CROP_PICTURE_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri fileUri = data.getData();
            final File file;
            if (fileUri == null) {
                file = mTarFile;
            } else {
                file = FileUtil.getFileByUri(BaseSelectPictureActivity.this, fileUri);
            }
            if (file != null && file.exists() && file.length() > 0) {
                selectPictureFilePath(file.getAbsolutePath(), "CropImage");
                SystemUtil.scanPhoto(getApplicationContext(), file.getAbsolutePath());
            }
            return;
        }
        if (requestCode == MY_PHOTO_ALUM_REQUEST && Activity.RESULT_CANCELED == resultCode) {
            cancleSelectPicture("Alum");
            return;
        }
        if (requestCode == MY_CAMERA_REQUEST && Activity.RESULT_CANCELED == resultCode) {
            cancleSelectPicture("Camera");
            return;
        }
        if (requestCode == MY_CROP_PICTURE_REQUEST && Activity.RESULT_CANCELED == resultCode) {
            cancleSelectPicture("CropImage");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCommonSelectDialog != null && mCommonSelectDialog.isShowing())
            mCommonSelectDialog.dismiss();
    }
}
