package view;

import javax.swing.*;
import java.awt.*;
import tools.DBUtil;
import vo.User;

public class ChangePasswordDialog extends JDialog {
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton confirmButton;
    private JButton cancelButton;
    private User currentUser;

    public ChangePasswordDialog(JFrame parent, User user) {
        super(parent, "修改密码", true);
        this.currentUser = user;
        
        setSize(300, 200);
        setLocationRelativeTo(parent);
        
        // 创建面板
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 添加组件
        panel.add(new JLabel("原密码:"));
        oldPasswordField = new JPasswordField();
        panel.add(oldPasswordField);
        
        panel.add(new JLabel("新密码:"));
        newPasswordField = new JPasswordField();
        panel.add(newPasswordField);
        
        panel.add(new JLabel("确认新密码:"));
        confirmPasswordField = new JPasswordField();
        panel.add(confirmPasswordField);
        
        confirmButton = new JButton("确认");
        cancelButton = new JButton("取消");
        panel.add(confirmButton);
        panel.add(cancelButton);
        
        // 添加事件监听
        confirmButton.addActionListener(e -> changePassword());
        cancelButton.addActionListener(e -> dispose());
        
        add(panel);
    }
    
    private void changePassword() {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // 验证输入
        if (oldPassword.trim().isEmpty() || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有字段都不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "新密码两次输入不一致！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 更新密码
        if (DBUtil.updatePassword(currentUser.getUserName(), oldPassword, newPassword)) {
            JOptionPane.showMessageDialog(this, "密码修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "原密码错误或修改失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
} 