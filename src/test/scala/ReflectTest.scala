import x.common.annotation.ConfigValue
import x.utils.ReflectUtils

/**
 * Created by xw on 2019/8/29.
 */
object ReflectTest {

  class HelloWorld {
    @ConfigValue
    val dataType: String = ""

    @ConfigValue(alias = "esBatch", value = "2000")
    val esBatchSize: Int = 0

    @ConfigValue("hahaha")
    val esYml: String = ""

    @ConfigValue
    val index: String = ""

    @ConfigValue
    val indexType: String = ""

    @ConfigValue
    val username: String = ""

    @ConfigValue
    val password: Int = 0

    @ConfigValue("600")
    val timeValue: Int = 0

    @ConfigValue("1,2,3,4,5")
    val shards: Array[String] = null

    override def toString: String = {
      "Hw [dataType=" + dataType + ", esBatchSize=" + esBatchSize + ", esYml=" + esYml + ", index=" + index + ", indexType=" + indexType + ", password=" + password + ", shards=" + shards.mkString(",") + ", timeValue=" + timeValue + ", username=" + username + "]"
    }
  }

  def main(args: Array[String]): Unit = {
    val h = new HelloWorld
    import scala.collection.JavaConversions._
    ReflectUtils.wrapObject(h).injectConfigValue(Map("dataType" -> "Test-Data"))
    println(h)
  }
}
