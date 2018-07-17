package dev.yong.wheel.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author coderyong
 */
public final class MD5 {

    private MD5() {
        throw new UnsupportedOperationException("Cannot be created");
    }

    private static final char[] HEX_DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String toHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(HEX_DIGITS[(b >> 4) & 0x0F]);
            hex.append(HEX_DIGITS[b & 0x0F]);
        }
        return hex.toString();
    }

    public static String md5(File file) throws IOException {
        byte[] encodeBytes;
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            messagedigest.update(new FileInputStream(file).getChannel()
                    .map(FileChannel.MapMode.READ_ONLY, 0, file.length()));
            encodeBytes = messagedigest.digest();
        } catch (NoSuchAlgorithmException neverHappened) {
            throw new RuntimeException(neverHappened);
        }
        return toHexString(encodeBytes);
    }

    public static String md5(String string) {
        byte[] encodeBytes;
        try {
            encodeBytes = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException neverHappened) {
            throw new RuntimeException(neverHappened);
        } catch (UnsupportedEncodingException neverHappened) {
            throw new RuntimeException(neverHappened);
        }

        return toHexString(encodeBytes);
    }
}