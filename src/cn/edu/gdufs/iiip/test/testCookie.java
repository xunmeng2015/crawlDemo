package cn.edu.gdufs.iiip.test;

import org.apache.http.client.CookieStore;

import cn.edu.gdufs.iiip.crawler.Crawl;
import cn.edu.gdufs.iiip.util.IO;

public class testCookie {

	public static void main(String[] args) {
		String url = "http://weibo.com/2863259912/AhMRtlt3M";
		Crawl c = new Crawl();
		for(int i=1;i<=23;i++){
			CookieStore cookie=IO.readCookieStore("./cookie/gwcrawler"+i+"@163.com");
			String html=c.crawl(url,cookie);
			System.out.println(html);
			if(html.contains("bonjour")){
				System.out.println("succeed");
			}else{
				System.out.println("fail");
			}
		}
		
	}

}
