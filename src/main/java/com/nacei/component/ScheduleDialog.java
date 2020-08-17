package com.nacei.component;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.nacei.utils.WindowUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;

public class ScheduleDialog extends JDialog {
    private JProgressBar progressBar_dialog;
    private JLabel tips_dialog;
    private JPanel tips_panel;
    private Timer timer;

    public ScheduleDialog(Frame owner, Boolean modal, int minimum, int maximum) {
        super(owner, modal);
        progressBar_dialog.setMinimum(minimum);
        progressBar_dialog.setMaximum(maximum);
    }

    public void show_dialog() {
        setTitle("进度提示");
        setSize(500, 100);
        setLocationRelativeTo(null);
        WindowUtil.windowCentered(this);
        add(tips_panel);
        progressBar_dialog.addChangeListener(i -> {
            int value = progressBar_dialog.getValue();
            String complete = new BigDecimal(progressBar_dialog.getPercentComplete() * 100).setScale(0, BigDecimal.ROUND_HALF_UP) + "%";
            tips_dialog.setText("请稍等... " + value + " / " + complete);
        });
//        绘制百分比文本（进度条中间显示的百分数）
//        progressBar_dialog.setStringPainted(true);
    }

    public void addProgressBarStopTimer(Timer timer) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                timer.stop();
            }
        });
    }

    public JProgressBar getProgressBar_dialog() {
        return progressBar_dialog;
    }

    public void setProgressBar_dialog(JProgressBar progressBar_dialog) {
        this.progressBar_dialog = progressBar_dialog;
    }

    public JLabel getTips_dialog() {
        return tips_dialog;
    }

    public void setTips_dialog(JLabel tips_dialog) {
        this.tips_dialog = tips_dialog;
    }

    public JPanel getTips_panel() {
        return tips_panel;
    }

    public void setTips_panel(JPanel tips_panel) {
        this.tips_panel = tips_panel;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        tips_panel = new JPanel();
        tips_panel.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        final Spacer spacer1 = new Spacer();
        tips_panel.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        tips_panel.add(spacer2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 0, 10), -1, -1));
        tips_panel.add(panel1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        progressBar_dialog = new JProgressBar();
        panel1.add(progressBar_dialog, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 10, 0), -1, -1));
        tips_panel.add(panel2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tips_dialog = new JLabel();
        tips_dialog.setText("等待... 0 / 0%");
        panel2.add(tips_dialog, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return tips_panel;
    }

}
