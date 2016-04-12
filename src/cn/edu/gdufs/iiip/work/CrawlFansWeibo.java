package cn.edu.gdufs.iiip.work;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

import cn.edu.gdufs.iiip.crawler.*;
import cn.edu.gdufs.iiip.login.Cookie;
import cn.edu.gdufs.iiip.util.*; 
import cn.edu.gdufs.iiip.work.*; 

//哲影
//爬取广外用户的微博，按天分
//用于高校热点检测
@SuppressWarnings("unused")
public class CrawlFansWeibo {
	private static String uidPath = "F:/weiboid/项目.txt";
	private static String path = "F:/weibo/项目";//文件保存的路径
	public static String crawledPath = path + "crawled.txt";//已爬取微博用户记录文件
	public static void main(String[] args) {
//		Cookie.login("niketim@163.com","321456");
		Cookie.login("gwcrawler20@163.com", "321654");
		crawlWeibo();
//		crawlWeibo(2015,8,11);
	}
	
	//爬取用户所有微博
	private static void crawlWeibo() {
		(new File(path)).mkdirs();
		List<String> uids = IO.readList(uidPath);
		File file = new File(crawledPath);
		List<String> crawled;
		if (file.exists()) crawled = IO.readList(crawledPath);
		else crawled = new ArrayList<>();
		ExecutorService pool = Executors.newFixedThreadPool(1);
		for (String uid: uids) {
			if (!crawled.contains(uid)) {
				CrawlWeiboThread cw = new CrawlWeiboThread(uid);
				pool.execute(cw);
			}
		}
		pool.shutdown();
	}
	
	//爬取用户某一天开始的微博
	private static void crawlWeibo(int year, int month, int day) {
		(new File(path)).mkdirs();
		List<String> uids = IO.readList(uidPath);
		File file = new File(crawledPath);
		List<String> crawled;
		if (file.exists()) crawled = IO.readList(crawledPath);
		else crawled = new ArrayList<>();
		Date updateTime = Time.getDate(year, month, day);
		ExecutorService pool = Executors.newFixedThreadPool(10);
		for (String uid: uids) {
			if (!crawled.contains(uid)) {
//				CrawlWeiboThread cw = new CrawlWeiboThread(uid, updateTime);
				CrawlWeiboThread cw = new CrawlWeiboThread(uid);

				pool.execute(cw);
			}
		}
		pool.shutdown();
	}
}
