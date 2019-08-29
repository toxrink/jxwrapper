package x.remote

import scala.beans.BeanProperty

/**
  * Created by xw on 2019/8/29.
  */
class HostInfo {
  /**
    * 服务器地址
    */
  @BeanProperty
  var host: String = ""

  /**
    * 目标端口
    */
  @BeanProperty
  var port: Int = 0

  /**
    * 服务器连接账号
    */
  @BeanProperty
  var username: String = ""

  /**
    * 服务器连接密码
    */
  @BeanProperty
  var password: String = ""
}
