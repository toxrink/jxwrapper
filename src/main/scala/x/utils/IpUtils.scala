package x.utils

/**
  * Created by xw on 2019/8/28.
  */
object IpUtils {
  val IP_PATTERN = "((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))"

  val FF = "FF"

  val MASK: Array[Char] = Array[Char]('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

  private def toHex(i: Int): String = {
    if (i >= 255) {
      FF
    } else {
      new String(Array[Char](MASK(i >> 4), MASK(i % 16)))
    }
  }

  def ipToNum(ip: String): Long = {
    val ipArr = ip.split("\\.")
    val sb = new StringBuffer(8)
    var num = -1l
    try {
      for (i <- ipArr) {
        sb.append(toHex(Integer.valueOf(i.trim())))
      }
      num = java.lang.Long.parseLong(sb.toString, 16)
    } catch {
      case _: Throwable =>
    }
    num
  }

  def numToIp(ipNum: Long): String = {
    val tmp = new StringBuffer(java.lang.Long.toHexString(ipNum))
    while (tmp.length() < 8) {
      tmp.insert(0, '0')
    }
    val tmp2 = new StringBuffer(15)

    val chars = tmp.toString.toCharArray
    var i = 0
    while (i < chars.length) {
      tmp.setLength(0)
      tmp.insert(0, chars(i))
      tmp.insert(1, chars(i + 1))
      tmp2.append(Integer.parseInt(tmp.toString, 16)).append(".")
      i = i + 2
    }
    tmp2.deleteCharAt(tmp2.length() - 1).toString
  }

  def isIp(ip: String): Boolean = {
    ip.matches(IP_PATTERN)
  }

  def isInRange(ip: String, startIp: String, endIp: String): Boolean = {
    val ipNum = ipToNum(ip)
    val start = ipToNum(startIp)
    val end = ipToNum(endIp)
    isInRange(ipNum, start, end)
  }

  def isInRange(ip: Long, startIp: String, endIp: String): Boolean = {
    val start = ipToNum(startIp)
    val end = ipToNum(endIp)
    isInRange(ip, start, end)
  }

  def isInRange(ip: Long, startIp: Long, endIp: Long): Boolean = {
    val start = Math.min(startIp, endIp)
    val end = Math.max(startIp, endIp)
    ip >= start && ip <= end
  }

//  def main(args: Array[String]): Unit = {
//    println(ipToNum("192.168.15.250"))
//    println(numToIp(3232239610l))
//    println(isInRange("192.168.15.220", "192.168.15.200", "192.168.15.250"))
//    println(isInRange("192.168.15.251", "192.168.15.200", "192.168.15.250"))
//    println(isIp("192.168.15.251"))
//    println(isIp("192.168.15.251a"))
//  }
}
