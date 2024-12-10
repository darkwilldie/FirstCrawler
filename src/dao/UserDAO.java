package dao;

import tools.JDBCTools;
import vo.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface UserDAO {
    // 添加用户验证方法
    public static User validateUser(String userName, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = JDBCTools.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("用户验证失败：" + e.getMessage());
        }
        return null;
    }
    
    public static boolean registerUser(String userName, String password, String name) {
        // 首先检查用户名是否已存在
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, password, name, role) VALUES (?, ?, ?, 'user')";
        
        try (Connection conn = JDBCTools.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setString(1, userName);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // 用户名已存在
                }
            }
            
            // 用户名不存在，执行注册
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, userName);
                insertStmt.setString(2, password);
                insertStmt.setString(3, name);
                
                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("注册用户失败：" + e.getMessage());
            return false;
        }
    }
} 