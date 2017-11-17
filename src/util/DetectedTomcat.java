package util;

import java.util.Arrays;
import org.apache.commons.lang.StringUtils;

/**
 * 将war包部署到远程服务器
 * Created by lucky8987 on 17/10/24.
 */
public class DetectedTomcat {

    private String checkTomcatCmd = "ps -ef | grep Tomcat | grep -v grep";

    public String baseTomcatHome;

    /**
     * 检查tomcat是否启动
     * @param commmand
     * @return
     */
    public boolean isStartingTomcat(String commmand) {
        if (commmand == null || commmand.equals("")) {
            commmand = checkTomcatCmd;
        }
        StringBuilder sb = ExecCmd.executeCommand(commmand);
        System.out.println(sb.toString());
        return StringUtils.isBlank(sb.toString());
    }

    /**
     * 关闭tomcat
     * @param shutdownCmd
     * @return
     */
    public boolean shutdownTomcat(String shutdownCmd) {
        if (isStartingTomcat(checkTomcatCmd)) {
            if (StringUtils.isBlank(shutdownCmd)) {
                StringBuilder sb = ExecCmd.executeCommand(checkTomcatCmd);
                String[] fragments = sb.toString().split("\\s+");
                Arrays.stream(fragments).forEach(e -> {
                    if (e.indexOf("catalina.base")  > -1) {
                        baseTomcatHome = e.split("=")[1];
                        return;
                    }
                });
                if (baseTomcatHome != null) {
                    shutdownCmd = "sh ".concat(baseTomcatHome).concat("/bin/shutdown.sh");
                }
            }
            ExecCmd.executeCommand(shutdownCmd);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean success = isStartingTomcat(checkTomcatCmd);
            if (success) {
                StringBuilder sb = ExecCmd.executeCommand(checkTomcatCmd);
                ExecCmd.executeCommand("kill -9".concat(sb.toString().split("\\s+")[1]));
            }
            return true;
        }
        return true;
    }

    /**
     * 重启tomcat
     */
    public void restartTomcat(String cmd) {
        if (isStartingTomcat(null)) {
            shutdownTomcat(null);
        }
        if (StringUtils.isBlank(cmd)) {
            cmd = "sh ".concat(baseTomcatHome).concat("/bin/startup.sh");
        }
        ExecCmd.executeCommand(cmd);
    }

    /**
     * 删除war包
     * @return
     */
    public boolean deleateWar() {
        String regex = baseTomcatHome+"/webapps/ROOT*";
        String deleteCommand = "rm -rf "+ regex;
        String  detectFileCommand = "ls "+baseTomcatHome+"/webapps | grep ROOT";
        String commands= deleteCommand+" ; "+detectFileCommand;
        StringBuilder sb= ExecCmd.executeCommand(commands);
        return StringUtils.isBlank(sb.toString());
    }

    /**
     * 上传war包
     * @param localFile
     * @return
     */
    public boolean uploadWar(String localFile) {
        String remoteDir = baseTomcatHome + "/webapps";
        return SchUnitJsch.uploadLocalFileToRemote(localFile, remoteDir);
    }
}
