package x.yaml

import java.util

import org.yaml.snakeyaml.Yaml
import x.utils.JxUtils

import scala.io.Source

/**
 * Created by xw on 2019/8/1.
 */
object YamlWrapper {

  private val log = JxUtils.getLogger(YamlWrapper.getClass)

  def loadYamlAs[T](path: String, ctype: Class[T]): T = {
    if (log.isDebugEnabled) {
      log.debug("load yaml file " + path)
    }
    val yaml = new Yaml()
    val reader = Source.fromFile(path, "UTF-8").bufferedReader()
    val as = yaml.loadAs(reader, ctype)
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

}
