@file:Suppress("unused")

package dev.yong.wheel.utils

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @author coderyong
 */
object MD5 {

    private val hexDigits =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    private fun toHexString(bytes: ByteArray?): String {
        if (bytes == null) return ""
        val hex = StringBuilder(bytes.size * 2)
        for (b in bytes) {
            hex.append(hexDigits[(b.toInt() shr 4) and 0x0F])
            hex.append(hexDigits[b.toInt() and 0x0F])
        }
        return hex.toString()
    }

    /**
     * 加密文件
     *
     * @param file 待加密文件
     * @return MD5加密字符串
     * @throws IOException IO操作异常
     */
    @JvmStatic
    @Throws(IOException::class)
    fun encrypt(file: File): String {
        val digest: MessageDigest
        val input: FileInputStream
        val ch: FileChannel
        val encodeBytes: ByteArray
        try {
            digest = MessageDigest.getInstance("MD5")
            input = FileInputStream(file)
            ch = input.channel
            val byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
            digest.update(byteBuffer)
            encodeBytes = digest.digest()
        } catch (neverHappened: NoSuchAlgorithmException) {
            throw RuntimeException(neverHappened)
        }
        return toHexString(encodeBytes)
    }

    /**
     * 加密字符串
     *
     * @param str 待加密串
     * @return MD5加密字符串
     */
    @JvmStatic
    fun encrypt(str: String): String {
        return toHexString(
            try {
                MessageDigest.getInstance("MD5").digest(str.toByteArray())
            } catch (neverHappened: NoSuchAlgorithmException) {
                throw RuntimeException(neverHappened)
            }
        )
    }
}