package test;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import crawler.NewsThread;
import tools.CrawlerTools2;

public class WTUNewsCrawlerTest {

	/**
	 * 爬取武汉纺织大学新闻网中的所有综合新闻信息 获取新闻链接地址，新闻标题、通讯员，来源，点击数，发布时间，审稿，新闻内容，存入数据库
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		String url = "https://news.wtu.edu.cn/zhxw1.htm"; // 综合新闻首页
		
		int totalPage = getTotalPage(url);  //获取新闻总页数
		System.out.println("总页数："+totalPage);
	
		//爬取第1页
		Thread thread1 = new Thread(new NewsThread(url));
		thread1.start();

		for (int i = 1; i <= totalPage-1; i++) { // 第2页到最后1页
			String url2 = "https://news.wtu.edu.cn/zhxw1/" + i + ".htm";
			Thread thread2 = new Thread(new NewsThread(url2));
			thread2.start();
			thread2.sleep(1000);  //休眠1秒，以免访问频繁，IP被封
		}
		thread1.join();  //等待线程结束
		
		System.out.println("爬取成功！");
		

	}

	public static int getTotalPage(String urlPath) {
		int totalPage = 0;
		// 根据新闻页面url爬取
		Map<String, String> map = CrawlerTools2.getData(urlPath, "utf-8");
		if (map.get("success").equals("0")) { // 爬取出错
			System.out.println(map.get("msg")); // 输出出错信息
			return totalPage;
		}
		String content = map.get("msg");
		// 对爬取结果解析
		Document doc = Jsoup.parse(content);

		//选择器：span.p_t:nth-child(3)
		String page = doc.select("span.p_t:nth-child(3)").text(); // 格式：1/402
		page = page.substring(page.indexOf("/")+1);
		
		totalPage = Integer.parseInt(page);

		return totalPage;
	}

}
