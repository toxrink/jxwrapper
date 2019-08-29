package x.database

import java.sql.Types
import java.util

import scala.beans.BeanProperty

/**
  * Created by xw on 2019/8/29.
  */
class TableInfo {
  @BeanProperty
  var table: String = ""

  @BeanProperty
  var columns: Array[String] = Array[String]()

  @BeanProperty
  var columnTypes: Array[Int] = Array[Int]()

  @BeanProperty
  var columnWithTypeMap: util.Map[String, Int] = new util.HashMap[String, Int]()

  def getColumnsByType(types: Int): Array[String] = {
    val list = new util.ArrayList[String](columns.length)
    var i = 0
    while (i < columns.length) {
      if (columnTypes(i) == types) {
        list.add(columns(i))
      }
      i = i + 1
    }
    list.toArray(Array[String]())
  }

  def getStringColumns(): Array[String] = {
    getColumnsByType(Types.VARCHAR)
  }

  def getIntegertColumns(): Array[String] = {
    getColumnsByType(Types.INTEGER)
  }

  def getBigintColumns(): Array[String] = {
    getColumnsByType(Types.BIGINT)
  }

  def getTimestampColumns(): Array[String] = {
    getColumnsByType(Types.TIMESTAMP)
  }

  def getDoubleColumns(): Array[String] = {
    getColumnsByType(Types.DOUBLE)
  }

  def getBooleanColumns(): Array[String] = {
    getColumnsByType(Types.BOOLEAN)
  }

  override def toString: String = {
    s"TableInfo [columnTypes=${util.Arrays.toString(columnTypes)}, columnWithTypeMap=$columnWithTypeMap, columns=${util.Arrays.toString(columns.asInstanceOf[Array[Object]])}, table=$table]"
  }
}
