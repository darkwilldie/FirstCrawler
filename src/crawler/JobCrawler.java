package crawler;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import tools.CSVTools;
import vo.JobInfo;

import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import dao.JobDAO;

public class JobCrawler implements Runnable{
    private WebDriver driver;
    private Random random = new Random();
    private List<JobInfo> jobInfoList = new ArrayList<>();
    private String url;
    private volatile boolean running = true;
    
    public JobCrawler(String url) {
        this.url = url;
        // 设置系统编码为UTF-8
        System.setProperty("file.encoding", "UTF-8");
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless");  // 启用无头模式
        options.addArguments("--window-size=1080,1080");  // 设置窗口大小
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-infobars");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        
        this.driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
    }
    
    private void randomSleep() {
        try {
            Thread.sleep(0 + random.nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private boolean handleCaptcha() {
        try {
            // 检查多种可能的验证码元素
            if(driver.findElements(By.className("pop")).size() > 0 || 
               driver.findElements(By.className("pop_verify")).size() > 0) {
                System.out.println("检测到验证码，等待手动处理...");
                Thread.sleep(30000);
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    
    public List<JobInfo> getJobInfoList() {
        return jobInfoList;
    }

    @Override
    public void run() {
        try {

            int pageNum = 1;
            String nextPageUrl = "";
            while (jobInfoList.size() < 100 && running) {
                String pageUrl = pageNum == 1 ? url : nextPageUrl;
                driver.get(pageUrl);
                System.out.println("正在访问：" + pageUrl);
                randomSleep();

                if (handleCaptcha()) {
                    System.out.println("验证码处理完成，继续爬取");
                }

                // 等待任意一个职位元素出现
                try {
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".job_item")));
                } catch (TimeoutException e) {
                    System.out.println("页面加载超时，尝试继续解析...");
                }

                // 尝试多个可能的选择器
                List<WebElement> jobList = new ArrayList<>();
                try {
                    jobList = driver.findElements(By.cssSelector(".job_item"));
                } catch (Exception e) {
                    System.out.println("获取职位列表失败");
                    continue;
                }
                // // 取jobList的前3个
                // jobList = jobList.subList(0, Math.min(3, jobList.size()));

                System.out.println("当前页面找到 " + jobList.size() + " 个职位");

                for (WebElement job : jobList) {
                    if (!running)
                        break;
                    try {
                        // 使用更灵活的方式获取元素
                        String title = getTextSafely(job, ".job_name .name");
                        String company = getTextSafely(job, ".comp_name");
                        String salary = getTextSafely(job, ".job_salary");
                        String location = getTextSafely(job, ".address");
                        String experience = getTextSafely(job, ".jingyan");
                        String education = getTextSafely(job, ".xueli");
                        String headcount = getTextSafely(job, ".job_people, .headcount");
                        String publishDate = getTextSafely(job, ".job_time, .publish_time");
                        if (experience.equals("未知")) {
                            handleCaptcha();
                            continue;
                        }
                        try {
                            WebElement linkElement = job.findElement(By.cssSelector(".job_name > a"));
                            String jobUrl = linkElement.getAttribute("href");
                            if (jobUrl != null && !jobUrl.isEmpty()) {
                                String currentWindow = driver.getWindowHandle();
                                ((JavascriptExecutor) driver).executeScript("window.open(arguments[0])", jobUrl);
                                for (String windowHandle : driver.getWindowHandles()) {
                                    if (!windowHandle.equals(currentWindow)) {
                                        driver.switchTo().window(windowHandle);
                                        break;
                                    }
                                    if (handleCaptcha()) {
                                        System.out.println("验证码处理完成，继续爬取");
                                    }
                                }
                                try {
                                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                                    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".con")));
                                    title = getTextSafely(driver.findElement(By.cssSelector(".con")), ".pos_title");
                                    company = getTextSafely(driver.findElement(By.cssSelector(".con")),
                                            ".baseInfo_link");
                                    location = getTextSafely(driver.findElement(By.cssSelector(".con")),
                                            ".pos_area_span.pos_address");
                                    headcount = getTextSafely(driver.findElement(By.cssSelector(".con")),
                                            ".item_condition.pad_left_none");
                                    headcount = headcount.replaceAll("[^0-9]", "");
                                    publishDate = getTextSafely(driver.findElement(By.cssSelector(".con")),
                                            ".pos_base_num.pos_base_update");
                                    // 关闭详情页
                                    driver.close();
                                    // 回到主页面
                                    driver.switchTo().window(currentWindow);
                                } catch (Exception e) {
                                    System.out.println("获取详情页信息失败");
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("获取职位链接失败");
                        }

                        JobInfo jobInfo = new JobInfo(title, company, salary, location,
                                experience, education, headcount, publishDate);

                        jobInfoList.add(jobInfo);
                        System.out.println("已爬取第 " + jobInfoList.size() + " 个职位信息：");
                        System.out.println(jobInfo);
                        if (jobInfoList.size() >= 100) {
                            System.out.println("已爬取100个职位信息，停止爬取");
                            break;
                        }

                        randomSleep();

                    } catch (Exception e) {
                        System.out.println("解析职位信息失败：" + e.getMessage());
                        continue;
                    }
                }

                if (jobList.isEmpty()) {
                    System.out.println("当前页面没有找到职位信息，可能需要处理验证码");
                    handleCaptcha();
                    continue;
                }

                if (running) {
                    pageNum++;
                    System.out.println("正在准备爬取第 " + pageNum + " 页...");

                    // 检查是否有下一页
                    WebElement nextPageElement = driver.findElement(By.cssSelector(".pagesout a.next"));
                    if (nextPageElement == null) {
                        System.out.println("已到达最后一页");
                        break;
                    }
                    nextPageUrl = nextPageElement.getAttribute("href");
                }
                else
                    return;
            }
            System.out.println("总共爬取到 " + jobInfoList.size() + " 个职位信息");
            if (jobInfoList.size() > 0) {
                // 保存到csv文件
                CSVTools.exportToCSV(jobInfoList, "job_info.csv");
                // 批量保存到数据库
                JobDAO.batchSave(jobInfoList);
            }
        } catch (Exception e) {

        } finally {
            driver.quit();
        }
    }
    
    private String getTextSafely(WebElement parent, String selectors) {
        try {
            for(String selector : selectors.split(", ")) {
                List<WebElement> elements = parent.findElements(By.cssSelector(selector));
                if(!elements.isEmpty()) {
                    return elements.get(0).getText().trim();
                }
            }
        } catch (Exception e) {
            // 忽略
        }
        return "未知";
    }

    public void stop() {

        System.out.println("线程已停止，正在退出……");
        running = false;
        if (driver != null) {
            driver.quit();
        }
    }

} 