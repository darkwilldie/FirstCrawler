package dao;

import tools.JDBCTools;
import vo.JobInfo;
import java.sql.*;
import java.util.List;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    
    public static void exportToCSV(List<JobInfo> jobInfoList, String fileName) {
        // 如果文件名为空，使用时间戳创建文件名
        if (fileName == null || fileName.trim().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            fileName = "job_info_" + sdf.format(new Date()) + ".csv";
        }
        
        // 确保文件名以.csv结尾
        if (!fileName.toLowerCase().endsWith(".csv")) {
            fileName += ".csv";
        }
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            // 写入BOM，解决Excel打开中文乱码问题
            writer.write('\ufeff');
            
            // 写入CSV头部
            writer.write("职位名称,公司名称,月薪范围,工作地点,经验要求,学历要求,招聘人数,发布日期");
            writer.newLine();
            
            // 写入数据
            for (JobInfo job : jobInfoList) {
                writer.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                    escapeCSV(job.getTitle()),
                    escapeCSV(job.getCompany()),
                    escapeCSV(job.getSalary()),
                    escapeCSV(job.getLocation()),
                    escapeCSV(job.getExperience()),
                    escapeCSV(job.getEducation()),
                    escapeCSV(job.getHeadcount()),
                    escapeCSV(job.getPublishDate())
                ));
                writer.newLine();
            }
            
            System.out.println("数据已成功导出到：" + fileName);
            
        } catch (IOException e) {
            System.out.println("导出CSV文件失败：" + e.getMessage());
        }
    }
    
    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\"\"");
    }
} 