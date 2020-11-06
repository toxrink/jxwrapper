package x.log

import org.apache.commons.logging.{Log, LogFactory}

/**
  * Created by xw on 2020/11/6.
  */
object Xlog {

  /**
    * 获取org.apache.commons.logging.Log
    * @param clazz 日志对象
    * @return
    */
  def getLogger(clazz: Class[_]): Log = {
    LogFactory.getLog(clazz)
  }
}
