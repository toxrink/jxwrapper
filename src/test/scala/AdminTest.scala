import x.database.AdminWrapper

/**
 * Created by xw on 2019/8/30.
 */
object AdminTest {
  def main(args: Array[String]): Unit = {
    import scala.collection.JavaConversions._
    val admin = AdminWrapper.build("com.mysql.jdbc.Driver", "jdbc:mysql://192.168.119.208:3306/scas?useSSL=false",
      "root", "root")
    admin.connect()
    admin.getTables.foreach(println)
    admin.close()
  }
}