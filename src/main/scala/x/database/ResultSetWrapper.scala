package x.database

import java.sql.{ResultSet, SQLException}
import java.util

import x.log.Xlog

/**
  * Created by xw on 2019/8/29.
  */
class ResultSetWrapper[T](rs: ResultSet) {
  private val LOG = Xlog.getLogger(classOf[ResultSetWrapper[T]])

  def mapToList(map: ResultSet => T): util.List[T] = {
    val list = new util.ArrayList[T]()
    try {
      while (rs.next()) {
        val t = map(rs)
        if (null != t) {
          list.add(t)
        }
      }
    } catch {
      case e: SQLException => LOG.error("", e)
    }
    list
  }

}
