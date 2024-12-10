package view;

import javax.swing.*;
import java.awt.*;
import vo.User;
import tools.JDBCTools;

public class LoginUI extends JFrame {
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginUI() {
        setTitle("用户登录");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // 居中显示
        
        // 创建面板
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 添加组件
        panel.add(new JLabel("用户名:"));
        userNameField = new JTextField();
        panel.add(userNameField);
        
        panel.add(new JLabel("密码:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);
        
        loginButton = new JButton("登录");
        registerButton = new JButton("注册");
        panel.add(loginButton);
        panel.add(registerButton);
        
        // 添加事件监听
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> register());
        
        add(panel);
    }
    
    private void login() {
        String userName = userNameField.getText();
        String password = new String(passwordField.getPassword());
        
        // 基本输入验证
        if (userName.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "用户名和密码不能为空！",
                "错误",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 验证用户
        User user = JDBCTools.validateUser(userName, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this,
                "登录成功！\n欢迎回来，" + user.getName(),
                "登录信息",
                JOptionPane.INFORMATION_MESSAGE);
            
            onLoginSuccess(user);  // 调用登录成功的回调方法
        } else {
            JOptionPane.showMessageDialog(this,
                "用户名或密码错误！",
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void register() {
        // 创建注册对话框
        JDialog dialog = new JDialog(this, "用户注册", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField userNameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JTextField nameField = new JTextField();
        
        panel.add(new JLabel("用户名:"));
        panel.add(userNameField);
        panel.add(new JLabel("密码:"));
        panel.add(passwordField);
        panel.add(new JLabel("确认密码:"));
        panel.add(confirmPasswordField);
        panel.add(new JLabel("姓名:"));
        panel.add(nameField);
        
        JButton confirmButton = new JButton("确认注册");
        confirmButton.addActionListener(e -> {
            String userName = userNameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
            String name = nameField.getText().trim();
            
            // 输入验证
            if (userName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "所有字段都不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "两次输入的密码不一致！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 执行注册
            if (JDBCTools.registerUser(userName, password, name)) {
                JOptionPane.showMessageDialog(dialog, "注册成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "注册失败，用户名可能已存在！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);
        
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    // 登录成功的回调方法，可以被子类重写
    protected void onLoginSuccess(User user) {
        // 默认实现为空
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginUI().setVisible(true);
        });
    }
}