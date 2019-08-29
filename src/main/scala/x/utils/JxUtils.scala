package x.utils

import org.apache.commons.logging.{Log, LogFactory}

/**
  * Created by xw on 2019/8/1.
  */
object JxUtils {

  def getLogger(clazz: Class[_]): Log = {
    LogFactory.getLog(clazz)
  }
}
