package com.inclusive.finance.jh.utils.auth

import it.sauronsoftware.base64.Base64
import java.io.*

/**  */
/**
 *
 *
 * BASE64编码解码工具包
 *
 *
 *
 * 依赖javabase64-1.3.1.jar
 *
 *
 * @author jun
 * @date 2012-5-19
 * @version 1.0
 */
object Base64Utils {
    /**  */
    /**
     * 文件读取缓冲区大小
     */
    private const val CACHE_SIZE = 1024
    /**  */
    /**
     *
     *
     * BASE64字符串解码为二进制数据
     *
     *
     * @param base64
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun decode(base64: String): ByteArray {
        return Base64.decode(base64.toByteArray())
    }

    fun decode(b: ByteArray?): String {
        return String(Base64.decode(b))
    }
    /**  */
    /**
     *
     *
     * 二进制数据编码为BASE64字符串
     *
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    @JvmStatic
	@Throws(Exception::class)
    fun encode(bytes: ByteArray?): String {
        return String(Base64.encode(bytes))
    }
    /**  */
    /**
     *
     *
     * 将文件编码为BASE64字符串
     *
     *
     *
     * 大文件慎用，可能会导致内存溢出
     *
     *
     * @param filePath
     * 文件绝对路径
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun encodeFile(filePath: String?): String {
        val bytes = fileToByte(filePath)
        return encode(bytes)
    }
    /**  */
    /**
     *
     *
     * BASE64字符串转回文件
     *
     *
     * @param filePath
     * 文件绝对路径
     * @param base64
     * 编码字符串
     * @throws Exception
     */
    @Throws(Exception::class)
    fun decodeToFile(filePath: String?, base64: String) {
        val bytes = decode(base64)
        byteArrayToFile(bytes, filePath)
    }
    /**  */
    /**
     *
     *
     * 文件转换为二进制数组
     *
     *
     * @param filePath
     * 文件路径
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun fileToByte(filePath: String?): ByteArray {
        var data = ByteArray(0)
        val file = File(filePath)
        if (file.exists()) {
            val `in` = FileInputStream(file)
            val out = ByteArrayOutputStream(2048)
            val cache = ByteArray(CACHE_SIZE)
            var nRead = 0
            while (`in`.read(cache).also { nRead = it } != -1) {
                out.write(cache, 0, nRead)
                out.flush()
            }
            out.close()
            `in`.close()
            data = out.toByteArray()
        }
        return data
    }
    /**  */
    /**
     *
     *
     * 二进制数据写文件
     *
     *
     * @param bytes
     * 二进制数据
     * @param filePath
     * 文件生成目录
     */
    @Throws(Exception::class)
    fun byteArrayToFile(bytes: ByteArray?, filePath: String?) {
        val `in`: InputStream = ByteArrayInputStream(bytes)
        val destFile = File(filePath)
        if (!destFile.parentFile.exists()) {
            destFile.parentFile.mkdirs()
        }
        destFile.createNewFile()
        val out: OutputStream = FileOutputStream(destFile)
        val cache = ByteArray(CACHE_SIZE)
        var nRead = 0
        while (`in`.read(cache).also { nRead = it } != -1) {
            out.write(cache, 0, nRead)
            out.flush()
        }
        out.close()
        `in`.close()
    }
}