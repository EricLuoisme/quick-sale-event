package com.selfStudy.quicksaleevent.utils;


import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    private static final String salt = "1a2b3c4d";

    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    // encoding step 1
    public static String inputPassToFormPass(String inputPass) {
        String str = salt.charAt(5) + salt.charAt(2) + inputPass + salt.charAt(3) + salt.charAt(4);
        return md5(str);
    }

    // encoding step 2
    public static String formPassToDBPass(String formPass, String salt) {
        String str = salt.charAt(3) + salt.charAt(5) + formPass + salt.charAt(4) + salt.charAt(1);
        return md5(str);
    }

    // whole encoding process
    public static String inputPassToDBPass(String plainText, String saltDB) {
        String formPass = inputPassToFormPass(plainText);
        return formPassToDBPass(formPass, saltDB);
    }
}
