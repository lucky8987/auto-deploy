package util;

import com.jcraft.jsch.*;
import org.apache.commons.lang.StringUtils;


/**
 * Created by lucky8987 on 17/10/24.
 * 通过ssh登陆远程服务器
 */
public class SchUnitJsch {

    private static String userName = "xxx";
    private static String host = "127.0.0.1";
    private static String KnownHosts = "/Users/lucky8987/.ssh/known_hosts";
    private static String identity = "/Users/lucky8987/.ssh/id_rsa";

    private static Session getSession() throws JSchException {
        JSch jsch = new JSch();
        jsch.setKnownHosts(KnownHosts);
        jsch.addIdentity(identity);
        Session session = jsch.getSession(userName, host);
        session.connect();
        System.out.println("连接服务器成功：success");
        return session;
    }

    /**
     * default type: exec
     * @param channelType
     * @return
     * @throws JSchException
     */
    public static Channel getChannel(String channelType) throws JSchException {
        channelType = StringUtils.defaultIfEmpty(channelType, "exec");
        Session session = getSession();
        Channel channel = session.openChannel(channelType);
        return channel;
    }

    /**
     * sftp 文件上传到远程服务器
     * @param localFile
     * @param remoteDir
     * @return
     */
    public static boolean uploadLocalFileToRemote(String localFile, String remoteDir) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = (ChannelSftp) getSession().openChannel("sftp");
            channelSftp.connect();
            SftpProgressMonitorImpl sftpProgressMonitor = new SftpProgressMonitorImpl();
            channelSftp.put(localFile, remoteDir, sftpProgressMonitor);
            return sftpProgressMonitor.isSuccess();
        } catch (JSchException e) {
            e.printStackTrace();
            System.err.println("服务器：" + host + ", 连接失败。。。");
        } catch (SftpException e) {
            e.printStackTrace();
            System.err.println("文件上传失败：" + e.getMessage());
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
        }

        return false;
    }

    /**
     * 上传镜像映射检查
     */
    static class SftpProgressMonitorImpl implements SftpProgressMonitor {

        private long size;
        private long currentSize = 0;
        private boolean endFlag = false;

        @Override
        public void init(int op, String srcFile, String dstDir, long size) {
            this.size = size;
            System.out.println("文件开始上传：" + srcFile);
            System.out.println("文件大小：" + size + "字节");
            System.out.println("参数：" + op);
        }

        @Override
        public boolean count(long l) {
            currentSize += l;
            return true;
        }

        @Override
        public void end() {
            System.out.println("文件上传结束");
            endFlag = true;
        }

        public boolean isSuccess() {
            return endFlag ? endFlag : currentSize == size;
        }
    }

    /**
     * 删除远程服务器文件或目录
     * @param remoteFile
     * @return
     */
    public static boolean deleteRemoteFileOrDir(String remoteFile) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = (ChannelSftp) getSession().openChannel("sftp");
            channelSftp.connect();
            SftpATTRS sftpATTRS = channelSftp.lstat(remoteFile);
            if (sftpATTRS.isDir()) {
                channelSftp.rmdir(remoteFile);
                System.out.println("成功删除目录：" + remoteFile);
            } else if(sftpATTRS.isReg()) {
                channelSftp.rm(remoteFile);
                System.out.println("成功删除文件：" + remoteFile);
            } else {
                System.err.println("未找到该文件或目录!");
                return false;
            }
        } catch (JSchException e) {
            e.printStackTrace();
            System.err.println("服务器：" + host + ", 连接失败。。。");
        } catch (SftpException e) {
            e.printStackTrace();
            System.err.println("删除文件：" + remoteFile + ", 失败");
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
        }
        return true;
    }

}
