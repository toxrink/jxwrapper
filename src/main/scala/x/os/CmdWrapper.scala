package x.os

import java.io._
import java.util
import java.util.zip.{GZIPInputStream, ZipInputStream}

import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveInputStream}
import org.apache.commons.compress.archivers.zip.{ZipArchiveEntry, ZipArchiveOutputStream}
import org.apache.commons.compress.compressors.gzip.GzipUtils
import org.apache.commons.io.{FileUtils, IOUtils}
import x.utils.{JxUtils, TimeUtils}

/**
  * Created by xw on 2019/8/1.
  */
object CmdWrapper {
  private val log = JxUtils.getLogger(CmdWrapper.getClass)

  /**
    * tail文件
    *
    * @param fileWatch 文件处理
    * @param file      监听文件
    * @param offset    开始位置
    * @param delay     输出延迟
    */
  def tailf(fileWatch: FileWatcher, file: File, offset: Int, delay: Int): Unit = {
    val filePath = file.getAbsolutePath
    log.info(s"start watch file: $filePath,offset $offset byte,push delay $delay ms")
    var raf: RandomAccessFile = null
    try {
      raf = new RandomAccessFile(file, "r")
      if (raf.length > offset) raf.skipBytes(raf.length.toInt - offset)
      var msg: String = null
      while (true) {
        msg = raf.readLine
        if (fileWatch.isStop) {
          log.info(s"stop watch file: $filePath")
          return
        }
        if (null == msg) {
          sleep(1000)
        } else {
          if (!fileWatch.isStop) {
            fileWatch.push(msg)
            if (0 != delay) sleep(delay)
          } else {
            log.info(s"stop watch file: $filePath")
            return
          }
        }
      }
    } catch {
      case e: Exception => log.error("", e)
    } finally {
      IOUtils.closeQuietly(raf)
    }
  }

  /**
    * 判断操作系统是否是windows
    *
    * @return
    */
  def isWindows: Boolean = {
    import org.apache.commons.lang.StringUtils
    val os = System.getenv("os")
    if (StringUtils.isEmpty(os)) {
      false
    } else {
      os.toLowerCase.contains("windows")
    }
  }

  /**
    * 等待
    *
    * @param millis 等待时间
    */
  def sleep(millis: Long): Unit = {
    Thread.sleep(millis)
  }

  /**
    * 创建文件夹
    *
    * @param path 文件夹路径
    */
  def mkdirs(path: String): Unit = {
    val file = new File(path)
    if (!file.exists()) {
      if (log.isDebugEnabled) {
        log.debug("create dirs " + path)
      }
      file.mkdirs()
    }
  }

  /**
    * zip 压缩
    *
    * @param fileInfoList 需要压缩的文件列表
    * @param prefix       生成的压缩文件前缀
    * @return
    */
  @throws[IOException]
  def zip(fileInfoList: util.List[FileInfo], prefix: String): File = {
    import scala.collection.JavaConversions._
    val zfile = File.createTempFile(prefix, ".zip")
    val zout = new ZipArchiveOutputStream(zfile)
    for (fileInfo <- fileInfoList.toList) {
      val file = new File(fileInfo.getPath)
      if (file.exists()) {
        val entry = new ZipArchiveEntry(fileInfo.getName)
        zout.putArchiveEntry(entry)
        IOUtils.write(FileUtils.readFileToByteArray(file), zout)
        zout.closeArchiveEntry()
      }
    }
    zout.close()
    zfile
  }

  /**
    * zip 文件解压
    *
    * @param filePath .zip文件路径
    * @param outDir   解压文件输出路径
    * @param encode   文件编码
    * @return
    */
  def unZip(filePath: String, outDir: String, encode: String): util.ArrayList[FileInfo] = {
    val list = new util.ArrayList[FileInfo]()
    var zip: ZipInputStream = null
    var br: BufferedReader = null
    try {
      val file = new File(filePath)
      zip = new ZipInputStream(new FileInputStream(file))
      br = new BufferedReader(new InputStreamReader(zip, encode))
      var entity = zip.getNextEntry
      var outFile: File = null
      val ts = TimeUtils.getTimestamp
      var fi: FileInfo = null
      var fout: FileOutputStream = null
      while (entity != null && !entity.isDirectory) {
        outFile = new File(outDir + File.separator + ts + "_" + entity.getName)
        mkdirs(outFile.getParent)
        fout = new FileOutputStream(outFile)
        IOUtils.copy(br, fout, encode)
        fout.close()
        log.info("create file " + outFile.getAbsolutePath)
        fi = FileInfo(outFile)
        list.add(fi)
        entity = zip.getNextEntry
      }
    } catch {
      case e: Exception => log.error("", e)
    } finally {
      IOUtils.closeQuietly(br)
      IOUtils.closeQuietly(zip)
    }
    list
  }

  /**
    * tar.zip 文件解压
    *
    * @param filePath .tar.gz文件路径
    * @param outDir   文件解压输出路径
    * @param encode   文件编码
    * @return
    */
  def unTarGzip(filePath: String, outDir: String, encode: String): util.ArrayList[FileInfo] = {
    val list = new util.ArrayList[FileInfo]()
    var tar: TarArchiveInputStream = null
    var br: BufferedReader = null
    try {
      val file = new File(filePath)
      val fileInput = new FileInputStream(file)
      val gzipInput = new GZIPInputStream(fileInput)
      tar = new TarArchiveInputStream(gzipInput)
      br = new BufferedReader(new InputStreamReader(tar, encode))
      var entity: TarArchiveEntry = tar.getNextTarEntry
      val ts = TimeUtils.getTimestamp
      var fi: FileInfo = null
      var fout: FileOutputStream = null
      var outFile: File = null
      while (entity != null && !entity.isDirectory) {
        outFile = new File(outDir + File.separator + ts + "_" + entity.getName)
        mkdirs(outFile.getParent)
        fout = new FileOutputStream(outFile)
        IOUtils.copy(br, fout, encode)
        fout.close()
        log.info("create file " + outFile.getAbsolutePath)
        fi = FileInfo(outFile)
        list.add(fi)
        entity = tar.getNextTarEntry
      }
    } catch {
      case e: Exception => log.error("", e)
    } finally {
      IOUtils.closeQuietly(br)
      IOUtils.closeQuietly(tar)
    }
    list
  }

  /**
    * gzip 文件解压
    *
    * @param filePath .gz文件路径
    * @param outDir   文件解压输出路径
    * @param encode   文件编码
    * @return
    */
  def unGzip(filePath: String, outDir: String, encode: String): util.ArrayList[FileInfo] = {
    val list = new util.ArrayList[FileInfo]()
    var gzipInput: GZIPInputStream = null
    var br: BufferedReader = null
    try {
      val file = new File(filePath)
      val fileInput = new FileInputStream(file)
      gzipInput = new GZIPInputStream(fileInput)
      br = new BufferedReader(new InputStreamReader(gzipInput, encode))
      val ts = TimeUtils.getTimestamp
      val unFileName = GzipUtils.getUncompressedFilename(file.getName)
      val outFile = new File(outDir + File.separator + ts + "_" + unFileName)
      mkdirs(outFile.getParent)
      val fout = new FileOutputStream(outFile)
      IOUtils.copy(br, fout, encode)
      fout.close()
      log.info("create file " + outFile.getAbsolutePath)
      val fi = FileInfo(outFile)
      list.add(fi)
    } catch {
      case e: Exception => log.error("", e)
    } finally {
      IOUtils.closeQuietly(br)
      IOUtils.closeQuietly(gzipInput)
    }
    list
  }

  /**
    * 执行cmd
    *
    * @param cmd 命令
    * @return
    */
  def run(cmd: String): JxProcess = {
    var jxProcess: JxProcess = null
    try {
      log.info(cmd)
      jxProcess = new JxProcess(Runtime.getRuntime.exec(cmd))
    } catch {
      case e: Throwable => log.error("", e)
    }
    jxProcess
  }

  /**
    * 执行cmd
    *
    * @param command 命令
    * @return
    */
  def run(command: Array[String]): JxProcess = {
    run(command.mkString(" "))
  }

  /**
    * 包含运行环境变量执行cmd
    *
    * @param command 命令
    * @return
    */
  def runWithEnv(command: Array[String]): JxProcess = {
    var jxProcess: JxProcess = null
    try {
      log.info(command.mkString(" "))
      val processBuilder = new ProcessBuilder(command: _*)
      jxProcess = new JxProcess(processBuilder.start())
    } catch {
      case e: Throwable => log.error("", e)
    }
    jxProcess
  }
}
