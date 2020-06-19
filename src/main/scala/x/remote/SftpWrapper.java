package x.remote;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import x.utils.JxUtils;

import java.io.File;

public final class SftpWrapper {
    private static final Log LOG = JxUtils.getLogger(SftpWrapper.class);

    /**
     * 远程复制文件
     *
     * @param sftpInfo sftp信息
     * @return 返回是否成功
     */
    public static boolean scp(SftpInfo sftpInfo) {
        SessionWrapper<ChannelSftp> session = null;
        ChannelSftp sftp = null;
        try {
            session = new SessionWrapper<ChannelSftp>(sftpInfo);
            sftp = session.openChannel("sftp");
            sftp.connect();
            upload(sftpInfo.getLocal(), sftpInfo.getRemote(), sftp);
        } catch (JSchException | SftpException e) {
            LOG.error("", e);
            return false;
        } finally {
            IOUtils.closeQuietly(session);
        }
        return true;
    }

    /**
     * 上传文件
     *
     * @param local  本地文件路径
     * @param remote 远程保存路径
     * @param sftp   连接客户端
     * @throws SftpException
     * @throws JSchException
     */
    public static void upload(String local, String remote, ChannelSftp sftp) throws SftpException, JSchException {
        createRemoteDirectory(remote, sftp);
        File file = new File(local);
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                upload(f.getAbsolutePath(), remote + "/" + file.getName(), sftp);
            }
        } else {
            if (file.exists()) {
                LOG.info(sftp.getSession().getHost() + " FETCH " + file.getAbsolutePath());
                sftp.put(file.getAbsolutePath(), remote);
            } else {
                throw new JSchException(local + " file not exists");
            }

        }
    }

    /**
     * 创建远程目录
     *
     * @param remote 远程目录路径
     * @param sftp   连接客户端
     */
    public static void createRemoteDirectory(String remote, ChannelSftp sftp) {
        if (!isRemoteDirectoryExist(remote, sftp)) {
            String[] a1 = remote.split("/");
            String[] a2 = new String[a1.length - 1];
            System.arraycopy(a1, 0, a2, 0, a2.length);
            String remoteParent = String.join("/", a2);
            createRemoteDirectory(remoteParent, sftp);
            try {
                LOG.info(sftp.getSession().getHost() + " CREATE " + remote);
                sftp.mkdir(remote);
            } catch (SftpException | JSchException e) {
                LOG.error("", e);
            }
        }
    }

    /**
     * 判断远程目录是否存在
     *
     * @param remote 远程目录路径
     * @param sftp   连接客户端
     * @return 返回是否成功
     */
    public static boolean isRemoteDirectoryExist(String remote, ChannelSftp sftp) {
        try {
            if ("".equals(remote) || "/".equals(remote)) {
                return true;
            }
            return sftp.lstat(remote).isDir();
        } catch (SftpException e) {
            if (e.getMessage().equalsIgnoreCase("No such file")) {
                return false;
            }
            LOG.error("", e);
        }
        return true;
    }

    /**
     * 判断远程目录是否存在
     *
     * @param remote   远程目录路径
     * @param sftpInfo 连接
     * @return 返回是否成功
     */
    public static boolean isRemoteDirectoryExist(String remote, SftpInfo sftpInfo) {
        SessionWrapper<ChannelSftp> session = null;
        ChannelSftp sftp = null;
        boolean exist = false;
        try {
            session = new SessionWrapper<ChannelSftp>(sftpInfo);
            sftp = session.openChannel("sftp");
            sftp.connect();
            exist = SftpWrapper.isRemoteDirectoryExist(remote, sftp);
        } catch (JSchException e) {
            LOG.error("", e);
        } finally {
            IOUtils.closeQuietly(session);
        }
        return exist;
    }

    /**
     * 判断远程文件是否存在
     *
     * @param remote   远程目录路径
     * @param sftpInfo 连接
     * @return 返回是否存在
     */
    public static boolean isRemoteExist(String remote, SftpInfo sftpInfo) {
        SessionWrapper<ChannelSftp> session = null;
        ChannelSftp sftp = null;
        boolean exist = false;
        try {
            session = new SessionWrapper<ChannelSftp>(sftpInfo);
            sftp = session.openChannel("sftp");
            sftp.connect();
            exist = SftpWrapper.isRemoteExist(remote, sftp);
        } catch (JSchException e) {
            LOG.error("", e);
        } finally {
            IOUtils.closeQuietly(session);
        }
        return exist;
    }

    /**
     * 判断远程文件是否存在
     *
     * @param remote 远程目录路径
     * @param sftp   连接客户端
     * @return 返回是否存在
     */
    public static boolean isRemoteExist(String remote, ChannelSftp sftp) {
        try {
            if ("".equals(remote) || "/".equals(remote)) {
                return true;
            }
            sftp.lstat(remote);
        } catch (SftpException e) {
            if (e.getMessage().equalsIgnoreCase("No such file")) {
                return false;
            }
            LOG.error("", e);
        }
        return true;
    }

    /**
     * 下载文件
     *
     * @param sftpInfo 连接信息
     * @return 返回是否成功
     */
    public static boolean get(SftpInfo sftpInfo) {
        SessionWrapper<ChannelSftp> session = null;
        ChannelSftp sftp = null;
        try {
            session = new SessionWrapper<ChannelSftp>(sftpInfo);
            sftp = session.openChannel("sftp");
            sftp.connect();
            return download(sftpInfo.getLocal(), sftpInfo.getRemote(), sftp);
        } catch (JSchException e) {
            LOG.error("", e);
            return false;
        } finally {
            IOUtils.closeQuietly(session);
        }
    }

    /**
     * 下载文件
     *
     * @param local  本地保存路径
     * @param remote 远程文件路径
     * @param sftp   连接客户端
     * @return
     */
    public static boolean download(String local, String remote, ChannelSftp sftp) {
        return download(local, remote, -1, sftp);
    }

    @SuppressWarnings("unchecked")
    public static boolean download(String local, String remote, int base, ChannelSftp sftp) {
        if (!isRemoteExist(remote, sftp)) {
            LOG.warn(remote + " does not exist");
            return false;
        }
        if (base == -1) {
            base = remote.lastIndexOf("/");
        }
        try {
            SftpATTRS attrs = sftp.lstat(remote);
            if (attrs.isDir()) {
                new File(local + "/" + remote.substring(base)).mkdir();
                for (LsEntry e : (java.util.Vector<LsEntry>) sftp.ls(remote)) {
                    if (!e.getFilename().startsWith(".")) {
                        LOG.info("FETCH " + remote);
                        download(local, remote + "/" + e.getFilename(), base, sftp);
                    }
                }
            } else {
                LOG.info("DOWNLOAD " + remote);
                sftp.get(remote, local + "/" + remote.substring(base));
            }
        } catch (SftpException e) {
            LOG.error("", e);
            return false;
        }
        return true;
    }

//	public static void main(String[] args) {
//		SftpInfo info = new SftpInfo();
//		info.setHost("192.168.119.218");
//		info.setPort(22);
//		info.setLocal("F:\\test");
////		info.setLocal("D:\\eclipse-workspace\\vap-manangement-platform\\dist");
//		info.setRemote("/data/xw/test");
//		info.setUsername("root");
//		info.setPassword("root");
////		scp(info);
//		get(info);
//	}
}
