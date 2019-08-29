package x.remote;

import java.io.File;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import x.utils.JxUtils;

public final class SftpWrapper {
    private static final Log LOG = JxUtils.getLogger(SftpWrapper.class);

    /**
     * 远程复制文件
     *
     * @param sftpInfo sftp信息
     * @return
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
     * @param sftp
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
            LOG.info(sftp.getSession().getHost() + " FETCH " + local);
            sftp.put(local, remote);
        }
    }

    /**
     * 创建远程目录
     *
     * @param remote
     * @param sftp
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
     * @param remote
     * @param sftp
     * @return
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
     * 判断远程文件是否存在
     *
     * @param remote
     * @param sftp
     * @return
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
     * @param sftpInfo
     * @return
     */
    public static boolean get(SftpInfo sftpInfo) {
        SessionWrapper<ChannelSftp> session = null;
        ChannelSftp sftp = null;
        try {
            session = new SessionWrapper<ChannelSftp>(sftpInfo);
            sftp = session.openChannel("sftp");
            sftp.connect();
            download(sftpInfo.getLocal(), sftpInfo.getRemote(), sftp);
        } catch (JSchException e) {
            LOG.error("", e);
            return false;
        } finally {
            IOUtils.closeQuietly(session);
        }
        return true;
    }

    /**
     * 下载文件
     *
     * @param local  本地保存路径
     * @param remote 远程文件路径
     * @param sftp
     */
    public static void download(String local, String remote, ChannelSftp sftp) {
        download(local, remote, -1, sftp);
    }

    @SuppressWarnings("unchecked")
    public static void download(String local, String remote, int base, ChannelSftp sftp) {
        if (!isRemoteExist(remote, sftp)) {
            LOG.info(remote + " does not exist");
            return;
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
        }
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
