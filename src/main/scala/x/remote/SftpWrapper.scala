package x.remote

import java.io.File

import com.jcraft.jsch.{ChannelSftp, JSchException, SftpException}
import org.apache.commons.io.IOUtils
import x.log.Xlog

/**
  * Created by xw on 2020/06/28.
  */
object SftpWrapper {
  private val LOG = Xlog.getLogger(SftpWrapper.getClass())

  /**
    * 远程复制文件
    *
    * @param sftpInfo sftp信息
    * @return
    */
  def scp(sftpInfo: SftpInfo): Boolean = {
    var session: SessionWrapper[ChannelSftp] = null
    try {
      session = new SessionWrapper[ChannelSftp](sftpInfo)
      val sftp = session.openChannel("sftp")
      sftp.connect()
      upload(sftpInfo.getLocal(), sftpInfo.getRemote(), sftp)
    } catch {
      case e: Exception =>
        LOG.error("", e)
        return false
    } finally {
      IOUtils.closeQuietly(session)
    }
    true
  }

  /**
    *上传文件
    *
    * @param local 本地文件路径
    * @param remote 远程保存路径
    * @param sftp  连接客户端
    */
  @throws[SftpException]
  @throws[JSchException]
  def upload(local: String, remote: String, sftp: ChannelSftp): Unit = {
    createRemoteDirectory(remote, sftp)
    val file = new File(local)
    if (file.isDirectory()) {
      file
        .listFiles()
        .foreach(f => {
          upload(f.getAbsolutePath(), remote + "/" + file.getName(), sftp)
        })
    } else {
      if (file.exists()) {
        LOG.info(sftp.getSession().getHost() + " FETCH " + file.getAbsolutePath())
        sftp.put(file.getAbsolutePath(), remote)
      } else {
        throw new JSchException(local + " file not exists")
      }
    }
  }

  /**
    * 创建远程目录
    *
    * @param remote 远程目录路径
    * @param sftp 连接客户端
    */
  def createRemoteDirectory(remote: String, sftp: ChannelSftp): Unit = {
    if (!isRemoteDirectoryExist(remote, sftp)) {
      val a1 = remote.split("/")
      val a2 = new Array[String](a1.length - 1)
      System.arraycopy(a1, 0, a2, 0, a2.length)
      val remoteParent = a2.mkString("/")
      createRemoteDirectory(remoteParent, sftp)
      try {
        LOG.info(sftp.getSession().getHost() + " CREATE " + remote)
        sftp.mkdir(remote)
      } catch {
        case e: Exception => LOG.error("", e)
      }
    }
  }

  /**
    *判断远程目录是否存在
    *
    * @param remote 远程目录路径
    * @param sftp 连接客户端
    * @return
    */
  def isRemoteDirectoryExist(remote: String, sftp: ChannelSftp): Boolean = {
    try {
      if ("".equals(remote) || "/".equals(remote)) {
        return true
      }
      return sftp.lstat(remote).isDir()
    } catch {
      case e: Exception =>
        if (e.getMessage().equalsIgnoreCase("No such file")) {
          return false
        }
        LOG.error("", e)
    }
    true
  }

  /**
    * 判断远程目录是否存在
    *
    * @param remote 远程目录路径
    * @param sftpInfo 连接
    * @return
    */
  def isRemoteDirectoryExist(remote: String, sftpInfo: SftpInfo): Boolean = {
    var session: SessionWrapper[ChannelSftp] = null
    var exist = false
    try {
      session = new SessionWrapper[ChannelSftp](sftpInfo)
      val sftp = session.openChannel("sftp")
      sftp.connect()
      exist = SftpWrapper.isRemoteDirectoryExist(remote, sftp)
    } catch {
      case e: Exception => LOG.error("", e)
    } finally {
      IOUtils.closeQuietly(session)
    }
    exist
  }

  /**
    * 判断远程文件是否存在
    *
    * @param remote 远程目录路径
    * @param sftpInfo 连接信息
    * @return
    */
  def isRemoteExist(remote: String, sftpInfo: SftpInfo): Boolean = {
    var session: SessionWrapper[ChannelSftp] = null
    var exist = false
    try {
      session = new SessionWrapper[ChannelSftp](sftpInfo)
      val sftp = session.openChannel("sftp")
      sftp.connect()
      exist = SftpWrapper.isRemoteExist(remote, sftp)
    } catch {
      case e: Exception => LOG.error("", e)
    } finally {
      IOUtils.closeQuietly(session)
    }
    exist
  }

  /**
    * 判断远程文件是否存在
    *
    * @param remote 远程目录路径
    * @param sftp 连接客户端
    * @return
    */
  def isRemoteExist(remote: String, sftp: ChannelSftp): Boolean = {
    try {
      if ("".equals(remote) || "/".equals(remote)) {
        return true
      }
      sftp.lstat(remote).isFifo()
    } catch {
      case e: SftpException =>
        if (e.getMessage().equalsIgnoreCase("No such file")) {
          return false
        }
        LOG.error("", e)
    }
    true
  }

  /**
    * 下载文件
    *
    * @param sftpInfo 连接信息
    * @return
    */
  def get(sftpInfo: SftpInfo): Boolean = {
    var ret = false
    var session: SessionWrapper[ChannelSftp] = null
    try {
      session = new SessionWrapper[ChannelSftp](sftpInfo)
      val sftp = session.openChannel("sftp")
      sftp.connect()
      ret = download(sftpInfo.getLocal(), sftpInfo.getRemote(), sftp)
    } catch {
      case e: Exception =>
        LOG.error("", e)
        ret = false
    } finally {
      IOUtils.closeQuietly(session)
    }
    ret
  }

  /**
    * 下载文件
    *
    * @param local 本地保存路径
    * @param remote 远程文件路径
    * @param sftp 连接客户端
    * @return
    */
  def download(local: String, remote: String, sftp: ChannelSftp): Boolean = {
    download(local, remote, -1, sftp)
  }

  /**
    * 下载文件
    *
    * @param local 本地保存路径
    * @param remote 远程文件路径
    * @param baseIn 最后一个"/"位置
    * @param sftp 连接客户端
    * @return
    */
  def download(local: String, remote: String, baseIn: Int, sftp: ChannelSftp): Boolean = {
    if (!isRemoteExist(remote, sftp)) {
      LOG.warn(remote + " does not exist")
      false
    } else {
      var base = baseIn
      if (base == -1) {
        base = remote.lastIndexOf("/")
      }
      try {
        val attrs = sftp.lstat(remote)
        if (attrs.isDir()) {
          new File(local + "/" + remote.substring(base)).mkdir()
          val list = sftp.ls(remote).asInstanceOf[java.util.Vector[sftp.LsEntry]]
          import scala.collection.JavaConverters._
          list.asScala.foreach((e: sftp.LsEntry) => {
            if (!e.getFilename().startsWith(".")) {
              LOG.info("FETCH " + remote)
              download(local, remote + "/" + e.getFilename(), base, sftp)
            }
          })
        } else {
          LOG.info("DOWNLOAD " + remote)
          sftp.get(remote, local + "/" + remote.substring(base))
        }
      } catch {
        case e: Exception =>
          LOG.error("", e)
          return false
      }
      true
    }
  }
}
