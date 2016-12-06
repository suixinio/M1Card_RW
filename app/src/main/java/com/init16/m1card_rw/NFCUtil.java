package com.init16.m1card_rw;

/**
 * Created by 佳诚 on 2016/9/24.
 */

public class NFCUtil {

    public static String byte2HexString(byte[] bytes) {
        String ret = "";
        if (bytes != null) {
            for (Byte b : bytes) {
                ret += String.format("%02X", b.intValue() & 0xFF);
            }
        }
        return ret;
    }

    public static byte[] string2byte(String inputStr) {
        byte[] result = new byte[inputStr.length() / 2];
        for (int i = 0; i < inputStr.length() / 2; ++i)
            result[i] = (byte) (Integer.parseInt(inputStr.substring(i * 2, i * 2 + 2), 16) & 0xff);
        return result;
    }
}
