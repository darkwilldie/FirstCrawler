package crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tools.CrawlerTools2;
import vo.News;

public class NewsThread implements Runnable {
	private String urlPath;

	public NewsThread(String urlPath) {
		super();
		this.urlPath = urlPath;
	}

	@Override
	public void run() {
		// 根据新闻页面url爬取
		Map<String, String> map = CrawlerTools2.getData(urlPath, "utf-8");
		if (map.get("success").equals("0")) { // 爬取出错
			System.out.println(map.get("msg"));  //输出出错信息
			return;
		}
		String content = map.get("msg");

		// 对爬取结果解析
		Document doc = Jsoup.parse(content);
		Elements elements = doc.select(".list .list ul li"); // 所有新闻

		int index = 0;
		for (Element element : elements) {
			index++;
			if (index % 2 != 0) {
				Element el = element.selectFirst("a");
				String href = el.attr("href"); // 解析详情页的超链接地址：该地址是一个相对地址
				href = getAbsoluteUrl(urlPath, href); // 将超链接相对地址转换为绝对地址
				if (href.equals("")) { // 站外链接地址，忽略处理
					continue;
				}
				// 爬取详情页面
				Map<String, String> mapSecond = CrawlerTools2.getData(href, "utf-8");
				if (mapSecond.get("success").equals("0")) { // 出错
					System.out.println(mapSecond.get("msg"));
					continue;
				}

				String id = href.substring(href.lastIndexOf("/") + 1, href.lastIndexOf(".")); // 新闻详情页面文件名作为新闻的id
				String secondContent = mapSecond.get("msg"); // 新闻详情页内容
				// 解析详情页面
				Document secondDoc = Jsoup.parse(secondContent);
				String article_title = secondDoc.selectFirst(".article_title h1").text(); // 新闻标题
				String article_data = secondDoc.selectFirst(".article_data").html();
				// 对article_data进一步解析，解析新闻相关字段信息
				String arr[] = article_data.split("<b></b>");
				String tongxunyuan = arr[0].substring(arr[0].indexOf("：") + 1); // 通讯员
				String laiyuan = arr[1].substring(arr[1].indexOf("：") + 1); // 来源
				String click = getClick(arr[2]); // 获取点击数
				String fabushijian = arr[3].substring(arr[3].indexOf("：") + 1); // 发布时间
				String shengao = arr[4].substring(arr[4].indexOf("：") + 1); // 审稿
				// String newsContent = secondDoc.select("#vsb_content_2").text();  //新闻内容
				String editor = secondDoc.selectFirst(".photogr").text(); // 编辑
				editor = editor.substring(editor.indexOf("：")+1);
				
				News news = new News(id, article_title, tongxunyuan, laiyuan, Integer.parseInt(click), 
						fabushijian, shengao, editor);
				
				System.out.println(news.toString());

				// 保存至数据库或文件
			}
		}
	}

	/**
	 * 获取url绝对地址
	 * 
	 * @param currentUrlPath:当前页面url
	 * @param targetUrl：链接页面url
	 * @return 返回链接页面的绝对url 为""表示是站外链接，不处理
	 */
	public static String getAbsoluteUrl(String currentUrlPath, String targetUrl) {
		String absoluteUrl = "";
		try {
			URL url = new URL(currentUrlPath);
			String basePath = url.getProtocol() + "://" + url.getHost();
			if (url.getPort() != -1) {
				basePath = basePath + ":" + url.getPort() + "/"; // 当前页面根地址
			}

			if (targetUrl.startsWith("http") || targetUrl.startsWith("https")) {
				URL url2 = new URL(targetUrl);
				String basePath2 = url2.getProtocol() + "://" + url2.getHost();
				if (url2.getPort() != -1) {
					basePath2 = basePath2 + ":" + url2.getPort() + "/"; // 根目录地址
				}
				if (!basePath.equals(basePath2)) { // 根目录不一样，表示是站外链接，不处理
					return "";
				}
			}
			String currentPath = currentUrlPath.substring(0, currentUrlPath.lastIndexOf("/") + 1); // 当前目录

			if (targetUrl.startsWith("/")) {
				absoluteUrl = basePath + targetUrl;
			} else if (targetUrl.startsWith("http") || targetUrl.startsWith("https")) {
				absoluteUrl = targetUrl;
			} else {
				absoluteUrl = currentPath + targetUrl;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return absoluteUrl;
	}

	/// 新闻点击是通过以下url请求获取的
	// https://news.wtu.edu.cn/system/resource/code/news/click/dynclicks.jsp?clickid=31901&owner=1423556954&clicktype=wbnews
	// 需要先从js字符串_showDynClicks("wbnews", 1423556954, 31901)中获取owner和clickid
	public static String getClick(String str) {
		String clickNum = "";
		String regex = "(\\d+),(\\d+)";
		str = str.replaceAll("\\s+", ""); // 去掉空格
		Pattern pat = Pattern.compile(regex);
		Matcher mat = pat.matcher(str);
		String clickid = "";
		String owner = "";
		if (mat.find()) {
			owner = mat.group(1);
			clickid = mat.group(2);
			// 新闻点击是通过以下url请求获取的
			String url = "https://news.wtu.edu.cn/system/resource/code/news/click/dynclicks.jsp?clickid=" + clickid
					+ "&owner=" + owner + "&clicktype=wbnews";
			Map<String, String> map = CrawlerTools2.getData(url, "utf-8");
			if (map.get("success").equals("0")) { // 出错
				System.out.println(map.get("msg"));
			} else {
				clickNum = map.get("msg");
			}

		}
		return clickNum;
	}
}
