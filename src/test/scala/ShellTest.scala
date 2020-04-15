import x.remote.{HostInfo, ShellWrapper}

/**
  * Created by xw on 2020/4/15.
  */
object ShellTest {
  def main(args: Array[String]): Unit = {
    val cmd = "source /etc/profile;cd /data/vap-management-platform/admin;./admin.sh --cmd agent stop"
    val hostInfo = new HostInfo
    hostInfo.host = "192.168.56.105"
    hostInfo.username = "root"
    hostInfo.password = "root"
    ShellWrapper.shell(hostInfo, cmd)
  }
}
