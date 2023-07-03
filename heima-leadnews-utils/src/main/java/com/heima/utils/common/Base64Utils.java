package com.heima.utils.common;

import org.apache.commons.codec.binary.Base64;

public class Base64Utils {

    /**
     * 解码
     * @param base64
     * @return
     */
    public static byte[] decode(String base64) {
        try {
            // Base64 解码
            return Base64.decodeBase64(base64);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 编码
     * @param data
     * @return
     * @throws Exception
     */
    public static String encode(byte[] data) {
        return Base64.encodeBase64String(data);
    }
}