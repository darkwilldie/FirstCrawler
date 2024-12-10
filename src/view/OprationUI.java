package view;

import javax.swing.*;
import java.awt.*;
import test.WTUNewsCrawlerTest;
import vo.User;
import test.SeleniumTest;
import test.JobCrawlerTest;

public class OprationUI extends JFrame {
    private JComboBox<String> testSelector;
    private JButton runButton;
    private JButton stopButton;
    private Thread testThread;

    public OprationUI() {

        setTitle("爬虫测试界面");
        setSize(400, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // 居中显示
        initComponents();
    }

    private void initComponents() {
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 创建顶部控制面板
        JPanel controlPanel = new JPanel();
        controlPanel.putClientProperty("charset", "UTF-8");
        String[] tests = {"58同城招聘", "武纺新闻", "selenium测试"};
        testSelector = new JComboBox<>(tests);
        runButton = new JButton("运行测试");
        stopButton = new JButton("终止测试");
        JButton sqlButton = new JButton("执行SQL");
        
        stopButton.setEnabled(false);
        
        controlPanel.add(testSelector);
        controlPanel.add(runButton);
        controlPanel.add(stopButton);
        controlPanel.add(sqlButton);
        
        // 添加SQL按钮的事件监听
        sqlButton.addActionListener(e -> {
            // 检查用户权限
            User currentUser = Main.getCurrentUser();
            if (currentUser == null || "admin".equals(currentUser.getRole())) {
                new MysqlUI(this).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "只有管理员可以执行此操作！", 
                    "权限错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

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