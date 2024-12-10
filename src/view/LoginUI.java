package view;

import javax.swing.*;
import java.awt.*;
import vo.User;
import tools.DBUtil;

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
        User user = DBUtil.validateUser(userName, password);
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
        // TODO: 实现注册逻辑
        JOptionPane.showMessageDialog(this, 
            "注册功能待实现", 
            "提示", 
            JOptionPane.INFORMATION_MESSAGE);
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