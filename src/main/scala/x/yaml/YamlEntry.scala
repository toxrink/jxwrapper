package x.yaml

import java.util

import x.utils.StrUtils

/**
 * Created by xw on 2019/8/1.
 */
class YamlEntry(yml: util.LinkedHashMap[String, Object]) {
  var ymlGlobal: util.LinkedHashMap[String, Object] = yml

  def getValue[T](yml: util.LinkedHashMap[String, Object], keys: Array[String]): T = {
    if (keys.length > 1) {
      val yml2 = yml.get(keys(0))
      if (null == yml2) {
        asInstanceOf[T]
      } else {
        val yml3 = yml2.asInstanceOf[util.LinkedHashMap[String, Object]]
        val keys2 = Array.fill[String](keys.length - 1)("")
        System.arraycopy(keys, 1, keys2, 0, keys2.length)
        getValue(yml3, keys2).asInstanceOf[T]
      }
    } else {
      yml.get(keys(0)).asInstanceOf[T]
    }
  }

  def getValue[T](yml: util.LinkedHashMap[String, Object], keys: String): T = {
    getValue(yml, keys.split("\\."))
  }

  def getValue[T](implicit keys: String): T = {
    val v = ymlGlobal.get(keys)
    if (null == v) {
      getValue(ymlGlobal, keys)
    } else {
      v.asInstanceOf[T]
    }
  }

  def getListWithLinkedHashMap(implicit key: String): util.ArrayList[util.LinkedHashMap[String, Object]] = {
    getValue
  }

  def getInt(implicit key: String): Integer = {
    getValue
  }

  def getList(implicit key: String): util.List[String] = {
    getValue
  }

  def getArray(implicit key: String): Array[String] = {
    val a: Object = getValue(key)
    a match {
      case arr: Array[String] => arr
      case arr2: util.List[String] => arr2.toArray(Array[String]())
      case _ => null
    }
  }

  def getBoolean(implicit key: String): Boolean = {
    val b = getValue
    if (null == b) {
      false
    } else {
      b.toString.toLowerCase match {
        case "true" => true
        case "false" => false
        case "0" => false
        case "1" => true
        case _ => false
      }
    }
  }

  def getStringOrDefault(implicit key: String, default: String): String = {
    val s = getString(key)
    if (StrUtils.isEmpty(s)) {
      default
    } else {
      s
    }
  }

  def getString(implicit key: String): String = {
    getValue
  }
}
