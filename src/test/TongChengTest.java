package test;

import crawler.TongChengCrawler;
import tools.CSVUtil;
import tools.JDBCTools;
import vo.JobInfo;

import java.util.List;

public class TongChengTest {
    public static void main(String[] args) {
		System.setProperty("webdriver.chrome.silentOutput", "true");
		java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.OFF);
		
        TongChengCrawler crawler = new TongChengCrawler("https://wh.58.com/quanzhizhaopin/");
        Thread thread1 = new Thread(crawler);
        thread1.start();
        try {
            thread1.join();
        } catch (InterruptedException interruption) {
            try {
                if (crawler != null) {
                    crawler.stop();
                }
            } catch (Exception e) {
            }
        }
    }
} 