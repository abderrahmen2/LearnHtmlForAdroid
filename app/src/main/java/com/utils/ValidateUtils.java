package com.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 本类是验证类
 * <p>
 * 对内容进行验证
 * <p>
 * Created by Novice on 2017/5/2.
 */

public class ValidateUtils {


    /**
     * 判断字符串是否符合规格：只能输入中文，下划线，数字和英文字母
     * 判断字符串长度是否为空
     * 判断字符串是否符合最大，最短长度
     *
     * @param str 原字符串
     * @return true 表示符合规则，false表示不符合
     */
    public static ResultSimple msIsStrRule(String str, int minLength, int maxLength) {

        if ("".equals(str) || str.length() == 0) {
            return new ResultSimple(false, "不能为空!");
        }
        if (str.length() < minLength || str.length() > maxLength) {
            return new ResultSimple(false, "长度在6~16字符之间哦!");
        }
        Pattern pattern = Pattern.compile("^\\w+$");
        Matcher match = pattern.matcher(str);
        if (!match.matches()) {
            return new ResultSimple(false, "只能由字母，下划线，数字和汉字组成!");
        }

        return new ResultSimple(true);
    }

    /**
     * 判断字符串是否符合规格：只能输入数字和英文字母
     * 判断字符串长度是否为空
     * 判断字符串是否符合最大，最短长度
     *
     * @param str 原字符串
     * @return true表示符合规则，false表示不符合
     */
    public static ResultSimple msIsNumberOrLetter(String str, int minLength, int maxLength) {

        if ("".equals(str) || str.length() == 0) {
            return new ResultSimple(false, "不能为空!");
        }
        if (str.length() < minLength || str.length() > maxLength) {
            return new ResultSimple(false, "长度在6~16字符之间哦!");
        }
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z]+$");
        Matcher match = pattern.matcher(str);
        if (!match.matches()) {
            return new ResultSimple(false, "只能由字母，数字和汉字组成!");
        }

        return new ResultSimple(true);
    }

    /**
     * 判断字符串是否全为数字,是否是手机号码
     *
     * @param strSource 原始字符串
     * @return 是返回true，否则返回false
     */
    public static ResultSimple msIsOphone(String strSource) {
        if (("".equals(strSource) || strSource.length() == 0)) {
            return new ResultSimple(false, "手机号码不能为空!");

        }
        for (int i = 0; i < strSource.length(); i++) {
            if (!Character.isDigit(strSource.charAt(i))) {
                return new ResultSimple(false, "手机号码包含非数字字符!");
            }
        }

        if ((strSource.length() != 11)) {
            return new ResultSimple(false, "手机号码由11数字组成的哦!");
        }

        return new ResultSimple(true);
    }


    /**
     * 判断字符串是否全为数字
     *
     * @param strSource 原始字符串
     * @return 是返回true，否则返回false
     */
    public static ResultSimple msIsNum(String strSource) {
        if ("".equals(strSource) || strSource.length() == 0 || strSource == null) {
            return new ResultSimple(false, "不能为空!");
        }

        for (int i = 0; i < strSource.length(); i++) {
            if (!Character.isDigit(strSource.charAt(i))) {
                return new ResultSimple(false, "包含非数字字符!");
            }
        }
        return new ResultSimple(true);
    }
    /**
     * 统计字符串长度或null
     *
     * @param strSource 原始字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 是返回true，否则返回false
     */
    public static ResultSimple msIsLengthRule(String strSource, int minLength, int maxLength) {
        if ("".equals(strSource) || strSource.length() == 0 || strSource == null) {
            return new ResultSimple(false, "不能为空!");
        }

        if(strSource.length() < minLength){
            return new ResultSimple(false, "不能少于"+minLength+"字符！");
        }

        if(strSource.length() > maxLength){
            return new ResultSimple(false, "不能超过"+maxLength+"字符！");
        }

        return new ResultSimple(true);
    }
}


