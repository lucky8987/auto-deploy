package util;

import java.io.*;
import java.util.Vector;

/**
 * 项目编译
 * Created by lucky8987 on 17/10/24.
 */
public class BuildProject {

    public static ThreadLocal<Vector<InputStream>> globalIn = new ThreadLocal<Vector<InputStream>>() {
       @Override
       protected Vector<InputStream> initialValue() {
           return new Vector<InputStream>();
       }
    };

    public static boolean gradleBuild(String projectDir) {
        StringBuilder sb = new StringBuilder("start building ...");
        try {
            Process buildProcess = Runtime.getRuntime().exec("gradle clean build -x test", null, new File(projectDir));
            // TODO 日志输出
            globalIn.get().add(buildProcess.getInputStream());
            // 等待项目编译结束
            buildProcess.waitFor();
            // 检查项目编译是否成功
            return new File(projectDir.concat(File.separator).concat("build").concat(File.separator).concat("libs")).isDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
