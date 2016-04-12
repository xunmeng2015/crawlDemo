package cn.edu.gdufs.iiip.crawler;

import java.util.*;
import java.util.concurrent.*;

import cn.edu.gdufs.iiip.entity.*;
import cn.edu.gdufs.iiip.login.*;
import cn.edu.gdufs.iiip.util.*;

/*样例：部分类的使用，方法的调用*/
@SuppressWarnings("unused")
public class Sample {
	public static void main(String[] args) {
		//批量登陆生成cookie，并保存到文件中
		//使用cookie时从文件读取，cookie有效时长24小时
//		Cookie.login();
		//测试cookie的可用性
//		Cookie.testCookie();
		
		//测试模拟登陆
//		Crawl c = new Crawl();
//		String html =  c.crawl("http://weibo.com/cnmsdn");
//		IO.print(html, "D:/test.html");
		
		
		
		
		//爬取用户微博
//		CrawlWeibo cw = new CrawlWeibo("2008337901");//参数为uid
//		List<Weibo> weibos = cw.getWeibo();
//		weibos = cw.getWeibo(new Date());
//		weibos = cw.getWeibo(1000);
		
		//多线程示例
//		class MyThread implements Runnable {//实现多线程类然
//			String uid;
//			public String getUid() {return uid;}
//			public void setUid(String uid) {this.uid = uid;}
//			public MyThread(String uid) {
//				this.uid = uid;
//			}
//			@Override
//			public void run() {
//				CrawlWeibo cw = new CrawlWeibo(this.uid);
//				List<Weibo> weibos = cw.getWeibo();
				//TODO
//			}
//		}
//		ExecutorService pool = Executors.newFixedThreadPool(10);
//		MyThread mt = new MyThread("2008337901");
//		pool.execute(mt);
//		pool.shutdown();
		
		//爬取用户某一关键词的微博
		//参数为uid，关键词
//		CrawlTopicWeibo ctw = new CrawlTopicWeibo("2008337901", "李天一");
//		List<Weibo> weibos = ctw.getWeibo();
		
		//爬取搜索微博
//		CrawlSearch cs = new CrawlSearch("李天一");//参数为关键词
//		List<Weibo> weibos = cs.getSearch();
		
		
		
		
		//爬取微博的评论
//		CrawlComment cc = new CrawlComment("3651450041019442");//参数为mid
//		List<Comment> comments = cc.getComment();
		
		//爬取微博的转发
//		CrawlRepost cr = new CrawlRepost("3651450041019442");//参数为mid
//		List<Repost> reposts = cr.getRepost();
		
		
		
		
		//爬取用户的信息
//		CrawlUser cu = new CrawlUser("2008337901");//参数为uid
//		User user = cu.getUser();
		
		//爬取用户的关注
//		CrawlFollow cf = new CrawlFollow("2008337901");//参数为uid
//		List<User> follows = cf.getFollow();
		
		//爬取用户的关注
//		CrawlFan cfa = new CrawlFan("2008337901");//参数为uid
//		List<User> fans = cfa.getFan();
	}
}
