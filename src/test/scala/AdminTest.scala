import x.database.AdminWrapper

/**
  * Created by xw on 2019/8/30.
  */
object AdminTest {
  def main(args: Array[String]): Unit = {
    import scala.collection.JavaConverters._
    val admin = AdminWrapper.build(
      "org.apache.hive.jdbc.HiveDriver",
      "jdbc:hive2://vrv203:2181,vrv204:2181,vrv205:2181,vrv206:2181,vrv207:2181/vap;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2",
      "",
      ""
    )
    admin.connect()
    admin.getHiveTables.asScala.foreach(println)
    admin.close()
//    val admin = AdminWrapper.build("com.mysql.jdbc.Driver", "jdbc:mysql://192.168.119.208:3306/scas?useSSL=false",
//      "root", "root")
//    admin.connect()
//    admin.getTables.foreach(println)
//    admin.close()
  }
}
