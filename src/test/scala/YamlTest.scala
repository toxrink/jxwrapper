import java.util

import x.yaml.YamlWrapper

import scala.collection.mutable

/**
  * Created by xw on 2019/8/1.
  */
object YamlTest {
  def main(args: Array[String]): Unit = {
//    val path = "D:\\vscode\\java\\vap-flume\\assets\\help\\test\\lc\\data-fix.yml"
//    YamlWrapper
//      .loadAsYamlEntry(path)
//      .getListWithLinkedHashMap("conf.settings")
//      .toList.foreach(m => {
//      val yaml = YamlWrapper.loadAsYamlEntry(m)
//      println(yaml.getArray("setting.fix.add"))
//      println(yaml.getString("setting.es-time-format"))
//      println(yaml.getArray("setting.fix.handle"))
//      println(yaml.getString("setting.index"))
//      println(yaml.getString("setting.lv2-fields"))
//      println(yaml.getString("setting.lv1-fields"))
//      println(yaml.getArray("setting.fix.rename"))
//      println(yaml.getString("setting.time-format"))
//      println(yaml.getString("setting.time-key"))
//      println(yaml.getString("setting.type"))
//    })

    val map = new util.LinkedHashMap[String, Object]()
    map.put("a", "1")
    val yaml = YamlWrapper.loadAsYamlEntry(map)
    println(yaml.getBoolean("a"))
  }
}
