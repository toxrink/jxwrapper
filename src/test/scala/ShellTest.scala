import x.remote.ShellWrapper.LogOutput
import x.remote.{HostInfo, ShellWrapper}

/**
  * Created by xw on 2020/4/15.
  */
object ShellTest {
  def main(args: Array[String]): Unit = {
//    val cmd = "source /etc/profile;cd /data/vap-management-platform/admin;./admin.sh --cmd agent stop"
//    val hostInfo = new HostInfo
//    hostInfo.host = "192.168.56.105"
//    hostInfo.username = "root"
//    hostInfo.password = "root"
//    ShellWrapper.shell(hostInfo, cmd)

    val hostInfo = new HostInfo
    hostInfo.host = "192.168.119.206"
    hostInfo.username = "root"
    hostInfo.password = "root"
    ShellWrapper.shell(
      hostInfo,
      "tail -f -n -50 /var/log/vap-mp/agent-server.log",
      new LogOutput {
        var i = 0
        override def apply(obj: Object): Unit = {
          println(i + "#" + obj)
          i = i + 1
        }

        override def stop(): Boolean = {
          i > 3
        }
      }
    )

//    ShellWrapper.shell(hostInfo, "ls /",new LogOutput {
//      override def apply(obj: Object): Unit = println(obj)
//
//      override def stop(): Boolean = false
//    })
  }
}
