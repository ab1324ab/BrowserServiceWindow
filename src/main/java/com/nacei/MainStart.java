package com.nacei;

import com.nacei.component.UpdateDialog;
import com.nacei.service.WebSocketService;
import com.nacei.utils.WindowUtil;
import com.nacei.view.MainView;
import org.java_websocket.WebSocketImpl;

import javax.swing.*;
import java.awt.*;

public class MainStart {



    public static void main(String[] args) {
        new MainStart().init();
    }

    public void init() {
        JFrame frame = new JFrame();
        frame.setTitle("网页辅助操作工具" + UpdateDialog.VERSION_THIS);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(900, 500));

        MainView main = new MainView(frame);
        frame.getContentPane().add(main.getJPanel());
        WindowUtil.windowCentered(frame);

        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // 检测权限存在
       // checkAuthority(frame);
        // 实例化一个监听服务器
        WebSocketImpl.DEBUG = false;
        new WebSocketService(10239,main).start();
    }



    /**
     * 检查是否具有使用权限
     * @param frame
     */
    public void checkAuthority(JFrame frame){

        final UpdateDialog updateDialog = new UpdateDialog(frame);

        // 检测更新
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateDialog.checkUpdate();
            }
        }).start();
    }
}
