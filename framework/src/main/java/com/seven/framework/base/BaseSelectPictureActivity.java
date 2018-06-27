package com.seven.framework.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.seven.framework.base.mvp.BasePresenter;
import com.seven.framework.base.mvp.BaseView;
import com.seven.framework.permissions.rxpermissions.RxPermissions;

import java.io.File;
import java.util.List;

import io.reactivex.functions.Consumer;

public abstract class BaseSelectPictureActivity<V extends BaseView, P extends BasePresenter<V>> extends BaseActivity<V, P> {

    private static final int MY_CAMERA_REQUEST = 0x12;
    private static final int MY_PHOTO_ALUM_REQUEST = 0x13;
    private static final int MY_CROP_PICTURE_REQUEST = 0x14;
    private static final int MY_CROP_PICTURE_MATISSE = 0x15;
    private RxPermissions mRxPermissions;
    private String mCameraFilePath;
    private File mTarFile;

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
     * 选择的图片地址和来源
     *
     * @param filePaths
     * @param pictureFromType
     */
    public abstract void selectPictureFilePath(List<String> filePaths, String pictureFromType);

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
                           /* String fileName = DateFormat.format("yyyyMMddhhmmss", Calendar.getInstance(Locale.CHINA)) + ".png";
                            mCameraFilePath = BaseConfig.SYSTEM_PHOTO_PATH + fileName;
                            SystemAppUtil.openCamera(RootSelectPictureActivity.this, BaseConfig.SYSTEM_PHOTO_PATH + fileName, MY_CAMERA_REQUEST);*/
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
//                            SystemAppUtil.openPhotoAlum(RootSelectPictureActivity.this, MY_PHOTO_ALUM_REQUEST);
                        }
                    }
                });
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
//                            int canCropSize = BitmapUtil.pictureCanCropSize(getApplicationContext(), filePath);
//                            mTarFile = new File(BaseConfig.CROP_PHOTO_PATH, "crop_" + FileUtil.getFileName(filePath));
//                            SystemAppUtil.cropPhoto(RootSelectPictureActivity.this, canCropSize, new File(filePath), mTarFile, MY_CROP_PICTURE_REQUEST);
                        }
                    }
                });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取来自相册的信息
     /*   if (requestCode == MY_PHOTO_ALUM_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            File file = FileUtil.getFileByUri(RootSelectPictureActivity.this, fileUri);
            if (file != null && file.exists() && file.length() > 0) {
                selectPictureFilePath(file.getAbsolutePath(), "Alum");
            }
            return;
        }
        //信息来自相机
        if (requestCode == MY_CAMERA_REQUEST && resultCode == RESULT_OK) {
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
        if (requestCode == MY_CROP_PICTURE_REQUEST && resultCode == RESULT_OK) {
            Uri fileUri = data.getData();
            final File file;
            if (fileUri == null) {
                file = mTarFile;
            } else {
                file = FileUtil.getFileByUri(RootSelectPictureActivity.this, fileUri);
            }
            if (file != null && file.exists() && file.length() > 0) {
                selectPictureFilePath(file.getAbsolutePath(), "CropImage");
                SystemUtil.scanPhoto(getApplicationContext(), file.getAbsolutePath());
            }
            return;
        }
        //MATISSE选择图片
        if (requestCode == MY_CROP_PICTURE_MATISSE && resultCode == RESULT_OK) {
//            List<Uri> uris = Matisse.obtainResult(data);
            List<String> strings = Matisse.obtainPathResult(data);
            selectPictureFilePath(strings, "Matisse");
            if (strings != null && strings.size() > 0) {
                for (String filePtah : strings) {
                    SystemUtil.scanPhoto(getApplicationContext(), filePtah);
                }
            }
            return;
        }*/

        if (requestCode == MY_CROP_PICTURE_MATISSE && RESULT_CANCELED == resultCode) {
            cancleSelectPicture("Matisse");
            return;
        }
        if (requestCode == MY_PHOTO_ALUM_REQUEST && RESULT_CANCELED == resultCode) {
            cancleSelectPicture("Alum");
            return;
        }

        if (requestCode == MY_CAMERA_REQUEST && RESULT_CANCELED == resultCode) {
            cancleSelectPicture("Camera");
            return;
        }
        if (requestCode == MY_CROP_PICTURE_REQUEST && RESULT_CANCELED == resultCode) {
            cancleSelectPicture("CropImage");
        }
    }

}
