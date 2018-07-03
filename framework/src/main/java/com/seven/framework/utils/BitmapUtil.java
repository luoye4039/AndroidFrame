package com.seven.framework.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * bitmap工具类
 */

public class BitmapUtil {

    /**
     * 保持图片到本地
     *
     * @param bitmap    图片
     * @param imagePath 保存地址
     * @return true 保存成功 false 保存失败
     */
    public static void saveBitmapToLocal(final Bitmap bitmap, final String imagePath) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                File file = new File(imagePath);
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        emitter.onNext(false);
                    }
                }
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
                bufferedOutputStream.close();
                emitter.onNext(true);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 保持图片到本地
     *
     * @param bitmap    图片
     * @param imagePath 保存地址
     * @param observer  监听结果
     * @return true 保存成功 false 保存失败
     */
    public static void saveBitmapToLocal(final Bitmap bitmap, final String imagePath, Observer<Boolean> observer) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                File file = new File(imagePath);
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        emitter.onNext(false);
                    }
                }
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
                bufferedOutputStream.close();
                emitter.onNext(true);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            LogUtils.e(e.toString());
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }


    /**
     * 压缩图片不更换路径
     *
     * @param context     上下文
     * @param srcFilePath 源文件地址
     * @return 压缩后自主
     */
    public static String compressOnlyScanle(Context context, String srcFilePath) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcFilePath, opts);
        int size = bitmapScaling(context, opts);
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = size;
        if (size > 1) {
            Bitmap bitmap = BitmapFactory.decodeFile(srcFilePath, opts);
            saveBitmapToLocal(bitmap, srcFilePath);
        }
        return srcFilePath;
    }


    /**
     * 压缩图片不更换路径
     *
     * @param context     上下文
     * @param srcFilePath 源文件地址
     * @param observer    源文件地址
     * @return 压缩后自主
     */
    public static void compressOnlyScanle(final Context context, final String srcFilePath, Observer<String> observer) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String s = compressOnlyScanle(context, srcFilePath);
                emitter.onNext(s);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    /**
     * 压缩图片并将图片另存到tarPath文件夹中
     *
     * @param context     上下文
     * @param srcFilePath 源文件地址
     * @param tarPath     目标文件夹地址
     * @return 压缩后自主
     */
    public static String compressOnlyScanle(Context context, String srcFilePath, String tarPath) {
        String endPath = "";
        String fileFormat = FileUtil.getFileFormat(srcFilePath);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcFilePath, opts);
        int size = bitmapScaling(context, opts);
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = size;
        if (size > 1) {
            Bitmap bitmap = BitmapFactory.decodeFile(srcFilePath, opts);
            String name = FrameUtils.getStrToMD5(System.currentTimeMillis() + "");
            if (TextUtils.isEmpty(fileFormat)) {
                endPath = tarPath + name + ".png";
            } else {
                endPath = tarPath + name + "." + fileFormat;
            }
            saveBitmapToLocal(bitmap, endPath);
        } else {
            endPath = srcFilePath;
        }
        return endPath;
    }

    /**
     * 压缩图片并将图片另存到tarPath文件夹中
     *
     * @param context     上下文
     * @param srcFilePath 源文件地址
     * @param tarPath     目标文件夹地址
     * @return 压缩后自主
     */
    public static void compressOnlyScanle(final Context context, final String srcFilePath, final String tarPath, Observer<String> observer) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String s = compressOnlyScanle(context, srcFilePath, tarPath);
                emitter.onNext(s);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    /**
     * 压缩图片文件到小于指定最大值
     *
     * @param context     上下文
     * @param srcFilePath 源文件地址
     * @return 压缩后自主
     */
    public static String compressToAssignSize(Context context, String srcFilePath, long maxKBSize) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcFilePath, opts);
        int size = bitmapScaling(context, opts);
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = size;
        bitmap = BitmapFactory.decodeFile(srcFilePath, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length > maxKBSize * 1024) {
            baos.reset();
            quality -= 5;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }
        try {
            baos.writeTo(new FileOutputStream(srcFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return srcFilePath;
    }

    /**
     * 压缩图片文件到小于指定最大值
     *
     * @param context     上下文
     * @param srcFilePath 源文件地址
     * @return 压缩后自主
     */
    public static void compressToAssignSize(final Context context, final String srcFilePath, final long maxKBSize, Observer<String> observer) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String s = compressToAssignSize(context, srcFilePath, maxKBSize);
                emitter.onNext(s);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    /**
     * 压缩图片
     *
     * @param context     上下文
     * @param srcFilePath 源文件地址
     * @param tarPath     目标文件夹地址
     * @return 压缩后自主
     */
    public static String compressToAssignSize(Context context, String srcFilePath, String tarPath, long maxKBSize) {
        String endPath = "";
        String fileFormat = FileUtil.getFileFormat(srcFilePath);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcFilePath, opts);
        int size = bitmapScaling(context, opts);
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = size;
        bitmap = BitmapFactory.decodeFile(srcFilePath, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length > maxKBSize * 1024) {
            baos.reset();
            quality -= 5;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }
        try {
            String name = FrameUtils.getStrToMD5(System.currentTimeMillis() + "");
            if (TextUtils.isEmpty(fileFormat)) {
                endPath = tarPath + name + ".png";
            } else {
                endPath = tarPath + name + "." + fileFormat;
            }
            baos.writeTo(new FileOutputStream(endPath));
        } catch (Exception e) {
            endPath = "";
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return endPath;
    }

    /**
     * 压缩图片
     *
     * @param context     上下文
     * @param srcFilePath 源文件地址
     * @param tarPath     目标文件夹地址
     * @return 压缩后自主
     */
    public static void compressToAssignSize(final Context context, final String srcFilePath, final String tarPath, final long maxKBSize, Observer<String> observer) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String s = compressToAssignSize(context, srcFilePath,tarPath, maxKBSize);
                emitter.onNext(s);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    /**
     * 根据当前手机屏幕宽高获取bitmap的缩放比例
     *
     * @param context
     * @param opts
     * @return
     */
    public static int bitmapScaling(Context context, BitmapFactory.Options opts) {
        DisplayMetrics displayMetrics = DeviceInfo.getDisplayMetrics(context);
        float windowHeight = displayMetrics.heightPixels;
        float windowWidth = displayMetrics.widthPixels;
        int bitmapWidth = opts.outWidth;
        int bitmapHeight = opts.outHeight;
        int size = 0;
        if (bitmapWidth <= windowWidth && bitmapHeight <= windowHeight) {
            size = 1;
        } else {
            double scale = bitmapWidth >= bitmapHeight ? bitmapWidth / windowWidth : bitmapHeight / windowHeight;
            double log = Math.log(scale) / Math.log(2);
            double logCeil = Math.ceil(log);
            size = (int) Math.pow(2, logCeil);
        }
        return size;
    }

    /**
     * 根据流创建 制定宽高的bitmap
     *
     * @param inputStream
     * @param width
     * @param height
     * @return
     */
    public static Bitmap creatBitmap(InputStream inputStream, int width, int height) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, new Rect(0, 0, width, height), opts);
        float windowHeight = height;
        float windowWidth = width;
        int bitmapWidth = opts.outWidth;
        int bitmapHeight = opts.outHeight;
        int size = 0;
        if (bitmapWidth <= windowWidth && bitmapHeight <= windowHeight) {
            size = 1;
        } else {
            double scale = bitmapWidth >= bitmapHeight ? bitmapWidth / windowWidth : bitmapHeight / windowHeight;
            double log = Math.log(scale) / Math.log(2);
            double logCeil = Math.ceil(log);
            size = (int) Math.pow(2, logCeil);
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = size;
        return BitmapFactory.decodeStream(inputStream, new Rect(0, 0, width, height), opts);
    }

    /**
     * 根据文件地址生成 bitmap
     *
     * @param filePath 文件地址
     * @param width    宽
     * @param height   高
     * @return
     */
    public static Bitmap creatBitmap(String filePath, int width, int height) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(fileInputStream, new Rect(0, 0, width, height), opts);
            float windowHeight = height;
            float windowWidth = width;
            int bitmapWidth = opts.outWidth;
            int bitmapHeight = opts.outHeight;
            int size = 0;
            if (bitmapWidth <= windowWidth && bitmapHeight <= windowHeight) {
                size = 1;
            } else {
                double scale = bitmapWidth >= bitmapHeight ? bitmapWidth / windowWidth : bitmapHeight / windowHeight;
                double log = Math.log(scale) / Math.log(2);
                double logCeil = Math.ceil(log);
                size = (int) Math.pow(2, logCeil);
            }
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = size;
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream, new Rect(0, 0, width, height), opts);
            fileInputStream.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable drawable
     * @return bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
                .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 将bitmap转化为drawable
     *
     * @param bitmap bitmap
     * @return drawable
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }


    /**
     * 裁剪图片的尺寸
     *
     * @param context  上下文
     * @param filePath 图片地址
     * @return 裁剪尺寸
     */
    public static int pictureCanCropSize(Context context, String filePath) {
        int[] appScreenSize = DeviceInfo.getAppScreenSize(context);
        int screenWidth = appScreenSize[0];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;
        int bitmapMin = Math.min(bitmapWidth, bitmapHeight);
        if (screenWidth > bitmapMin) {
            return (int) (bitmapMin * 0.6);
        } else {
            return (int) (screenWidth * 0.4);
        }
    }

}
