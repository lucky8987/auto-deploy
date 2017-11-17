package tool;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBList;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lucky8987 on 17/10/25.
 */
public class ToolFactoryCompute implements ToolWindowFactory {

    private ToolWindow myToolWindow;
    private JPanel mPanel;
    private JTextArea txtContent;
    private JScrollPane mScrollPane;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        myToolWindow = toolWindow;

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mPanel, "Control", false);
        toolWindow.getContentManager().addContent(content);

        // 禁止编辑
        txtContent.setEditable(false);

        // 鼠标点击事件
        txtContent.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //点击事件
                if (e.getButton() == 3) {
                    JBList jbList = new JBList();
                    String[] title = new String[2];
                    title[0] = "    Select All";
                    title[1] = "    Clear All";
                    jbList.setListData(title);
                    JBPopup popup = new PopupChooserBuilder(jbList).setItemChoosenCallback(() -> {
                        String value = String.valueOf(jbList.getSelectedValue());
                        if (title[0].equals(value)) {
                            txtContent.selectAll();
                        } else if (title[1].equals(value)) {
                            txtContent.setText("");
                        }
                    }).createPopup();

                    // 设置大小 2017/3/18 21:13
                    Dimension dimension = popup.getContent().getPreferredSize();
                    popup.setSize(new Dimension(150, dimension.height));
                    popup.show(new RelativePoint(e));

                    jbList.clearSelection();
                    jbList.setFocusable(false);

                    // 添加鼠标进入List事件
                    jbList.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            super.mouseEntered(e);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            super.mouseExited(e);
                            jbList.clearSelection();
                        }
                    });
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                txtContent.setCursor(new Cursor(Cursor.TEXT_CURSOR));   //鼠标进入Text区后变为文本输入指针
            }

            @Override
            public void mouseExited(MouseEvent e) {
                txtContent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));   //鼠标离开Text区后恢复默认形态
            }
        });
    }
}
