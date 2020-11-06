import x.os.CmdWrapper
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

    var sstop = false

    val lo = new LogOutput {
      var i = 0

      override def apply(obj: Object): Unit = {
        println(s"$i#$obj")
        i = i + 1
        CmdWrapper.sleep(1000)
      }

      override def stop(): Boolean = {
        sstop
      }
    }
    new Thread(new Runnable {
      override def run(): Unit = {
        ShellWrapper.tailFile(
          hostInfo,
          "/var/log/vap-mp/agent-server.log",
          "-n -5 ",
          lo
        )
      }
    }).start()

    var i = 0
    while (i < 30) {
      i = i + 1
      CmdWrapper.sleep(1000)
      if (i > 15) {
        sstop = true
      }
    }

//    ShellWrapper.shell(hostInfo, "chmod +x /data/vap-management-platform/admin/admin.sh")

//    ShellWrapper.shell(
//      hostInfo,
//      "tail -f -n -50 /var/log/vap-mp/agent-server.log",
//      new LogOutput {
//        var i = 0
//        override def apply(obj: Object): Unit = {
//          println(i + "#" + obj)
//          i = i + 1
//        }
//
//        override def stop(): Boolean = {
//          i > 3
//        }
//      }
//    )

//    ShellWrapper.shell(hostInfo, "ls /",new LogOutput {
//      override def apply(obj: Object): Unit = println(obj)
//
//      override def stop(): Boolean = false
//    })
  }
}
