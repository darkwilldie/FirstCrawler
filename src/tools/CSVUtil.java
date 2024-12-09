package tools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import vo.JobInfo;

public class CSVUtil {
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
                // 处理字段中的逗号，用双引号包裹
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
    
    // 处理CSV特殊字符
    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        // 将双引号替换为两个双引号
        return value.replace("\"", "\"\"");
    }
} 