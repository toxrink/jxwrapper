package x.utils

import java.security.MessageDigest
import java.util.{Base64, UUID}

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
    if (null == b) {
      ""
    } else {
      Base64.getEncoder.encodeToString(b)
    }
  }

  /**
    * 转base64
    *
    * @param s 字符串
    * @return
    */
  def encodeBase64(s: String): String = {
    if (null == s) {
      ""
    } else {
      encodeBase64(s.getBytes(JxConst.UTF8))
    }
  }

  /**
    * base64解码
    *
    * @param b 字符串
    * @return
    */
  def decodeBase64(b: Array[Byte]): String = {
    if (null == b) {
      ""
    } else {
      new String(Base64.getDecoder.decode(b), JxConst.UTF8)
    }
  }

  /**
    * base64解码
    *
    * @param s 字符串
    * @return
    */
  def decodeBase64(s: String): String = {
    if (null == s) {
      ""
    } else {
      decodeBase64(s.getBytes(JxConst.UTF8))
    }
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
}
