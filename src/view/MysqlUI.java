package view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import dao.SqlDAO;

public class MysqlUI extends JDialog {
    private JTextArea sqlArea;
    private JTextArea resultArea;

    public MysqlUI(JFrame parent) {
        super(parent, "执行SQL语句", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(true);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建标签面板
        JPanel labelPanel = new JPanel(new GridLayout(2, 1));
        labelPanel.add(new JLabel("job_info：(id, title, company, salary, location, experience, education, headcount, publish_date)"));
        labelPanel.add(new JLabel("请输入SQL语句："));
        panel.add(labelPanel, BorderLayout.NORTH);

        // SQL输入区域
        sqlArea = new JTextArea();
        sqlArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JScrollPane sqlScrollPane = new JScrollPane(sqlArea);

        // 结果显示区域
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);

        // 创建分割面板
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            sqlScrollPane,
            resultScrollPane
        );
        splitPane.setResizeWeight(0.25);
        splitPane.setDividerLocation(120);
        
        panel.add(splitPane, BorderLayout.CENTER);

        // 执行按钮
        JButton executeButton = new JButton("执行");
        executeButton.addActionListener(e -> executeSQL());
        panel.add(executeButton, BorderLayout.EAST);

        setContentPane(panel);
    }

    private void executeSQL() {
        try {
            String sql = sqlArea.getText();
            String result = SqlDAO.executeSQL(sql);
            resultArea.setText(result);
        } catch (SQLException ex) {
            resultArea.setText("SQL执行错误：\n" + ex.getMessage());
        }
    }
} 