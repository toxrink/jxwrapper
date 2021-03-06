package x.yaml

import java.io.Reader
import java.util

import org.yaml.snakeyaml.Yaml
import x.log.Xlog

import scala.io.Source

/**
  * Created by xw on 2019/8/1.
  */
object YamlWrapper {

  private val LOG = Xlog.getLogger(YamlWrapper.getClass)

  def loadYamlAs[T](path: String, ctype: Class[T]): T = {
    loadYamlAs(path, ctype, "UTF-8")
  }

  def loadYamlAs[T](path: String, ctype: Class[T], charset: String): T = {
    if (LOG.isDebugEnabled) {
      LOG.debug("load yaml file " + path)
    }
    val yaml   = new Yaml()
    val reader = Source.fromFile(path, charset).bufferedReader()
    val as     = yaml.loadAs(reader, ctype)
    reader.close()
    as
  }

  def loadYamlAsLinkedHashMap[T](path: String): util.LinkedHashMap[String, T] = {
    loadYamlAs(path, classOf[util.LinkedHashMap[String, T]])
  }

  def loadAsYamlEntry(path: String): YamlEntry = {
    val map = loadYamlAs(path, classOf[util.LinkedHashMap[String, Object]])
    new YamlEntry(map)
  }

  def loadAsYamlEntry(yml: util.LinkedHashMap[String, Object]): YamlEntry = {
    new YamlEntry(yml)
  }

  def loadAsYamlEntry(source: Reader): YamlEntry = {
    val yaml = new Yaml()
    val as   = yaml.loadAs(source, classOf[util.LinkedHashMap[String, Object]])
    new YamlEntry(as)
  }

}
