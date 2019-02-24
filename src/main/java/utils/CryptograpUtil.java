package utils;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.hash.Md5Hash;

// 加密解密工具类
public class CryptograpUtil {

    // Base64 编码加密
    public static String enBase64(String str){
        return Base64.encodeToString(str.getBytes());
    }

    // Base64 编码解密
    public static String decBase64(String str){
        return Base64.decodeToString(str);
    }

    // md5 加密，salt 加盐，这种方式加密是不可逆的。即不可破解
    public static String md5(String str,String salt){
        return new Md5Hash(str,salt).toString();
    }

    public static void main(String[] args) {
        String password = "123456";
        String s = CryptograpUtil.enBase64(password);
        System.out.println(s);
        System.out.println(CryptograpUtil.decBase64(s));

        System.out.println(CryptograpUtil.md5("hyman","hyman"));
    }
}
