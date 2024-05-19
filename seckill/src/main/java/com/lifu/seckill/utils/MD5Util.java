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
        String str = SALT.charAt(0) + SALT.charAt(7) + inputPass
            + SALT.charAt(1) + SALT.charAt(6);
            return md5(str);
    }

    //第二次加密
    public static String formPassToDBPass(String fromPass , String salt){
        String str = SALT.charAt(2) + SALT.charAt(5) + fromPass
                + SALT.charAt(3) + SALT.charAt(4);
        return md5(str); //?
    }

    public static String inputPassToDBPass(String inputPass , String salt){
        return formPassToDBPass( inputPassToFromPass(inputPass) , salt);
    }

    public static void main(String[] args) {
        //test
        System.out.println(inputPassToFromPass("111"));
        System.out.println(formPassToDBPass(inputPassToFromPass("111") , SALT));
        System.out.println(inputPassToDBPass("111" , SALT));
    }
}
