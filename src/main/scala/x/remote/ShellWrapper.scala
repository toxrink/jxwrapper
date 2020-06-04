package x.remote

import java.io.InputStream
import java.util.{Date, Properties}

import com.jcraft.jsch.ChannelExec
import org.apache.commons.io.IOUtils
import x.os.CmdWrapper
import x.utils.{JxUtils, TimeUtils}

/**
  * Created by xw on 2019/8/29.
  */
object ShellWrapper {
  private val LOG = JxUtils.getLogger(ShellWrapper.getClass)

  private val logOut = new LogOutput {
    override def apply(obj: Object): Unit = LOG.info(obj)

    override def stop(): Boolean = false
  }

  /**
    * 远程文件压缩
    *
    * @param hostInfo host信息
    * @param remote   远程目录
    * @return
    */
  def zipRemote(hostInfo: HostInfo, remote: String): String = {
    val filePath = (if (remote.endsWith("/")) remote.substring(0, remote.length() - 1) else remote) + "_" + TimeUtils
      .format1(new Date()) + ".zip"
    val home = remote.substring(0, remote.lastIndexOf("/"))
    val name = remote.substring(remote.lastIndexOf("/"))
    shell(hostInfo, "cd " + home + ";zip -r " + filePath + " ." + name, logOut)
    filePath
  }

  /**
    * 远程zip文件解压
    *
    * @param hostInfo host信息
    * @param remote   远程zip文件路径
    * @return
    */
  def unZipRemote(hostInfo: HostInfo, remote: String): String = {
    val home = remote.substring(0, remote.lastIndexOf("/"))
    val name = remote.substring(remote.lastIndexOf("/"))
    val dir = name.substring(0, name.lastIndexOf("."))
    shell(hostInfo, "cd " + home + ";unzip ." + name + " -d ." + dir, logOut)
    home + dir
  }

  /**
    * shell命令执行
    *
    * @param hostInfo host信息
    * @param cmd      命令
    */
  def shell(hostInfo: HostInfo, cmd: String): Boolean = {
    shell(hostInfo, cmd, null)
  }

  /**
    * shell命令执行
    *
    * @param hostInfo host信息
    * @param cmd      命令
    * @param log      输入流处理
    */
  def shell(hostInfo: HostInfo, cmd: String, log: LogOutput): Boolean = {
    shell(hostInfo, cmd, log, null)
  }

  /**
    * shell命令执行
    *
    * @param hostInfo host信息
    * @param cmd 命令
    * @param log 输入流处理
    * @param option 连接配置
    * @return
    */
  def shell(hostInfo: HostInfo, cmd: String, log: LogOutput, option: Properties): Boolean = {
    var session: SessionWrapper[ChannelExec] = null
    var exec: ChannelExec = null
    var in: InputStream = null
    try {
      session = new SessionWrapper[ChannelExec](hostInfo, option)
      exec = session.openChannel("exec")
      LOG.info(cmd)
      exec.setCommand(cmd)
      exec.setErrStream(System.err)
      exec.setPty(true)
      exec.connect()
      in = exec.getInputStream
      if (null != log) {
        val tmp = Array.fill[Byte](1024)(0)
        var run = true
        var index = 0
        while (run && !log.stop()) {
          var run2 = true
          while (run2 && in.available() > 0 && !log.stop()) {
            index = in.read(tmp, 0, 1024)
            if (index < 0) {
              run2 = false
            }
            log.apply(new String(tmp, 0, index))
          }
          if (exec.isClosed) {
            run = false
          }
          CmdWrapper.sleep(1000)
        }
      }
      true
    } catch {
      case e: Throwable =>
        LOG.error("", e)
        false
    } finally {
      if (!exec.isClosed) {
        exec.sendSignal("2")
      }
      IOUtils.closeQuietly(in)
      IOUtils.closeQuietly(session)
    }
  }

  trait LogOutput {
    def apply(obj: Object)

    def stop(): Boolean
  }

}
