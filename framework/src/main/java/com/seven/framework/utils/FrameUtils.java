package com.seven.framework.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用工具类
 * Created by wangbin on 2016/9/6.
 */
public class FrameUtils {
    /**
     * 获取文本的MD5值
     *
     * @param value 字符串
     * @return 字符串
     */
    public static String getStrToMD5(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] e = md.digest(value.getBytes());
            return toHexString(e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return value;
        }
    }

    /**
     * 获取字节的MD5值
     *
     * @param bytes 字节
     * @return
     */
    public static String getBytesToMD5(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] e = md.digest(bytes);
            return toHexString(e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取文件的MD5值
     *
     * @param filePath 文件地址
     * @return
     */
    public static String getFileToMD5(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return "";
        File file = new File(filePath);
        if (!file.isFile())
            return "";
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return toHexString(digest.digest());
    }


    /**
     * 将MD5的字节转成字符串
     *
     * @param bytes 字节
     * @return
     */
    private static String toHexString(byte bytes[]) {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (int n = 0; n < bytes.length; n++) {
            stmp = Integer.toHexString(bytes[n] & 0xff);
            if (stmp.length() == 1)
                hs.append("0").append(stmp);
            else
                hs.append(stmp);
        }
        return hs.toString();
    }

    /**
     * 正则表达判断是否是手机号
     *
     * @param mobiles 手机号
     * @return
     */
    public static boolean isPhoneNum(String mobiles) {
//        Pattern p = Pattern.compile("^[1][3-5|7-8]\\d{9}$");
        Pattern p = Pattern.compile("^[1]\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 正则表达�?判断是否是email
     *
     * @param email 邮箱地址
     * @return
     */
    public static boolean isEmail(String email) {
        String stre = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(stre);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 正则表达式判断是否为汉字
     *
     * @param text 文本
     * @return
     */
    public static boolean isChineseCharacters(String text) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(text);
        return m.matches();

    }

    /**
     * 正则表达式判断是否为数字
     */
    public static boolean isNumDigit(String str) {
        String REG_DIGIT = "[0-9]*";
        return str.matches(REG_DIGIT);
    }

    /**
     * 正则表达式判断是否为字符
     */
    public static boolean isChar(String str) {
        String REG_CHAR = "[a-zA-Z]*";
        return str.matches(REG_CHAR);
    }

    /**
     * 2.判断字符串的首字符是否为字母
     *
     * @param content 文本内容
     * @return
     */
    public static boolean startIsChar(String content) {
        char c = content.charAt(0);
        int i = (int) c;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 得到参数列表字符串
     *
     * @param paramValues 参数map对象
     * @return 参数列表字符串
     */
    public static String getParams(Map<String, String> paramValues) {
        String params = "";
        Set<String> key = paramValues.keySet();
        String beginLetter = "";
        for (Iterator<String> it = key.iterator(); it.hasNext(); ) {
            String s = (String) it.next();
            if (params.equals("")) {
                params += beginLetter + s + "=" + paramValues.get(s);
            } else {
                params += "&" + s + "=" + paramValues.get(s);
            }
        }
        return params;
    }

    /**
     * int  转IP地址
     *
     * @param i
     * @return
     */
    public static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

}
