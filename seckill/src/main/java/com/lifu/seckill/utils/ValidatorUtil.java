package com.lifu.seckill.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号校验
 */
public class ValidatorUtil {
    private static final Pattern mobile_pattern = Pattern.compile("^[1]([3-9])[0-9]{9}$");
    public static boolean isMobile(String mobile){
        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches(); //matches()方法为全局匹配，如果正则能把整个字符串匹配上则返回true，否则返回false
    }


}
