import java.io.File
import java.util

import org.apache.commons.io.FileUtils
import x.utils.{AreaInfo, AreaUtils, IpUtils}

/**
  * Created by xw on 2019/11/18.
  */
object AreaTest {
  def main(args: Array[String]): Unit = {
    val file = new File("C:\\Users\\admin\\Desktop\\iptable.csv")
    val lines = FileUtils.readLines(file, "UTF-8")
    val ipList = new util.ArrayList[AreaInfo]()
    for (i <- 2 until lines.size()) {
      val sp = lines.get(i).split("\t")
      val start = IpUtils.ipToNum(sp(1))
      val end = IpUtils.ipToNum(sp(2))
      ipList.add(AreaInfo(i.toString, i.toString, sp(1), sp(2), start, end))
    }
    val at = AreaUtils.loadArea(ipList)
    var count = 0
    var last = System.currentTimeMillis()
    while (count <= 50000000) {
      if (count % 1000 == 0) {
        println(s"$count:${System.currentTimeMillis() - last}")
        last = System.currentTimeMillis()
      }
      count = count + 1
      at.binarySearch("194.147.7.255")
    }
  }
}
