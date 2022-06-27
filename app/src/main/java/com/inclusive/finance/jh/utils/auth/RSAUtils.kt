package com.inclusive.finance.jh.utils.auth

import com.inclusive.finance.jh.utils.auth.Base64Utils.decode
import com.inclusive.finance.jh.utils.auth.Base64Utils.encode
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

object RSAUtils {
    private const val PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCUcwJGtJ4NP2TixMzg6V6pbf9A5vm6+Oq36rB7cxkwd/DWqHYFgpzNZPkETqoXYOaSWrG/UrGSO+xhIDF56a1wDL/Q1C2uGuA0KCmPDptQZ44e3UG7lEq9omaJVHD4R8XN0eidotyNA5i2pZu0RxN805CX/6GhMS2PpJogGasI6QIDAQAB"

    /** RSA最大加密明文大小  */
    private const val MAX_ENCRYPT_BLOCK = 117

    /** RSA最大解密密文大小  */
    private const val MAX_DECRYPT_BLOCK = 128

    /** 加密算法RSA  */
    private const val KEY_ALGORITHM = "RSA"

    /**
     * 公钥加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun encryptByPublicKey(data: String): String {
        val dataByte = data.toByteArray()
        val keyBytes = decode(PUBLIC_KEY)
        val x509KeySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val publicK: Key = keyFactory.generatePublic(x509KeySpec)
        // 对数据加密
        // Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicK)
        val inputLen = dataByte.size
        val out = ByteArrayOutputStream()
        var offSet = 0
        var cache: ByteArray
        var i = 0
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            cache = if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cipher.doFinal(dataByte, offSet, MAX_ENCRYPT_BLOCK)
            } else {
                cipher.doFinal(dataByte, offSet, inputLen - offSet)
            }
            out.write(cache, 0, cache.size)
            i++
            offSet = i * MAX_ENCRYPT_BLOCK
        }
        val encryptedData = out.toByteArray()
        out.close()
        return encode(encryptedData)
    }

    /**
     * 获取模数和密钥
     *
     * @return
     */
    val modulusAndKeys: Map<String, String>
        get() {
            val map: MutableMap<String, String> = HashMap()
            try {
                val `in` = RSAUtils::class.java.getResourceAsStream("/rsa.properties")
                val prop = Properties()
                prop.load(`in`)
                val modulus = prop.getProperty("modulus")
                val publicKey = prop.getProperty("publicKey")
                val privateKey = prop.getProperty("privateKey")
                `in`.close()
                map["modulus"] = modulus
                map["publicKey"] = publicKey
                map["privateKey"] = privateKey
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return map
        }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr
     * 公钥数据字符串
     * @throws Exception
     * 加载公钥时产生的异常
     */
    @Throws(Exception::class)
    fun loadPublicKey(publicKeyStr: String?): PublicKey {
        return try {
            val buffer = decode(publicKeyStr!!)
            val keyFactory = KeyFactory.getInstance("RSA")
            val keySpec = X509EncodedKeySpec(buffer)
            keyFactory.generatePublic(keySpec) as RSAPublicKey
        } catch (e: NoSuchAlgorithmException) {
            throw Exception("无此算法")
        } catch (e: InvalidKeySpecException) {
            throw Exception("公钥非法")
        } catch (e: NullPointerException) {
            throw Exception("公钥数据为空")
        }
    }

    /**
     * 从字符串中加载私钥<br></br>
     * 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令）。
     *
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun loadPrivateKey(privateKeyStr: String?): PrivateKey {
        return try {
            val buffer = decode(privateKeyStr!!)
            // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            val keySpec = PKCS8EncodedKeySpec(buffer)
            val keyFactory = KeyFactory.getInstance("RSA")
            keyFactory.generatePrivate(keySpec) as RSAPrivateKey
        } catch (e: NoSuchAlgorithmException) {
            throw Exception("无此算法")
        } catch (e: InvalidKeySpecException) {
            throw Exception("私钥非法")
        } catch (e: NullPointerException) {
            throw Exception("私钥数据为空")
        }
    }
}