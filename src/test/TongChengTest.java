package test;

import crawler.TongChengCrawler;
import tools.CSVUtil;
import tools.DBUtil;
import vo.JobInfo;

import java.util.List;

public class TongChengTest {
    public static void main(String[] args) {
        TongChengCrawler crawler = new TongChengCrawler();
        crawler.start("https://wh.58.com/quanzhizhaopin/");
        
        List<JobInfo> jobInfoList = crawler.getJobInfoList();
        System.out.println("总共爬取到 " + jobInfoList.size() + " 个职位信息");
        
        // 保存到csv文件
        CSVUtil.exportToCSV(jobInfoList, "job_info.csv");
        // 批量保存到数据库
        DBUtil.batchSaveJobInfo(jobInfoList);
    }
} 