package dao;

import tools.JDBCTools;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PswdDAO {
    public static boolean updatePassword(String userName, String oldPassword, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ? AND password = ?";
        
        try (Connection conn = JDBCTools.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setString(2, userName);
            pstmt.setString(3, oldPassword);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("修改密码失败：" + e.getMessage());
            return false;
        }
    }
} 