package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import java.io.*;
import java.nio.charset.Charset;
import javax.swing.*;
import util.BuildProject;
import util.ExecCmd;
import util.SchUnitJsch;

/**
 * Created by lucky8987 on 17/10/24.
 */
public class DeployAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(DataKeys.PROJECT);
        String projectPaht = project.getProjectFilePath();
        // 项目编译
        boolean isBuild = BuildProject.gradleBuild(project.getBasePath());
        if (isBuild) {
            // 文件上传
            SchUnitJsch.uploadLocalFileToRemote(project.getBasePath().concat(File.separator).concat("build").concat(File.separator).concat("libs").concat(File.separator).concat(project.getName()).concat(".jar"), "/www");
            showLog(new SequenceInputStream(BuildProject.globalIn.get().elements()), project);
        }
    }

    public void showLog(final InputStream in, final Project project) {
        if (in != null) {
            // 将项目对象，ToolWindow的id传入，获取控件对象
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("auto-deploy");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final JTextArea jTextArea = (JTextArea) ((JScrollPane) toolWindow.getContentManager().getContent(0)
                            .getComponent().getComponent(0)).getViewport().getComponent(0);
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")))) {
                        if (jTextArea != null) {
                            String buf;
                            while ((buf = reader.readLine()) != null) {
                                jTextArea.append(buf);
                                jTextArea.append("\n");
                                jTextArea.paintImmediately(jTextArea.getBounds());
                            }
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
