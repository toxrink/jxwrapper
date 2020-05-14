package x.utils

import java.security.{MessageDigest, SecureRandom}
import java.util.UUID

import javax.crypto.{Cipher, KeyGenerator}
import org.apache.commons.codec.binary.Base64
import x.self.JxConst

/**
  * Created by xw on 2019/8/1.
  */
object StrUtils {

  /**
    * 字符串判空
    *
    * @param s 判空字符串
    * @return
    */
  def isEmpty(s: String): Boolean = {
    null == s || "".equals(s)
  }

  /**
    * 生成uuid
    *
    * @return
    */
  def getUUID: String = {
    UUID.randomUUID.toString.replace("-", "")
  }

  /**
    * 字符串转md5
    *
    * @param s 字符串
    * @return
    */
  def toMD5(s: String): String = {
    if (isEmpty(s)) {
      s
    } else {
      val md5 = MessageDigest.getInstance("MD5")
      md5
        .digest(s.getBytes(JxConst.UTF8))
        .map(b => {
          val i = b & 0xff
          if (i < 16) {
            "0" + Integer.toHexString(i)
          } else {
            Integer.toHexString(i)
          }
        })
        .mkString
    }
  }

  /**
    * 首字母大写
    *
    * @param s 字符串
    * @return
    */
  def upperCaseFirstLetter(s: String): String = {
    if (s.charAt(0) > 96 && s.charAt(0) < 123) {
      val b = s.getBytes
      b.update(0, (s.charAt(0) - 32).asInstanceOf[Byte])
      new String(b)
    } else {
      s
    }
  }

  /**
    * 首字母小写
    *
    * @param s 字符串
    * @return
    */
  def lowerCaseFirstLetter(s: String): String = {
    if (s.charAt(0) > 64 && s.charAt(0) < 91) {
      val b = s.getBytes
      b.update(0, (s.charAt(0) + 32).asInstanceOf[Byte])
      new String(b)
    } else {
      s
    }
  }

  /**
    * 转base64
    *
    * @param b 字符串
    * @return
    */
  def encodeBase64(b: Array[Byte]): String = {
    Base64.encodeBase64URLSafeString(b)
  }

  /**
    * 转base64
    *
    * @param s 字符串
    * @return
    */
  def encodeBase64(s: String): String = {
    Base64.encodeBase64URLSafeString(s.getBytes(JxConst.UTF8))
  }

  /**
    * base64解码
    *
    * @param b 字符串
    * @return
    */
  def decodeBase64(b: Array[Byte]): String = {
    new String(Base64.decodeBase64(b))
  }

  /**
    * 下划线转驼峰
    *
    * @param s 字符串
    * @return
    */
  def underLineToCamel(s: String): String = {
    lowerCaseFirstLetter(s.split("_").map(upperCaseFirstLetter).mkString)
  }

  /**
    * 驼峰转下划线
    *
    * @param s 字符串
    * @return
    */
  def camelToUnderLine(s: String): String = {
    s.map(c => {
        if (c > 64 && c < 91) {
          "_" + c
        } else {
          c
        }
      })
      .mkString
  }

  /**
    * 获取环境变量配置
    *
    * @param t 格式 ${key:default}
    * @return
    */
  def getEnvironmentValue(t: String): String = {
    if (t.startsWith("${") && t.endsWith("}")) {
      val index = t.indexOf(":")
      if (-1 != index) {
        val key = t.substring(2, index)
        val value = t.substring(index + 1, t.length - 1)
        val v1 = System.getenv.get(key)
        if (null == v1) {
          System.getProperty(key, value)
        } else {
          v1
        }
      } else {
        val key = t.substring(2, t.length - 2)
        val v1 = System.getenv.get(key)
        if (null == v1) {
          System.getProperty(key)
        } else {
          v1
        }
      }
    } else {
      t
    }
  }

  /**
    * des加密
    *
    * @param key 秘钥
    * @param value 需要加密的字符串
    * @return 返回base64字符串
    */
  def encodeDES(key: String, value: String): String = {
    val cipher = desCipher(Cipher.ENCRYPT_MODE, key)
    encodeBase64(cipher.doFinal(value.getBytes(JxConst.UTF8)))
  }

  /**
    * des解密
    *
    * @param key 秘钥
    * @param value encodeDES方法返回的字符串
    * @return
    */
  def decodeDES(key: String, value: String): String = {
    val cipher = desCipher(Cipher.DECRYPT_MODE, key)
    val d64 = Base64.decodeBase64(value.getBytes())
    new String(cipher.doFinal(d64))
  }

  private def desCipher(mode: Int, dkey: String): Cipher = {
    val cipher = Cipher.getInstance("DES")
    val key = KeyGenerator.getInstance("DES")
    val random = SecureRandom.getInstance("SHA1PRNG")
    random.setSeed(dkey.getBytes(JxConst.UTF8S))
    key.init(random)
    cipher.init(mode, key.generateKey())
    cipher
  }
}
