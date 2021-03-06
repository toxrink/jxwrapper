package x.os

import java.io.IOException

import org.apache.commons.io.IOUtils
import x.common.constant.JxConst
import x.log.Xlog

import scala.io.Source

/**
  * Created by xw on 2019/8/1.
  */
class JxProcess(process: Process) {

  private val LOG = Xlog.getLogger(this.getClass)

  private var errLimit = 0

  private var errLimit2 = 0

  private val charset = if (CmdWrapper.isWindows) JxConst.GBKS else JxConst.UTF8S

  /**
    * 保持输入流
    *
    * @param prefix   日志前缀
    * @param logsw    是否打印输入
    * @param errLines 打印输入行数
    * @return
    */
  def waitFor(prefix: String, logsw: Boolean, errLines: Int): JxProcess = {
    val run: Runnable = new Runnable {
      override def run(): Unit = {
        try {
          Source
            .fromInputStream(process.getErrorStream, charset)
            .getLines()
            .foreach(l => {
              if (logsw && errLimit < errLines) {
                errLimit = errLimit + 1
                LOG.error(
                  new StringBuilder()
                    .append(errLimit)
                    .append("-StdErr#")
                    .append(prefix)
                    .append(" : ")
                    .append(l)
                )
              } else if (LOG.isDebugEnabled) {
                LOG.debug(prefix + " : " + l)
              }
            })
          Source
            .fromInputStream(process.getInputStream, charset)
            .getLines()
            .foreach(l => {
              if (logsw && errLimit2 < errLines) {
                errLimit2 = errLimit2 + 1
                LOG.info(
                  new StringBuilder()
                    .append(errLimit2)
                    .append("-StdIn#")
                    .append(prefix)
                    .append(" : ")
                    .append(l)
                )
              } else if (LOG.isDebugEnabled) {
                LOG.debug(prefix + " : " + l)
              }
            })
          process.waitFor()
        } catch {
          case e: IOException          => LOG.error("", e)
          case e: InterruptedException => LOG.error("", e)
        }
      }
    }
    new Thread(run, "JxProcess-" + System.currentTimeMillis()).start()
    this
  }

  /**
    * 保持输入流
    *
    * @param prefix 日志前缀
    * @param logsw  是否打印输入
    * @return
    */
  def waitFor(prefix: String, logsw: Boolean): JxProcess = {
    waitFor(prefix, logsw, 40)
  }

  /**
    * 保持输入流
    *
    * @param prefix 日志前缀
    * @return
    */
  def waitFor(prefix: String): JxProcess = {
    waitFor(prefix, logsw = false)
  }

  /**
    * 获取进程对象
    *
    * @return
    */
  def getProcess: Process = {
    process
  }

  /**
    * 获取第n行数据,获取完毕后会关闭命令行输入流
    *
    * @param line 第n行数据
    * @return
    */
  def getLine(line: Int): String = {
    val br = Source.fromInputStream(process.getInputStream, charset).bufferedReader()
    var cd = line
    var ret = ""
    try {
      while (cd > 0) {
        cd = cd - 1
        br.readLine()
      }
      ret = br.readLine()
    } catch {
      case e: IOException => LOG.error("", e)
    } finally {
      IOUtils.closeQuietly(br)
    }
    ret
  }

  /**
    * 读取input stream
    * @return
    */
  def toStringFromInput(charset: String): String = {
    IOUtils.toString(process.getInputStream, charset)
  }

  /**
    * 读取error stream
    * @return
    */
  def toStringFromError(charset: String): String = {
    IOUtils.toString(process.getErrorStream, charset)
  }

  /**
    * 销毁process
    */
  def destroy(): Unit = {
    process.destroy()
  }
}
