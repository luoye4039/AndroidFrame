package com.seven.framework.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by wangbin on 2016/11/22.
 */

public class SystemAppUtil {
    /**
     * 安装app
     *
     * @param context context
     * @param file    文件path
     */
    public static void installAPK(Context context, File file) {
        if (file == null || !file.exists()) {
            Log.i("te", "文件为空或者不存在");
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(getUriForFile(context, file),
                        "application/vnd.android.package-archive");
                context.startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
                context.startActivity(intent);
            }

        } catch (Exception e) {
            LogUtils.e(e.toString());
        }

    }

    /**
     * 卸载指定app
     *
     * @param context     context
     * @param packageName 包名
     */
    public static void uninstallApk(Context context, String packageName) {
        if (SystemUtil.isPackageExist(context, packageName)) {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
                    packageURI);
            context.startActivity(uninstallIntent);
        }else {
            LogUtils.e("未安装此应用");
        }
    }

    /**
     * 去查看指定包的app市场
     *
     * @param context context
     * @param pck     包名
     */
    public static void gotoMarket(Context context, String pck) {
        if (!SystemUtil.isHaveMarket(context)) {
            Toast.makeText(context, "你手机中没有安装应用市场！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + pck));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }


    /**
     * 打开app市场并显示本应用
     *
     * @param context context
     */
    public static void openAppInMarket(Context context) {
        if (context != null) {
            String pckName = context.getPackageName();
            try {
                gotoMarket(context, pckName);
            } catch (Exception ex) {
                try {
                    String otherMarketUri = "http://market.android.com/details?id=" + pckName;
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(otherMarketUri));
                    context.startActivity(intent);
                } catch (Exception e) {

                }
            }
        }
    }


    /**
     * 调用系统拨打电话
     *
     * @param context context
     * @param number  号码
     */
    public static void callPhone(Context context, String number) {
        Uri uri = Uri.parse("tel:" + number);
        Intent it = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(it);
    }

    /**
     * 打开系统拨号界面
     *
     * @param context context
     */
    public static void openDail(Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 发送短信
     *
     * @param context contex
     * @param smsBody 消息内容
     * @param tel     电话号码
     */
    public static void openSMS(Context context, String smsBody, String tel) {
        Uri uri = Uri.parse("smsto:" + tel);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", smsBody);
        context.startActivity(it);
    }

    /**
     * 打开短信界面
     *
     * @param context context
     */
    public static void openSendMsg(Context context) {
        Uri uri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 发送邮件
     *
     * @param context
     * @param subject 主题
     * @param content 内容
     * @param emails  邮件地址
     */
    public static void sendEmail(Context context, String subject,
                                 String content, String... emails) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822"); // 真机
            intent.putExtra(Intent.EXTRA_EMAIL, emails);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, content);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 调用系统安装了的应用分享
     *
     * @param context context
     * @param title   title
     * @param url     url
     */
    public static void showSystemShareOption(Context context,
                                             final String title, final String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
        intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
        context.startActivity(Intent.createChooser(intent, "选择分享"));
    }

    /**
     * 打开系统相机
     *
     * @param activity
     * @param pictureSavePath
     * @param requestCode
     */
    public static void openCamera(Activity activity, String pictureSavePath, int requestCode) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File pictureSaveFile = new File(pictureSavePath);
                Uri imageUri = getUriForFile(activity, pictureSaveFile);//通过FileProvider创建一个content类型的Uri
                Intent cameraIntent = new Intent();
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
                activity.startActivityForResult(cameraIntent, requestCode);
            } else {
                File pictureSaveFile = new File(pictureSavePath);
                Uri photoUri = Uri.fromFile(pictureSaveFile);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                activity.startActivityForResult(cameraIntent, requestCode);
            }
        } catch (Exception e) {
            ToastUtil.showMessage("请打开相机权限");
        }
    }

    /**
     * 打开系统相册
     *
     * @param activity
     * @param requestCode
     */
    public static void openPhotoAlum(Activity activity, int requestCode) {
        try {
            Intent photoAlumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoAlumIntent.setType("image/*");
            activity.startActivityForResult(photoAlumIntent, requestCode);
        } catch (Exception e) {
            ToastUtil.showMessage("打开相册失败，请检查权限是否打开");
        }
    }

    /**
     * 裁剪指定图片
     *
     * @param activity    activity
     * @param size        裁剪尺寸
     * @param filePath    裁剪图片文件
     * @param tarFilePath 裁剪图片文件
     * @param requestCode 请求code
     */
    public static void cropPhoto(Activity activity, int size, File filePath, File tarFilePath, int requestCode) {
        try {
            String fileCompressFormat = TextUtils.isEmpty(FileUtil.getFileFormat(filePath.getAbsolutePath())) ? Bitmap.CompressFormat.PNG.toString() : FileUtil.getFileFormat(filePath.getAbsolutePath());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri imageUri = getUriForFile(activity, filePath);//通过FileProvider创建一个content类型的Uri
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(imageUri, "image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tarFilePath));
                intent.putExtra("outputFormat", fileCompressFormat);
                intent.putExtra("noFaceDetection", true); // no face detection
                activity.startActivityForResult(intent, requestCode);
            } else {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(Uri.fromFile(filePath), "image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", size);
                intent.putExtra("outputY", size);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tarFilePath));
                intent.putExtra("return-data", false);
                intent.putExtra("outputFormat", fileCompressFormat);
                activity.startActivityForResult(intent, requestCode);
            }
        } catch (Exception e) {
            ToastUtil.showMessage("此文件无法裁剪");
        }

    }


    /**
     * 打开系统相机
     *
     * @param fragment
     * @param pictureSavePath
     * @param requestCode
     */
    public static void openCamera(Fragment fragment, String pictureSavePath, int requestCode) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File pictureSaveFile = new File(pictureSavePath);
                Uri imageUri = getUriForFile(fragment.getActivity().getApplicationContext(), pictureSaveFile);//通过FileProvider创建一个content类型的Uri
                Intent cameraIntent = new Intent();
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
                fragment.startActivityForResult(cameraIntent, requestCode);
            } else {
                File pictureSaveFile = new File(pictureSavePath);
                Uri photoUri = Uri.fromFile(pictureSaveFile);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                fragment.startActivityForResult(cameraIntent, requestCode);
            }
        } catch (Exception e) {
            ToastUtil.showMessage("请打开相机权限");
        }
    }

    /**
     * 打开系统相册
     *
     * @param fragment
     * @param requestCode
     */
    public static void openPhotoAlum(Fragment fragment, int requestCode) {
        try {
            Intent photoAlumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoAlumIntent.setType("image/*");
            fragment.startActivityForResult(photoAlumIntent, requestCode);
        } catch (Exception e) {
            ToastUtil.showMessage("打开相册失败，请检查权限是否打开");
        }
    }

    /**
     * 裁剪指定图片
     *
     * @param fragment    activity
     * @param size        裁剪尺寸
     * @param filePath    裁剪图片文件
     * @param tarFilePath 裁剪图片文件
     * @param requestCode 请求code
     */
    public static void cropPhoto(Fragment fragment, int size, File filePath, File tarFilePath, int requestCode) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri outputUri = getUriForFile(fragment.getActivity().getApplicationContext(), tarFilePath);
                Uri imageUri = getUriForFile(fragment.getActivity().getApplicationContext(), filePath);//通过FileProvider创建一个content类型的Uri
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(imageUri, "image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
                intent.putExtra("noFaceDetection", true); // no face detection
                fragment.startActivityForResult(intent, requestCode);
            } else {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(Uri.fromFile(filePath), "image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", size);
                intent.putExtra("outputY", size);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tarFilePath));
                intent.putExtra("return-data", false);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
                fragment.startActivityForResult(intent, requestCode);
            }
        } catch (Exception e) {
            ToastUtil.showMessage("此文件无法裁剪");
        }

    }


    /**
     * 获取文件提供者
     *
     * @param context
     * @param file
     * @return
     */
    private static Uri getUriForFile(Context context, File file) {
        String authority = context.getPackageName() + ".provider";
        return FileProvider.getUriForFile(context, authority, file);

    }

}
