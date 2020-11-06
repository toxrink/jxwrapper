package x.remote

import scala.beans.BeanProperty

/**
  * Created by xw on 2019/8/29.
  */
class SftpInfo(hostInfo: HostInfo) extends HostInfo {

  setHost(hostInfo.getHost())
  setPort(hostInfo.getPort())
  setUsername(hostInfo.getUsername())
  setPassword(hostInfo.getPassword())

  def this() = {
    this(new HostInfo())
  }

  /**
    * 远程文件路径
    */
  @BeanProperty
  var remote: String = ""

  /**
    * 本地存储目录
    */
  @BeanProperty
  var local: String = ""
}
