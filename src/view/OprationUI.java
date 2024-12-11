package view;

import javax.swing.*;

import dao.JobDAO;

import java.awt.*;
import java.io.File;

import test.WTUNewsCrawlerTest;
import vo.JobInfo;
import vo.User;
import test.SeleniumTest;
import test.JobCrawlerTest;
import java.util.List;

public class OprationUI extends JFrame {
    private JComboBox<String> testSelector;
    private JButton runButton;
    private JButton stopButton;
    private Thread testThread;

    public OprationUI() {

        setTitle("爬虫测试界面");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // 居中显示
        initComponents();
    }

    private void initComponents() {
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 创建顶部控制面板，使用GridLayout排成两行
        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 创建第一行面板
        JPanel firstRowPanel = new JPanel();
        String[] tests = {"58同城招聘", "武纺新闻", "selenium测试"};
        testSelector = new JComboBox<>(tests);
        runButton = new JButton("运行测试");
        stopButton = new JButton("终止测试");
        
        stopButton.setEnabled(false);
        
        firstRowPanel.add(testSelector);
        firstRowPanel.add(runButton);
        firstRowPanel.add(stopButton);

        // 创建第二行面板
        JPanel secondRowPanel = new JPanel();
        JButton sqlButton = new JButton("执行SQL");
        JButton exportCSVButton = new JButton("导出CSV");
        JButton exportXLSButton = new JButton("导出Excel");
        
        secondRowPanel.add(sqlButton);
        secondRowPanel.add(exportCSVButton);
        secondRowPanel.add(exportXLSButton);

        // 将两行面板添加到控制面板
        controlPanel.add(firstRowPanel);
        controlPanel.add(secondRowPanel);

        // 添加到主面板
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // 添加事件监听
        runButton.addActionListener(e -> {
            runButton.setEnabled(false);
            stopButton.setEnabled(true);
            runTest();
        });

        stopButton.addActionListener(e -> {
            if (testThread != null && testThread.isAlive()) {
                testThread.interrupt();
                System.out.println("测试已终止");
                runButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });
        // 添加执行SQL按钮的事件监听
        sqlButton.addActionListener(e -> {
            new MysqlUI(this).setVisible(true);
        });
        // 添加导出CSV按钮的事件监听
        exportCSVButton.addActionListener(e -> {
            List<JobInfo> jobList = JobDAO.getAllJobs();
            if (!jobList.isEmpty()) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("选择保存位置");
                fileChooser.setSelectedFile(new File("job_info.csv"));
                
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    JobDAO.exportToCSV(jobList, filePath);
                }
            } else {
                JOptionPane.showMessageDialog(this, "没有可导出的数据！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 添加导出Excel按钮的事件监听
        exportXLSButton.addActionListener(e -> {
            List<JobInfo> jobList = JobDAO.getAllJobs();
            if (!jobList.isEmpty()) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("选择保存位置");
                fileChooser.setSelectedFile(new File("job_info.xls"));
                
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    JobDAO.exportToXLS(jobList, filePath);
                }
            } else {
                JOptionPane.showMessageDialog(this, "没有可导出的数据！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 添加菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu userMenu = new JMenu("用户");
        JMenuItem changePasswordItem = new JMenuItem("修改密码");
        
        changePasswordItem.addActionListener(e -> {
            User currentUser = Main.getCurrentUser();
            if (currentUser != null) {
                new PswdUI(this, currentUser).setVisible(true);
            }
        });
        
        userMenu.add(changePasswordItem);
        menuBar.add(userMenu);
        setJMenuBar(menuBar);

        // 设置主面板
        setContentPane(mainPanel);
    }

    private void runTest() {
        String selectedTest = (String) testSelector.getSelectedItem();
        
        testThread = new Thread(() -> {
            try {
                switch (selectedTest) {
                    case "58同城招聘":
                        JobCrawlerTest.main(new String[]{});
                        System.out.println("执行完毕/已中断");
                        break;
                    case "武纺新闻":
                        WTUNewsCrawlerTest.main(new String[]{});
                        break;
                    case "selenium测试":
                        SeleniumTest.main(new String[]{});
                        break;
                }
            } catch (InterruptedException e) {
                System.out.println("测试执行被中断");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    runButton.setEnabled(true);
                    stopButton.setEnabled(false);
                });
            }
        });
        
        testThread.start();
    }

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        SwingUtilities.invokeLater(() -> {
            new OprationUI().setVisible(true);
        });
    }
} 