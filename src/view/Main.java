package view;

import javax.swing.SwingUtilities;
import vo.User;

public class Main {
    private static User currentUser = null;  // 保存当前登录用户
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 首先显示登录界面
                LoginUI loginUI = new LoginUI() {
                    @Override
                    protected void onLoginSuccess(User user) {
                        currentUser = user; // 保存登录用户
                        this.dispose(); // 关闭登录窗口

                        // 登录成功后打开爬虫测试界面
                        TestUI testUI = new TestUI();
                        testUI.setVisible(true);
                    }
                };
                loginUI.setVisible(true);
            }
        });
    }

    // 获取当前登录用户
    public static User getCurrentUser() {
        return currentUser;
    }
}
