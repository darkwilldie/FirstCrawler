package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlerTools2 {
	/**
	 * 读取指定url的网页html字符串，
	 * urlStr：待爬取网页的url地址
	 * charset:待爬取网页的字符编码，可以在网页的<meta charset="******" />处获取目标网页的编码
	 * 返回：HashMap
	 * success：0失败，1成功
	 * msg：错误描述或爬取的字符串
	 */
	public static HashMap<String, String> getData(String urlStr, String charset) {
		HashMap<String, String> map = new HashMap<String, String>();

		StringBuffer buf = new StringBuffer();
		HttpURLConnection con = null;
		InputStream in = null;
		BufferedReader read = null;
		try {
			urlStr = encodingUrl(urlStr);
			URL url = new URL(urlStr);
			con = (HttpURLConnection) url.openConnection();
			// 模拟浏览器发出请求，防止反爬，此值可以从浏览器的开发人员工具network标签任意请求头部复制过来
			con.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.64 Safari/537.36 Edg/101.0.1210.53");
			int code = con.getResponseCode();
			if (code == 200) {
				in = con.getInputStream();  //获取输入字节流
				read = new BufferedReader(new InputStreamReader(in, charset)); //利用转换流将字节流转为字符流，需要指定转换时的字符编码
				String info = "";
				while ((info = read.readLine()) != null) { //循环读取每一行，没有数据时值为null
					buf.append(info);
				}
				map.put("success", "1");
				map.put("msg", buf.toString());
			} else {
				map.put("success", "0");
				map.put("msg", "服务器返回出错码：" + code);
			}
		} catch (Exception e) {
			map.put("success", "0");
			map.put("msg", "异常：" + e.getMessage());
		} finally {  //无论是否异常，都需要执行的代码
			if (read != null) {
				try {
					read.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				con.disconnect();
			}
		}

		return map;
	}

	// 将url字符串里面的中文进行编码
	public static String encodingUrl(String url) {
		String regex = "[\u4e00-\u9fa5]+";
		Pattern pat = Pattern.compile(regex);
		Matcher mat = pat.matcher(url);
		while (mat.find()) {
			String hanzi = mat.group();
			System.out.println(hanzi);
			String encodehanzi = "";
			try {
				encodehanzi = URLEncoder.encode(hanzi, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			url = url.replaceAll(hanzi, encodehanzi);
		}
		return url;
	}

}
