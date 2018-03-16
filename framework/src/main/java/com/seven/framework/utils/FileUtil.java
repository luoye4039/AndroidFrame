package com.seven.framework.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具
 * Created by wangbin on 2016/11/22.
 */

public class FileUtil {
    /**
     * 指定文件夹创建文件
     *
     * @param folderPath 文件夹path
     * @param fileName   文件名
     * @return file
     */
    public static File createFile(String folderPath, String fileName) {
        File destDir = new File(folderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return new File(folderPath, fileName);
    }

    /**
     * 根据文件绝对路径获取文件名（带扩展名）
     *
     * @param filePath 文件path
     * @return 文件名
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件name
     * @return
     */
    public static String getFileFormat(String fileName) {
        if (TextUtils.isEmpty(fileName))
            return "";
        int point = 0;
        if (fileName.contains(".")) {
            point = fileName.lastIndexOf('.');
        } else {
            return "";
        }
        return fileName.substring(point + 1);
    }

    /**
     * 根据文件的绝对路径获取文件名但不包含扩展名
     *
     * @param filePath 文件path
     * @return file name
     */
    public static String getFileNameNoFormat(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        int point = filePath.lastIndexOf('.');
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1,
                point);
    }


    /**
     * 获取文件大小
     *
     * @param filePath 文件path
     * @return 文件大小
     */
    public static long getFileSize(String filePath) {
        long size = 0;
        if (TextUtils.isEmpty(filePath))
            return size;
        File file = new File(filePath);
        if (file.exists()) {
            size = file.length();
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileSize 文件大小
     * @return B/KB/MB/GB 返回合适的大小字符串
     */
    public static String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取目录文件大小
     *
     * @param fileFolder 文件夹
     * @return 所有文件大小
     */
    public static long getDirSize(File fileFolder) {
        if (fileFolder == null) {
            return 0;
        }
        if (!fileFolder.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = fileFolder.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 获取目录文件个数
     *
     * @param fileFolder 文件夹
     * @return 文件数量
     */
    public long getFileList(File fileFolder) {
        long count = 0;
        File[] files = fileFolder.listFiles();
        count = files.length;
        for (File file : files) {
            if (file.isDirectory()) {
                count = count + getFileList(file);// 递归
                count--;
            }
        }
        return count;
    }

    /**
     * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
     *
     * @param context  context
     * @param fileName 文件名
     * @param content  文本内容
     */
    public static void writeToDataFile(Context context, String fileName, String content) {
        if (TextUtils.isEmpty(content))
            return;
        try {
            FileOutputStream fos = context.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String readDataFile(Context context, String fileName) {
        try {
            FileInputStream in = context.openFileInput(fileName);
            return readInStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将流中的数据转成字符串
     *
     * @param inStream
     * @return
     */
    public static String readInStream(InputStream inStream) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
            outStream.close();
            inStream.close();
            return outStream.toString();
        } catch (IOException e) {
            Log.i("FileTest", e.getMessage());
        }
        return "";
    }

    /**
     * 读取指定文本文件的内容
     *
     * @param filePath
     * @return
     */
    public static String getFileString(String filePath) {
        File file = new File(filePath);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            return readInStream(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 删除目录(包括：目录里的所有文件)
     *
     * @param filePath 文件path
     * @return 删除结果
     */
    public static boolean deleteDirectory(String filePath) {
        boolean status;
        SecurityManager checker = new SecurityManager();
        if (!TextUtils.isEmpty(filePath)) {
            File newPath = new File(filePath);
            checker.checkDelete(newPath.toString());
            if (newPath.isDirectory()) {
                String[] listfile = newPath.list();
                try {
                    for (int i = 0; i < listfile.length; i++) {
                        File deletedFile = new File(newPath.toString() + "/"
                                + listfile[i].toString());
                        deletedFile.delete();
                    }
                    newPath.delete();
                    status = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    status = false;
                }

            } else
                status = false;
        } else
            status = false;
        return status;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件path
     * @return 删除结果
     */
    public static boolean deleteFile(String filePath) {
        boolean status;
        SecurityManager checker = new SecurityManager();
        if (!TextUtils.isEmpty(filePath)) {
            File newPath = new File(filePath);
            checker.checkDelete(newPath.toString());
            if (newPath.isFile()) {
                try {
                    newPath.delete();
                    status = true;
                } catch (SecurityException se) {
                    se.printStackTrace();
                    status = false;
                }
            } else
                status = false;
        } else
            status = false;
        return status;
    }

    /**
     * 重命名
     *
     * @param oldPath 旧地址
     * @param newPath 新地址
     * @return 结果
     */
    public static boolean reNamePath(String oldPath, String newPath) {
        File f = new File(oldPath);
        return f.renameTo(new File(newPath));
    }

    /**
     * 删除文件
     *
     * @param filePath 文件Path
     */
    public static boolean deleteFileWithPath(String filePath) {
        SecurityManager checker = new SecurityManager();
        File file = new File(filePath);
        checker.checkDelete(filePath);
        if (file.isFile()) {
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * 清空一个文件夹
     *
     * @param filePath path
     */
    public static void clearFileWithPath(String filePath) {
        List<File> files = listPathFiles(filePath);
        if (files.isEmpty()) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                clearFileWithPath(f.getAbsolutePath());
            } else {
                f.delete();
            }
        }
    }

    /**
     * 获取一个文件夹下的所有文件
     *
     * @param root 根目录
     * @return
     */
    public static List<File> listPathFiles(String root) {
        List<File> allDir = new ArrayList<File>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        File[] files = path.listFiles();
        for (File f : files) {
            if (f.isFile())
                allDir.add(f);
            else
                listPath(f.getAbsolutePath());
        }
        return allDir;
    }

    /**
     * 列出root目录下所有子目录
     *
     * @param root 根目录
     * @return 绝对路径
     */
    public static List<String> listPath(String root) {
        List<String> allDir = new ArrayList<String>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        // 过滤掉以.开始的文件夹
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (f.isDirectory() && !f.getName().startsWith(".")) {
                    allDir.add(f.getAbsolutePath());
                }
            }
        }
        return allDir;
    }

    /**
     * 从url中获取file
     *
     * @param context context
     * @param uri     uri
     * @return
     */
    public static File getFileByUri(Context context, Uri uri) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA}, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index != 0) {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();

            return new File(path);
        } else {
            return null;
        }
        return null;
    }

    /**
     * 将字节保存到本地
     *
     * @param filePath
     * @param bytes
     */
    public static void saveFile(byte[] bytes, String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists())
                file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 将读取的流写到文件中
     *
     * @param filePath
     * @param inputStream
     */
    public static void writeToFile(String filePath, InputStream inputStream) {
        int bytesWritten = 0;
        int byteCount = 0;
        byte[] bytes = new byte[1024];
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filePath);
            while ((byteCount = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, bytesWritten, byteCount);
                bytesWritten += byteCount;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 复制文件
     *
     * @param srcPath
     * @param tarPath
     * @return
     */
    public static boolean copyFile(String srcPath, String tarPath) {
        File srcFile = new File(srcPath);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            return false;
        } else if (!srcFile.isFile()) {
            return false;
        }
        // 判断目标文件是否存在
        File destFile = new File(tarPath);
        if (destFile.exists()) {
            // 如果目标文件存在并允许覆盖
            // 删除已经存在的目标文件，无论目标文件是目录还是单个文件
            new File(tarPath).delete();
        } else {
            // 如果目标文件所在目录不存在，则创建目录
            if (!destFile.getParentFile().exists()) {
                // 目标文件所在目录不存在
                if (!destFile.getParentFile().mkdirs()) {
                    // 复制文件失败：创建目标文件所在目录失败
                    return false;
                }
            }
        }
        // 复制文件
        int byteread = 0; // 读取的字节数
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            while ((byteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteread);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
