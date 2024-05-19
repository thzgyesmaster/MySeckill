package com.lifu.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5Util {
    private static final String SALT = "lifuSalt";

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    //第一次加密
    public static String inputPassToFromPass(String inputPass){
        String str = "" + SALT.charAt(0) + SALT.charAt(7) + inputPass +
                SALT.charAt(1) + SALT.charAt(6);
            return md5(str);
    }

    //第二次加密
    public static String fromPassToDBPass(String fromPass , String salt){
        String str = "" + salt.charAt(2) + salt.charAt(5) + fromPass
                + salt.charAt(3) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass , String salt){
        return fromPassToDBPass( inputPassToFromPass(inputPass) , salt);
    }

    public static void main(String[] args) {
        //test
        System.out.println(inputPassToFromPass("123456"));
        System.out.println(fromPassToDBPass("9982327109b91929b5fdd8a0597257da" , SALT));
        System.out.println(inputPassToDBPass("123456" , SALT));
    }
}
