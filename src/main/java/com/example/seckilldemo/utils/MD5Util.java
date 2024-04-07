package com.example.seckilldemo.utils;

import com.example.seckilldemo.SeckillDemoApplication;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;
@Component
public class MD5Util {

    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    private static final String salt = "1a2b3c4d";
    public static String inputPassToFromPass(String inputPass) {
        String str = salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String formPassToDBPass(String formPass, String salt) {
        String str = salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDbPass(String inputPass, String salt) {
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = formPassToDBPass(fromPass, salt);
        return dbPass;
    }

    public static void main(String[] args) {
        String pass = "123123";
        String salt = "1a2b3c4d";
        String md5Pass = inputPassToFromPass(pass);
        String dbPass = formPassToDBPass(md5Pass, salt);
        System.out.println(md5Pass);
        System.out.println(dbPass);
    }
    
}

