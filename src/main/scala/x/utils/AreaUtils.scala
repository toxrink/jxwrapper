package x.utils

import java.util

import x.yaml.YamlWrapper

/**
  * Created by xw on 2019/11/15.
  */
object AreaUtils {

  /**
    * 读取区域配置
    * <br>
    * yml格式如下:
    * <p>地区1:<br>
    * &nbsp;&nbsp;area-code: xxxxxxxxxx<br>
    * &nbsp;&nbsp;ip-ranges: 10.22.0.1-10.22.40.254,10.22.42.1-10.22.45.254<br>
    *</p>
    * ...
    * <p>地区n:<br>
    * &nbsp;&nbsp;area-code: xxxxxxxxxx<br>
    * &nbsp;&nbsp;ip-ranges: 10.22.0.1-10.22.40.254,10.22.42.1-10.22.45.254<br>
    *</p>
    *
    * @param path
    * @return
    */
  def loadArea(path: String): AreaTool = {
    import scala.collection.JavaConversions._
    val yamlEntry = YamlWrapper
      .loadYamlAsLinkedHashMap(path)
      .asInstanceOf[util.LinkedHashMap[String, util.LinkedHashMap[String, Object]]]
    val areas: List[AreaInfo] = yamlEntry
      .flatMap(m => {
        val areaName = castString(m._1, "").trim
        val areaCode = castString(m._2("area-code"), "").trim
        val ipRanges = castString(m._2("ip-ranges"), "")
        if ("".equals(areaName) || "".equals(areaCode) || "".equals(ipRanges)) {
          Array[AreaInfo]()
        } else {
          ipRanges
            .split(",")
            .map(ips => {
              val ips1 = ips.split("-")
              val startIp = ips1(0).trim
              val endIp = if (ips1.length == 1) startIp else ips1(1).trim
              val startIpNumber = IpUtils.ipToNum(startIp)
              val endIpNumber = IpUtils.ipToNum(endIp)

              AreaInfo(areaName, areaCode, startIp, endIp, startIpNumber, endIpNumber)
            })
        }
      })
      .toList
      .sortBy(_.startIpNumber) //根据startIpNumnber从小到大排序
    AreaTool(areas)
  }

  /**
    * 加载区域配置,会自动根据startIpNumnber从小到大排序
    * @param areaInfoList
    * @return
    */
  def loadArea(areaInfoList: util.ArrayList[AreaInfo]): AreaTool = {
    import scala.collection.JavaConversions._
    val areas = areaInfoList.toList.sortBy(_.startIpNumber)
    AreaTool(areas)
  }

  private def castString(o: Object, d: String): String = {
    if (null == o) {
      d
    } else {
      o.toString
    }
  }

  def main(args: Array[String]): Unit = {
    val at = loadArea("C:\\Users\\admin\\Desktop\\area.yml")
    println(at.binarySearch("10.22.69.21"))
  }
}

case class AreaTool(sortedAreaList: List[AreaInfo]) {

  /**
    * 根据ip查找区域信息
    * @param ip
    * @return
    */
  def search(ip: String): Option[AreaInfo] = {
    val ipNumber = IpUtils.ipToNum(ip)
    sortedAreaList.find(a => a.startIpNumber <= ipNumber && a.endIpNumber >= ipNumber)
  }

  /**
    * 根据ip二分查找区域信息,未找到返回null<br>
    * 使用该方法查找,ip范围不能有交叉的范围
    * @param ip
    * @return
    */
  def binarySearch(ip: String): Option[AreaInfo] = {
    val ipNumber = IpUtils.ipToNum(ip)
    var start = 0
    var end = sortedAreaList.length - 1
    while (start <= end) {
      val mid = (end + start) / 2
      val midAreaInfo = sortedAreaList(mid)
      if (midAreaInfo.startIpNumber <= ipNumber
          && midAreaInfo.endIpNumber >= ipNumber) {
        return Some(midAreaInfo)
      } else if (midAreaInfo.startIpNumber > ipNumber) {
        end = mid - 1
      } else if (midAreaInfo.endIpNumber < ipNumber) {
        start = mid + 1
      }
    }
    None
  }

  /**
    * 获取区域名称
    * @param ip
    * @return
    */
  def getAreaName(ip: String): String = {
    val areaInfo = search(ip)
    if (areaInfo.isEmpty) {
      ""
    } else {
      areaInfo.get.areaName
    }
  }

  /**
    * 获取区域码
    * @param ip
    * @return
    */
  def getAreaCode(ip: String): String = {
    val areaInfo = search(ip)
    if (areaInfo.isEmpty) {
      ""
    } else {
      areaInfo.get.areaCode
    }
  }

  /**
    * 二分查找获取区域名称
    * @param ip
    * @return
    */
  def getAreaName2(ip: String): String = {
    val areaInfo = binarySearch(ip)
    if (areaInfo.isEmpty) {
      ""
    } else {
      areaInfo.get.areaName
    }
  }

  /**
    * 二分查找获取区域码
    * @param ip
    * @return
    */
  def getAreaCode2(ip: String): String = {
    val areaInfo = binarySearch(ip)
    if (areaInfo.isEmpty) {
      ""
    } else {
      areaInfo.get.areaCode
    }
  }
}

case class AreaInfo(
    areaName: String,
    areaCode: String,
    startIp: String,
    endIp: String,
    startIpNumber: Long,
    endIpNumber: Long
)
