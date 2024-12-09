package test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

//https://www.cnblogs.com/tester-ggf/p/12602211.html
public class Test {

	public static void main(String[] args) {
//		WebDriver driver = new ChromeDriver();    //Chrome浏览器
//		WebDriver driver = new FirefoxDriver();   //Firefox浏览器
//		WebDriver driver = new InternetExplorerDriver();  // Internet Explorer浏览器
//		WebDriver driver = new OperaDriver();     //Opera浏览器
//		WebDriver driver = new PhantomJSDriver();   //PhantomJS

		WebDriver driver = new EdgeDriver();      //Edge浏览器

		// 2.打开百度首页
        driver.get("https://www.baidu.com");
        // 3.获取输入框，输入selenium
        driver.findElement(By.id("kw")).sendKeys("selenium");
        // 4.获取“百度一下”按钮，进行搜索
        driver.findElement(By.id("su")).click();
        // 5.退出浏览器
       // driver.quit();


	}

}
