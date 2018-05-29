package com.cn.flypay.utils.quantong;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
	public static String AES_KEY = "6ifddgau72i4fvfx";

	
    public static String decrypt(String sSrc,String key) {
    	if(sSrc==null||sSrc.length()<=0){
    		return "";
    	}
        try {
            byte[] raw = key.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = hex2byte(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String encrypt(String sSrc,String key) {
    	if(sSrc==null||sSrc.length()<=0){
    		return "";
    	}
        try {
            byte[] raw = key.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes());
            return byte2hex(encrypted).toLowerCase();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
        }
        return b;
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    public static void main(String[] args) throws Exception {
    	//通道ID:QT201708180011   key:6ifddgau72i4fvfx
    	String encrypt = encrypt("heiheiheihei", AES_KEY);
    	String decrypt = decrypt(encrypt, AES_KEY);
    	System.out.println(encrypt);
    	System.out.println(decrypt);
    }
}