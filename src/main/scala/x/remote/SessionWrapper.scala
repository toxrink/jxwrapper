package x.remote

import java.io.Closeable
import java.util.Properties

import com.jcraft.jsch.{Channel, JSch, JSchException}

/**
 * Created by xw on 2019/8/29.
 */
@throws[JSchException]
class SessionWrapper[T <: Channel](hostInfo: HostInfo, option: Properties) extends Closeable {

  private val jsch = new JSch()
  private val session = jsch.getSession(hostInfo.getUsername, hostInfo.getHost)
  session.setPassword(hostInfo.getPassword)
  session.setConfig("StrictHostKeyChecking", "no")
  if (null != option) {
    session.setConfig(option)
  }
  session.connect()

  private var channel: Channel = _

  def this(hostInfo: HostInfo) = {
    this(hostInfo, null)
  }

  @throws[JSchException]
  def openChannel(name: String): T = {
    channel = session.openChannel(name)
    channel.asInstanceOf[T]
  }

  override def close(): Unit = {
    if (null != channel) {
      channel.disconnect()
    }
    if (null != session) {
      session.disconnect()
    }
  }
}
