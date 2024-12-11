package dao;

import tools.JDBCTools;
import vo.JobInfo;
import java.sql.*;
import java.util.List;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import jxl.Workbook;
import jxl.write.*;
import jxl.format.Alignment;
import jxl.format.Colour;
import java.util.ArrayList;
import java.io.File;

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
    
    public static void exportToXLS(List<JobInfo> jobInfoList, String fileName) {
        // 如果文件名为空，使用时间戳创建文件名
        if (fileName == null || fileName.trim().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            fileName = "job_info_" + sdf.format(new Date()) + ".xls";
        }
        
        // 确保文件名以.xls结尾
        if (!fileName.toLowerCase().endsWith(".xls")) {
            fileName += ".xls";
        }
        
        try {
            // 创建工作簿
            WritableWorkbook workbook = Workbook.createWorkbook(new File(fileName));
            // 创建工作表
            WritableSheet sheet = workbook.createSheet("职位信息", 0);
            
            // 创建标题样式
            WritableFont titleFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            WritableCellFormat titleFormat = new WritableCellFormat(titleFont);
            titleFormat.setAlignment(Alignment.CENTRE);
            titleFormat.setBackground(Colour.GRAY_25);
            
            // 写入表头
            String[] headers = {"职位名称", "公司名称", "月薪范围", "工作地点", "经验要求", 
                              "学历要求", "招聘人数", "发布日期"};
            for (int i = 0; i < headers.length; i++) {
                sheet.addCell(new Label(i, 0, headers[i], titleFormat));
                // 设置列宽
                sheet.setColumnView(i, 15);
            }
            
            // 创建内容样式
            WritableCellFormat contentFormat = new WritableCellFormat();
            contentFormat.setWrap(true); // 允许文本换行
            
            // 写入数据
            for (int row = 0; row < jobInfoList.size(); row++) {
                JobInfo job = jobInfoList.get(row);
                int col = 0;
                sheet.addCell(new Label(col++, row + 1, job.getTitle(), contentFormat));
                sheet.addCell(new Label(col++, row + 1, job.getCompany(), contentFormat));
                sheet.addCell(new Label(col++, row + 1, job.getSalary(), contentFormat));
                sheet.addCell(new Label(col++, row + 1, job.getLocation(), contentFormat));
                sheet.addCell(new Label(col++, row + 1, job.getExperience(), contentFormat));
                sheet.addCell(new Label(col++, row + 1, job.getEducation(), contentFormat));
                sheet.addCell(new Label(col++, row + 1, job.getHeadcount(), contentFormat));
                sheet.addCell(new Label(col++, row + 1, job.getPublishDate(), contentFormat));
            }
            
            // 写入文件并关闭
            workbook.write();
            workbook.close();
            
            System.out.println("数据已成功导出到：" + fileName);
            
        } catch (Exception e) {
            System.out.println("导出XLS文件失败：" + e.getMessage());
        }
    }
    
    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\"\"");
    }
    
    public static List<JobInfo> getAllJobs() {
        List<JobInfo> jobList = new ArrayList<>();
        String sql = "SELECT * FROM job_info";
        
        try (Connection conn = JDBCTools.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                JobInfo job = new JobInfo(
                    rs.getString("title"),
                    rs.getString("company"),
                    rs.getString("salary"),
                    rs.getString("location"),
                    rs.getString("experience"),
                    rs.getString("education"),
                    String.valueOf(rs.getInt("headcount")),
                    rs.getString("publish_date")
                );
                jobList.add(job);
            }
            
        } catch (SQLException e) {
            System.out.println("查询职位信息失败：" + e.getMessage());
        }
        
        return jobList;
    }
} 