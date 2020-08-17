package com.nacei.utils;

import java.awt.*;

public class WindowUtil {

    /**
     * 窗口居中
     * @param window
     */
    public static void windowCentered(Window window) {
        Toolkit kit = window.getToolkit();
        try {
            Image image = kit.getImage(WindowUtil.class.getClass().getResource("/images/icon.png"));
            window.setIconImage(image);
            Dimension screenSize = kit.getScreenSize();
            int screenSizeWidth = screenSize.width;
            int screenSizeheight = screenSize.height;
            int windownWidth = window.getWidth();
            int windownHeight = window.getHeight();
            window.setLocation((screenSizeWidth - windownWidth) / 2, (screenSizeheight - windownHeight) / 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
