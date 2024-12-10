package view;

import javax.swing.*;

import dao.PswdDAO;

import java.awt.*;
import java.io.OutputStreamWriter;
import test.WTUNewsCrawlerTest;
import tools.JDBCTools;
import vo.User;
import test.Test;
import test.TongChengTest;
import java.sql.*;

public class TestUI extends JFrame {
    private JComboBox<String> testSelector;
    private JButton runButton;
    private JButton stopButton;
    private Thread testThread;

    public TestUI() {

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
                showCustomSqlDialog();
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
                new PswdDAO(this, currentUser).setVisible(true);
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
                        TongChengTest.main(new String[]{});
                        System.out.println("执行完毕/已中断");
                        break;
                    case "武纺新闻":
                        WTUNewsCrawlerTest.main(new String[]{});
                        break;
                    case "selenium测试":
                        Test.main(new String[]{});
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

    private void showCustomSqlDialog() {
        JFrame sqlFrame = new JFrame("执行SQL语句");
        sqlFrame.setSize(600, 400);
        sqlFrame.setLocationRelativeTo(this);
        sqlFrame.setResizable(true);
        sqlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建标签面板
        JPanel labelPanel = new JPanel(new GridLayout(2, 1));
        labelPanel.add(new JLabel("job_info：(id, title, company, salary, location, experience, education, headcount, publish_date"));
        labelPanel.add(new JLabel("请输入SQL语句："));
        panel.add(labelPanel, BorderLayout.NORTH);

        // SQL输入区域
        JTextArea sqlArea = new JTextArea();
        sqlArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JScrollPane sqlScrollPane = new JScrollPane(sqlArea);

        // 结果显示区域
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);

        // 创建分割面板
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            sqlScrollPane,
            resultScrollPane
        );
        splitPane.setResizeWeight(0.25); // 设置分割比例，0.3表示上面占30%
        splitPane.setDividerLocation(120); // 设置初始分割位置
        
        panel.add(splitPane, BorderLayout.CENTER);

        // 执行按钮
        JButton executeButton = new JButton("执行");
        executeButton.addActionListener(e -> {
            try {
                String sql = sqlArea.getText().trim();
                if (sql.isEmpty()) {
                    JOptionPane.showMessageDialog(sqlFrame, "SQL语句不能为空！");
                    return;
                }

                try (Connection conn = JDBCTools.getConnection();
                     Statement stmt = conn.createStatement()) {
                    
                    if (sql.toLowerCase().startsWith("select")) {
                        // 执行查询
                        ResultSet rs = stmt.executeQuery(sql);
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        
                        StringBuilder result = new StringBuilder();
                        // 添加列名
                        for (int i = 1; i <= columnCount; i++) {
                            result.append(metaData.getColumnName(i)).append("\t");
                        }
                        result.append("\n");
                        
                        // 添加数据
                        while (rs.next()) {
                            for (int i = 1; i <= columnCount; i++) {
                                result.append(rs.getString(i)).append("\t");
                            }
                            result.append("\n");
                        }
                        resultArea.setText(result.toString());
                    } else {
                        // 执行更新
                        int count = stmt.executeUpdate(sql);
                        resultArea.setText("执行成功，影响 " + count + " 行数据。");
                    }
                }
            } catch (SQLException ex) {
                resultArea.setText("SQL执行错误：\n" + ex.getMessage());
            }
        });
        panel.add(executeButton, BorderLayout.EAST);

        sqlFrame.add(panel);
        sqlFrame.setVisible(true);
    }

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        SwingUtilities.invokeLater(() -> {
            new TestUI().setVisible(true);
        });
    }
} 