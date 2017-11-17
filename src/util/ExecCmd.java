package util;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import java.io.*;
import java.nio.charset.Charset;

/**
 * 在远程服务器执行服务器指令
 * Created by lucky8987 on 17/10/24.
 */
public class ExecCmd {

    public static StringBuilder executeCommand(String command) {
        StringBuilder sb = new StringBuilder();
        ChannelExec channelExec = null;
        try {
            channelExec = (ChannelExec) SchUnitJsch.getChannel("exec");
            channelExec.setCommand("su www");
            channelExec.connect();
            OutputStream out = channelExec.getOutputStream();
            out.write("moseeker.com\n".getBytes());
            out.flush();
            out.write(command.getBytes());
            out.flush();
            InputStream in = channelExec.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            String buf = null;
            while ((buf = reader.readLine()) != null) {
                sb.append(buf);
                sb.append("\n");
                System.out.println(buf);
            }
        } catch (JSchException e) {
            e.printStackTrace();
            System.err.println("连接远程服务器失败!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("系统异常：" + e.getMessage());
        } finally {
            if (channelExec != null) {
                channelExec.disconnect();
            }
        }
        return sb;
    }

    public static InputStream executeCommandToIn(String command) {
        ChannelExec channelExec = null;
        try {
            channelExec = (ChannelExec) SchUnitJsch.getChannel("exec");
            channelExec.setCommand(command);
            channelExec.setInputStream(null);
            channelExec.setErrStream(System.err);
            channelExec.connect();
            return channelExec.getInputStream();
        } catch (JSchException e) {
            System.err.println("连接远程服务器失败!");
        } catch (Exception e) {
            System.out.println("系统异常：" + e.getMessage());
        } finally {
//            if (channelExec != null) {
//                channelExec.disconnect();
//            }
        }
        return null;
    }

    public static void main(String[] args) {
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ExecCmd.executeCommandToIn("tail -f /www/alphadog/log/mq_dev1.log"), Charset.forName("UTF-8")))) {
//            String buf;
//            while ((buf = reader.readLine()) != null) {
//                System.out.println(buf);
//            }
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
    }
}
