package x.database

import java.sql.{Connection, DriverManager, ResultSet, SQLException, Types}
import java.util

import x.utils.JxUtils
import scala.collection.JavaConverters._

/**
 * Created by xw on 2019/8/29.
 */
object AdminWrapper {
  private val LOG = JxUtils.getLogger(AdminWrapper.getClass)

  def build(driver: String, url: String): Admin = {
    build(driver, url, null, null)
  }

  def build(driver: String, url: String, user: String, password: String): Admin = {
    Class.forName(driver)
    new Admin(url, user, password)
  }

  class Admin(url: String, user: String, password: String) {
    private var conn: Connection = _

    /**
     * 创建连接
     *
     * @return
     */
    def connect(): Admin = {
      try {
        if (null == conn || conn.isClosed) {
          if (null != user) {
            conn = DriverManager.getConnection(url, user, password)
          } else {
            conn = DriverManager.getConnection(url)
          }
        }
      } catch {
        case e: SQLException => LOG.error("", e)
      }
      this
    }

    /**
     * 执行sql
     *
     * @param sql 执行的sql
     * @return
     */
    def execute(sql: String): Boolean = {
      var status = false
      try {
        status = conn.createStatement().execute(sql)
      } catch {
        case e: SQLException => LOG.error("", e)
      }
      status
    }

    /**
     * 查询
     *
     * @param sql 执行的sql
     * @return
     */
    def executeQuery(sql: String): ResultSet = {
      var rs: ResultSet = null
      try {
        rs = conn.createStatement().executeQuery(sql)
      } catch {
        case e: SQLException => LOG.error("", e)
      }
      rs
    }

    /**
     * 获取hive表信息
     *
     * @return
     */
    def getHiveTables: util.List[TableInfo] = {
      var list: util.List[TableInfo] = new util.ArrayList[TableInfo]()
      try {
        val rs = conn.createStatement().executeQuery("show tables")
        list = new ResultSetWrapper[TableInfo](rs).mapToList(table => {
          val rs2 = conn.createStatement().executeQuery("desc " + table.getString(1))
          val cList = new util.ArrayList[String]()
          val ctList = new util.ArrayList[Int]()
          val columnWithTypeMap = new util.HashMap[String, Int]()
          while (rs2.next()) {
            if (null == rs2.getString(2) || columnWithTypeMap.containsKey(rs2.getString(1))) {
              if (LOG.isDebugEnabled) {
                LOG.debug("null or exists field, skip " + rs2.getString(1))
              }
            } else {
              rs2.getString(2) match {
                case "string" =>
                  cList.add(rs2.getString(1))
                  ctList.add(Types.VARCHAR)
                  columnWithTypeMap.put(rs2.getString(1), Types.VARCHAR)
                case "int" =>
                  cList.add(rs2.getString(1))
                  ctList.add(Types.INTEGER)
                  columnWithTypeMap.put(rs2.getString(1), Types.INTEGER)
                case "bigint" =>
                  cList.add(rs2.getString(1))
                  ctList.add(Types.BIGINT)
                  columnWithTypeMap.put(rs2.getString(1), Types.BIGINT)
                case "timestamp" =>
                  cList.add(rs2.getString(1))
                  ctList.add(Types.TIMESTAMP)
                  columnWithTypeMap.put(rs2.getString(1), Types.TIMESTAMP)
                case "double" =>
                  cList.add(rs2.getString(1))
                  ctList.add(Types.DOUBLE)
                  columnWithTypeMap.put(rs2.getString(1), Types.DOUBLE)
                case "boolean" =>
                  cList.add(rs2.getString(1))
                  ctList.add(Types.BOOLEAN)
                  columnWithTypeMap.put(rs2.getString(1), Types.BOOLEAN)
              }
            }
          }
          val tableInfo = new TableInfo()
          tableInfo.setColumns(cList.asScala.toArray)
          tableInfo.setColumnTypes(ctList.asScala.toArray)
          tableInfo.setColumnWithTypeMap(columnWithTypeMap)
          tableInfo.setTable(rs.getString(1))
          rs2.close()
          tableInfo
        })
        rs.close()
      } catch {
        case e: SQLException => LOG.error("", e)
      }
      list
    }

    /**
     * 获取表信息
     *
     * @return
     */
    def getTables: util.List[TableInfo] = {
      val list = new util.ArrayList[TableInfo]()
      try {
        val dmd = conn.getMetaData
        val rs = dmd.getTables("", "", "", Array[String]("TABLE"))
        while (rs.next()) {
          val rs2 = dmd.getColumns("", "", rs.getString(3), "")
          val cList = new util.ArrayList[String]()
          val ctList = new util.ArrayList[Int]()
          val columnWithTypeMap = new util.HashMap[String, Int]()
          while (rs2.next()) {
            cList.add(rs2.getString(4))
            ctList.add(rs2.getInt(5))
            columnWithTypeMap.put(rs2.getString(4), rs2.getInt(5))
          }
          rs2.close()
          val tableInfo = new TableInfo()
          tableInfo.setColumns(cList.asScala.toArray)
          tableInfo.setColumnTypes(ctList.asScala.toArray)
          tableInfo.setColumnWithTypeMap(columnWithTypeMap)
          tableInfo.setTable(rs.getString(3))
          list.add(tableInfo)
        }
        rs.close()
      } catch {
        case e: SQLException => LOG.error("", e)
      }
      list
    }

    /**
     * 关闭连接
     */
    def close(): Unit = {
      try {
        conn.close()
      } catch {
        case e: SQLException => LOG.error("", e)
      }
    }
  }

}
