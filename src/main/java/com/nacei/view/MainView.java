package com.nacei.view;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.nacei.component.BindElementJPanel;
import com.nacei.component.ScheduleDialog;
import com.nacei.enums.OperationEnum;
import com.nacei.enums.RequstSignEnum;
import com.nacei.model.ClickResponseDTO;
import com.nacei.service.WebSocketServiceUtils;
import com.nacei.utils.CurrentUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class MainView<change> {
    private JFrame frame;
    private JPanel mainJPanel;
    private JTextField current_Address;
    private JButton upload_files;
    private JTabbedPane tabbedPane1;
    private JLabel status;
    private JButton start_app;
    private JPanel bind_input_jPanel;
    private JPanel content;
    private JSpinner spinner_unified_delay;
    private JButton clear_element;
    private JButton export_data;
    private JButton export_pattern;
    private JLabel element_count;

    private String connKey;

    public void setConnKey(String connKey) {
        this.connKey = connKey;
    }

    public GridBagLayout gridBagLayout = new GridBagLayout();
    public List<BindElementJPanel> bindInputJPanelMap = new ArrayList<>();

    public MainView(JFrame frame) {
        this.frame = frame;
        start_app.addActionListener(e -> {
            if (bindInputJPanelMap.size() <= 0) {
                JOptionPane.showMessageDialog(frame, "没有元素,先绑定!", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int[] currentProgress = {0};
            Timer timer = new Timer(7000, null);
            ScheduleDialog scheduleDialog = new ScheduleDialog(frame, true, 0, bindInputJPanelMap.size());
            scheduleDialog.show_dialog();
            scheduleDialog.addProgressBarStopTimer(timer);
            scheduleDialog.getProgressBar_dialog().setValue(currentProgress[0]);
            timer.addActionListener(formed -> {
                currentProgress[0]++;
                if (currentProgress[0] >= bindInputJPanelMap.size()) {
                    timer.stop();
                }
                BindElementJPanel v = bindInputJPanelMap.get(currentProgress[0] - 1);
                String item = (String) v.getOption_comboBox().getSelectedItem();
                if (StringUtils.isNotEmpty(OperationEnum.optionName(item)) && OperationEnum.click.getDesc().equals(item)) {
                    try {
                        ClickResponseDTO responseDTO = new ClickResponseDTO();
                        responseDTO.setCmd(RequstSignEnum.service_execute_program_settings.getCmdCode());
                        responseDTO.setElement(v.getElement_sgin().getText());
                        responseDTO.setDelayed(String.valueOf(v.getSpinner_delayed().getValue()));
                        String dto = JSON.toJSONString(responseDTO);
                        System.out.println(dto);
                        WebSocketServiceUtils.getWebSocketConn(connKey).onMessage(dto);
                        scheduleDialog.getProgressBar_dialog().setValue(currentProgress[0]);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "浏览器连接失败,请重试", "提示", JOptionPane.ERROR_MESSAGE);
                        timer.stop();
                    }
                }
            });
            timer.start();
            scheduleDialog.setVisible(true);
            scheduleDialog.setLocationRelativeTo(null);
        });
        spinner_unified_delay.addChangeListener(e -> {
            JSpinner spinner = (JSpinner) e.getSource();
            Object value = spinner.getValue();
            bindInputJPanelMap.forEach(v -> {
                v.getSpinner_delayed().setValue(value);
            });
        });
        clear_element.addActionListener(e -> {
            content.removeAll();
            bindInputJPanelMap.clear();
            content.updateUI();
            element_count.setText("count : " + bindInputJPanelMap.size() + " [Ln : 0]");
        });
        upload_files.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(false);
            /** 过滤文件类型 * */
//            FileNameExtensionFilter filter = new FileNameExtensionFilter(".csv",".xml", ".txt", ".doc", ".docx");
//            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(upload_files);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                /** 得到选择的文件* */
                File arrfiles = chooser.getSelectedFile();
                try (FileReader reader = new FileReader(arrfiles);
                     BufferedReader br = new BufferedReader(reader)
                ) {
                    String line;
                    //网友推荐更加简洁的写法
                    while ((line = br.readLine()) != null) {
                        // 一次读入一行数据
                        //System.out.println(line);
                        String[] row = line.split(",");
                        String url = row[0];
                        current_Address.setText(url);
                        int inputCount = bindInputJPanelMap.size();
                        content.setLayout(gridBagLayout);
                        GridBagConstraints gridBagConstraints = new GridBagConstraints();
                        gridBagConstraints.gridy = inputCount;
                        gridBagConstraints.gridx = 0;
                        gridBagConstraints.weightx = 1;  // 当窗口放大时，长度变
                        gridBagConstraints.fill = GridBagConstraints.BOTH;
                        BindElementJPanel bindElementJPanel = new BindElementJPanel();
                        gridBagLayout.setConstraints(bindElementJPanel.getBindJPane(), gridBagConstraints);
                        content.add(bindElementJPanel.getBindJPane());

                        String id = row[1];
                        bindElementJPanel.getElement_sgin().setText(id);

                        String type = "<" + row[2] + "/>";
                        bindElementJPanel.getElement_type().setText(type);

                        String option = row[3];
                        bindElementJPanel.getOption_comboBox().addItem(OperationEnum.get_pic.getDesc());
                        bindElementJPanel.getOption_comboBox().addItem(OperationEnum.click.getDesc());
                        bindElementJPanel.getOption_comboBox().addItem(OperationEnum.get_text.getDesc());
                        bindElementJPanel.getOption_comboBox().setSelectedItem(OperationEnum.optionDesc(option));

                        bindElementJPanel.getSpinner_delayed().setValue(Integer.valueOf(row[4]));
                        String elementContent = row[5];
                        bindElementJPanel.getCell_content_text().setText(elementContent);


                        //content.validate();// 重构内容面板
                        //content.repaint();// 重绘内容面板
                        //SwingUtilities.updateComponentTreeUI(content);
                        //SwingUtilities.updateComponentTreeUI(bind_input_jPanel);
                        bindInputJPanelMap.add(bindElementJPanel);
                        element_count.setText("count:   " + bindInputJPanelMap.size() + "   [Ln : 0]   ");
                        content.updateUI();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        export_data.addActionListener(e -> {
            if (bindInputJPanelMap.size() <= 0) {
                JOptionPane.showMessageDialog(frame, "没有可导出数据", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String dir = System.getProperty("user.dir");
            File file = new File(dir + File.separator + "export_" + CurrentUtil.serial_no() + ".csv");
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            final int[] current = {0};
            Timer timer = new Timer(0, null);
            ScheduleDialog scheduleDialog = new ScheduleDialog(frame, true, 0, bindInputJPanelMap.size());
            scheduleDialog.show_dialog();
            scheduleDialog.getProgressBar_dialog().setValue(current[0]);
            scheduleDialog.addProgressBarStopTimer(timer);
            timer.addActionListener(formed -> {
                current[0]++;
                if (current[0] >= bindInputJPanelMap.size()) {
                    timer.stop();
                }
                BindElementJPanel v = bindInputJPanelMap.get(current[0] - 1);
                PrintWriter out = null;
                try {
                    String text = v.getCell_content_text().getText();
                    if (StringUtils.isNotEmpty(text)) {
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8)));
                        text = text.replace("#&#", ",");
                        out.println(text);
                        out.flush();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "导出数据错误请重试", "提示", JOptionPane.ERROR_MESSAGE);
                } finally {
                    out.close();
                }
                scheduleDialog.getProgressBar_dialog().setValue(current[0]);
            });
            timer.start();
            scheduleDialog.setVisible(true);
            scheduleDialog.setLocationRelativeTo(null);
        });
    }

    public JPanel getJPanel() {
        return mainJPanel;
    }

    public JLabel statusLabel() {
        return status;
    }

    public JTextField getCurrent_Address() {
        return current_Address;
    }

    /**
     * 操作当前界面添加绑定元素样式
     *
     * @return
     */
    public boolean bindingInputBoxInterface(JSONObject jsonObject) {

        String url = jsonObject.getString("url");
        current_Address.setText(url);

        int inputCount = bindInputJPanelMap.size();
        content.setLayout(gridBagLayout);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = inputCount;
        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;  // 当窗口放大时，长度变
//        gridBagConstraints.weighty = 1;  // 当窗口放大时，高度变
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        //gridBagConstraints.anchor = GridBagConstraints.NORTH;
        BindElementJPanel bindElementJPanel = new BindElementJPanel();
        gridBagLayout.setConstraints(bindElementJPanel.getBindJPane(), gridBagConstraints);
        content.add(bindElementJPanel.getBindJPane());

        String id = jsonObject.getString("element");
        bindElementJPanel.getElement_sgin().setText(id);

        String type = "<" + jsonObject.getString("type") + "/>";
        bindElementJPanel.getElement_type().setText(type);

        String option = jsonObject.getString("option");
        bindElementJPanel.getOption_comboBox().addItem(OperationEnum.get_pic.getDesc());
        bindElementJPanel.getOption_comboBox().addItem(OperationEnum.click.getDesc());
        bindElementJPanel.getOption_comboBox().addItem(OperationEnum.get_text.getDesc());
        bindElementJPanel.getOption_comboBox().setSelectedItem(OperationEnum.optionDesc(option));

        String elementContent = jsonObject.getString("content");
        bindElementJPanel.getCell_content_text().setText(elementContent);

        //content.validate();// 重构内容面板
        //content.repaint();// 重绘内容面板
        //SwingUtilities.updateComponentTreeUI(content);
        //SwingUtilities.updateComponentTreeUI(bind_input_jPanel);
        bindInputJPanelMap.add(bindElementJPanel);

        element_count.setText("count  :  " + bindInputJPanelMap.size() + "  | [ Ln : 0 ]");
        content.updateUI();
        return true;
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
        mainJPanel = new JPanel();
        mainJPanel.setLayout(new GridLayoutManager(4, 7, new Insets(10, 10, 5, 10), -1, -1));
        mainJPanel.setBackground(new Color(-1));
        mainJPanel.setMaximumSize(new Dimension(100, 100));
        element_count = new JLabel();
        Font element_countFont = this.$$$getFont$$$(null, -1, 12, element_count.getFont());
        if (element_countFont != null) element_count.setFont(element_countFont);
        element_count.setText("本软件仅供测试使用如若他同一切后果由使用者承担");
        mainJPanel.add(element_count, new GridConstraints(3, 2, 1, 5, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setBackground(new Color(-1));
        mainJPanel.add(tabbedPane1, new GridConstraints(2, 0, 1, 7, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        panel1.setBackground(new Color(-1));
        panel1.setMaximumSize(new Dimension(100, 100));
        tabbedPane1.addTab("绑定管理", panel1);
        bind_input_jPanel = new JPanel();
        bind_input_jPanel.setLayout(new BorderLayout(0, 0));
        bind_input_jPanel.setBackground(new Color(-1));
        panel1.add(bind_input_jPanel, BorderLayout.CENTER);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 10, new Insets(5, 0, 5, 0), -1, -1));
        bind_input_jPanel.add(panel2, BorderLayout.NORTH);
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        final JLabel label1 = new JLabel();
        label1.setText("操作");
        panel2.add(label1, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, -1), null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("元素内容");
        panel2.add(label2, new GridConstraints(0, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("元素标志");
        panel2.add(label3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("类型");
        panel2.add(label4, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel2.add(spacer3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel2.add(spacer4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("延时(秒)");
        panel3.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        spinner_unified_delay = new JSpinner();
        spinner_unified_delay.setEnabled(true);
        panel3.add(spinner_unified_delay, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel3.add(spacer5, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel2.add(spacer6, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        panel4.setBackground(new Color(-1));
        bind_input_jPanel.add(panel4, BorderLayout.CENTER);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel4.add(scrollPane1, BorderLayout.CENTER);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new BorderLayout(0, 0));
        panel5.setBackground(new Color(-1));
        scrollPane1.setViewportView(panel5);
        content = new JPanel();
        content.setLayout(new BorderLayout(0, 0));
        content.setBackground(new Color(-1));
        panel5.add(content, BorderLayout.NORTH);
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setBackground(new Color(-1));
        tabbedPane1.addTab("模板管理", scrollPane2);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(6, 1, new Insets(5, 5, 5, 5), -1, -1));
        panel6.setBackground(new Color(-1));
        scrollPane2.setViewportView(panel6);
        final Spacer spacer7 = new Spacer();
        panel6.add(spacer7, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("模板名称");
        panel7.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JTextField textField1 = new JTextField();
        textField1.setMargin(new Insets(2, 6, 2, 6));
        panel7.add(textField1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JButton button1 = new JButton();
        button1.setText("编辑");
        panel7.add(button1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JButton button2 = new JButton();
        button2.setText("使用");
        panel7.add(button2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("模板名称");
        panel8.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JTextField textField2 = new JTextField();
        textField2.setMargin(new Insets(2, 6, 2, 6));
        panel8.add(textField2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JButton button3 = new JButton();
        button3.setText("编辑");
        panel8.add(button3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JButton button4 = new JButton();
        button4.setText("使用");
        panel8.add(button4, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel9, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("模板名称");
        panel9.add(label8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JTextField textField3 = new JTextField();
        textField3.setMargin(new Insets(2, 6, 2, 6));
        panel9.add(textField3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JButton button5 = new JButton();
        button5.setText("编辑");
        panel9.add(button5, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JButton button6 = new JButton();
        button6.setText("使用");
        panel9.add(button6, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel10, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("模板名称");
        panel10.add(label9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JTextField textField4 = new JTextField();
        textField4.setMargin(new Insets(2, 6, 2, 6));
        panel10.add(textField4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JButton button7 = new JButton();
        button7.setText("编辑");
        panel10.add(button7, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JButton button8 = new JButton();
        button8.setText("使用");
        panel10.add(button8, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel11, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("模板名称");
        panel11.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JTextField textField5 = new JTextField();
        textField5.setMargin(new Insets(2, 6, 2, 6));
        panel11.add(textField5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JButton button9 = new JButton();
        button9.setText("编辑");
        panel11.add(button9, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JButton button10 = new JButton();
        button10.setText("使用");
        panel11.add(button10, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        status = new JLabel();
        status.setText("服务端启动成功等待连接...");
        mainJPanel.add(status, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        upload_files = new JButton();
        upload_files.setAlignmentY(0.5f);
        upload_files.setBackground(new Color(-1));
        upload_files.setDefaultCapable(true);
        upload_files.setFocusable(false);
        upload_files.setIcon(new ImageIcon(getClass().getResource("/images/upload.png")));
        upload_files.setMargin(new Insets(5, 5, 5, 5));
        upload_files.setText("上传EXCEL");
        upload_files.putClientProperty("hideActionText", Boolean.FALSE);
        upload_files.putClientProperty("html.disable", Boolean.FALSE);
        mainJPanel.add(upload_files, new GridConstraints(0, 3, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        current_Address = new JTextField();
        current_Address.setEditable(false);
        current_Address.setEnabled(true);
        current_Address.setMargin(new Insets(2, 6, 2, 6));
        current_Address.setText("");
        current_Address.setVerifyInputWhenFocusTarget(true);
        current_Address.setVisible(true);
        mainJPanel.add(current_Address, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(300, 25), null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("当前操作网址");
        mainJPanel.add(label11, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("当前操作文件");
        mainJPanel.add(label12, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JTextField textField6 = new JTextField();
        textField6.setEditable(false);
        textField6.setEnabled(true);
        textField6.setMargin(new Insets(2, 6, 2, 6));
        textField6.setText("");
        mainJPanel.add(textField6, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(123, 25), null, 0, false));
        start_app = new JButton();
        start_app.setBackground(new Color(-1));
        start_app.setFocusable(false);
        start_app.setIcon(new ImageIcon(getClass().getResource("/images/start-app.png")));
        start_app.setMargin(new Insets(0, 0, 0, 0));
        start_app.setOpaque(true);
        start_app.setText("启动");
        start_app.setVerticalAlignment(0);
        start_app.setVisible(true);
        start_app.putClientProperty("html.disable", Boolean.FALSE);
        mainJPanel.add(start_app, new GridConstraints(0, 4, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(200, -1), null, 0, false));
        clear_element = new JButton();
        clear_element.setBackground(new Color(-1));
        clear_element.setFocusable(false);
        clear_element.setText("清空数据");
        mainJPanel.add(clear_element, new GridConstraints(0, 5, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        export_data = new JButton();
        export_data.setBackground(new Color(-1));
        export_data.setFocusable(false);
        export_data.setText("导出数据");
        mainJPanel.add(export_data, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        export_pattern = new JButton();
        export_pattern.setBackground(new Color(-1));
        export_pattern.setFocusable(false);
        export_pattern.setText("导出模板");
        mainJPanel.add(export_pattern, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainJPanel;
    }

}
