package dao;

import tools.JDBCTools;
import vo.JobInfo;
import java.sql.*;
import java.util.List;

public class JobDAO {
    
    public static void save(JobInfo jobInfo) {
        String sql = "INSERT INTO job_info (title, company, salary, location, experience, " +
                    "education, headcount, publish_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    
        try (Connection conn = JDBCTools.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, jobInfo.getTitle());
            pstmt.setString(2, jobInfo.getCompany());
            pstmt.setString(3, jobInfo.getSalary());
            pstmt.setString(4, jobInfo.getLocation());
            pstmt.setString(5, jobInfo.getExperience());
            pstmt.setString(6, jobInfo.getEducation());
            pstmt.setInt(7, Integer.parseInt(jobInfo.getHeadcount()));
            pstmt.setString(8, jobInfo.getPublishDate());
            
            pstmt.executeUpdate();
            System.out.println("成功保存职位信息：" + jobInfo.getTitle());
            
        } catch (SQLException e) {
            System.out.println("保存职位信息失败：" + e.getMessage());
        }
    }
    
    public static void batchSave(List<JobInfo> jobInfoList) {
        String sql = "INSERT INTO job_info (title, company, salary, location, experience, " +
                    "education, headcount, publish_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    
        try (Connection conn = JDBCTools.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (JobInfo jobInfo : jobInfoList) {
                pstmt.setString(1, jobInfo.getTitle());
                pstmt.setString(2, jobInfo.getCompany());
                pstmt.setString(3, jobInfo.getSalary());
                pstmt.setString(4, jobInfo.getLocation());
                pstmt.setString(5, jobInfo.getExperience());
                pstmt.setString(6, jobInfo.getEducation());
                pstmt.setInt(7, Integer.parseInt(jobInfo.getHeadcount()));
                pstmt.setString(8, jobInfo.getPublishDate());
                
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
            conn.commit();
            
            System.out.println("成功批量保存 " + jobInfoList.size() + " 条职位信息");
            
        } catch (SQLException e) {
            System.out.println("批量保存职位信息失败：" + e.getMessage());
        }
    }
} 