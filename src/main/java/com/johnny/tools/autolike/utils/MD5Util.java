package com.johnny.tools.autolike.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by johnny on 15/9/8.
 */
public class MD5Util implements java.io.Serializable {

    private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final String MD5 = "MD5";
    private static final Charset CHARSET = Charset.forName("UTF-8");

    public static char[] HexEncode(byte[] bytes) {
        final int nBytes = bytes.length;
        char[] result = new char[2 * nBytes];

        int j = 0;
        for (int i = 0; i < nBytes; i++) {
            result[j++] = HEX[(0xF0 & bytes[i]) >>> 4];
            result[j++] = HEX[(0x0F & bytes[i])];
        }

        return result;
    }

    public static byte[] Utf8Encode(CharSequence string) throws CharacterCodingException {
        try {
            ByteBuffer bytes = CHARSET.newEncoder().encode(CharBuffer.wrap(string));
            byte[] bytesCopy = new byte[bytes.limit()];
            System.arraycopy(bytes.array(), 0, bytesCopy, 0, bytes.limit());

            return bytesCopy;
        } catch (CharacterCodingException e) {
            throw e;
        }
    }

    public static String encode(String data) throws NoSuchAlgorithmException, CharacterCodingException {
        try {
            if (StringUtils.isEmpty(data)) {
                data = "";
            }
            MessageDigest messageDigest = MessageDigest.getInstance(MD5);
            byte[] digest = messageDigest.digest(Utf8Encode(data));
            return new String(HexEncode(digest));
        } catch (NoSuchAlgorithmException e) {
            throw e;
        } catch (CharacterCodingException e) {
            throw e;
        }
    }

    public static String md5AndBase64(String data) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("md5");
        byte[] md5 = md.digest(data.getBytes());
        String base64 = Base64.encodeBase64String(md5);
        return base64;
    }
}
