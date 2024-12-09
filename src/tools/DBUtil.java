package tools;

import java.sql.*;
import java.util.List;

import vo.JobInfo;

public class DBUtil {
    // 数据库连接信息
    private static final String URL = "jdbc:mysql://localhost:3306/58tongcheng?useUnicode=true&characterEncoding=utf8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";
    
    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL驱动加载失败", e);
        }
    }
    
    // 保存职位信息
    public static void saveJobInfo(JobInfo jobInfo) {
        String sql = "INSERT INTO job_info (title, company, salary, location, experience, " +
                    "education, headcount, publish_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, jobInfo.getTitle());
            pstmt.setString(2, jobInfo.getCompany());
            pstmt.setString(3, jobInfo.getSalary());
            pstmt.setString(4, jobInfo.getLocation());
            pstmt.setString(5, jobInfo.getExperience());
            pstmt.setString(6, jobInfo.getEducation());
            pstmt.setString(7, jobInfo.getHeadcount());
            pstmt.setString(8, jobInfo.getPublishDate());
            
            pstmt.executeUpdate();
            System.out.println("成功保存职位信息：" + jobInfo.getTitle());
            
        } catch (SQLException e) {
            System.out.println("保存职位信息失败：" + e.getMessage());
        }
    }
    
    // 批量保存职位信息
    public static void batchSaveJobInfo(List<JobInfo> jobInfoList) {
        String sql = "INSERT INTO job_info (title, company, salary, location, experience, " +
                    "education, headcount, publish_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false); // 开启事务
            
            for (JobInfo jobInfo : jobInfoList) {
                pstmt.setString(1, jobInfo.getTitle());
                pstmt.setString(2, jobInfo.getCompany());
                pstmt.setString(3, jobInfo.getSalary());
                pstmt.setString(4, jobInfo.getLocation());
                pstmt.setString(5, jobInfo.getExperience());
                pstmt.setString(6, jobInfo.getEducation());
                pstmt.setString(7, jobInfo.getHeadcount());
                pstmt.setString(8, jobInfo.getPublishDate());
                
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
            conn.commit(); // 提交事务
            
            System.out.println("成功批量保存 " + jobInfoList.size() + " 条职位信息");
            
        } catch (SQLException e) {
            System.out.println("批量保存职位信息失败：" + e.getMessage());
        }
    }
    
    // 关闭数据库资源
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 