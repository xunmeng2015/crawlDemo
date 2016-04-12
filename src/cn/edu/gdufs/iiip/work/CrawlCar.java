package cn.edu.gdufs.iiip.work;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import cn.edu.gdufs.iiip.crawler.*;
import cn.edu.gdufs.iiip.entity.*;
import cn.edu.gdufs.iiip.login.Cookie;
import cn.edu.gdufs.iiip.util.*;

//文敏
//爬取一批汽车用户的所有微博，以及每条微博的所有关注转发内容及用户来源地
//本类只针对一条微博爬取信息
@SuppressWarnings("unused")
public class CrawlCar {
	private static String uidPath = "uid/凤凰财经粉丝uid.txt";
	
	public static void main(String[] args) {
		Cookie.login();
		crawlWeibo();
//		crawlCommentRepost();
//		crawlUser();
	}
	
	//获取用户的所有微博
	private static void crawlWeibo() {
		List<String> uids = IO.readList(uidPath);
		ExecutorService pool = Executors.newFixedThreadPool(10);
		for (String uid: uids) {
			CrawlWeiboThread cw = new CrawlWeiboThread(uid);
			pool.execute(cw);
		}
		pool.shutdown();
	}
	
	//获取微博的所有评论转发
	private static void crawlCommentRepost() {
		File dir = new File("E:/weibo");
		File[] files = dir.listFiles();
		ExecutorService pool = Executors.newFixedThreadPool(10);
		for (File file: files) {
			List<Weibo> weibos = IO.readWeibo(file.getAbsolutePath());
			for (Weibo weibo: weibos) {
				CrawlCommentThread cc = new CrawlCommentThread(weibo.getMid());
				pool.execute(cc);
				CrawlRepostThread cr = new CrawlRepostThread(weibo.getMid());
				pool.execute(cr);
			}
		}
		pool.shutdown();
	}
	
	//获取评论转发者的个人资料
	private static void crawlUser() {
		ExecutorService pool = Executors.newFixedThreadPool(10);
		List<String> crawled;
		File file = new File("E:/user/crawled.txt");
		if (file.exists()) crawled = IO.readList("E:/user/crawled.txt");
		else crawled = new ArrayList<>();
		File dir = new File("E:/comment");
		File[] files = dir.listFiles();
		for (File f: files) {
			List<Comment> comments = IO.readComment(f.getAbsolutePath());
			for (Comment comment: comments) {
				if (!crawled.contains(comment.getUid())) {
					CrawlUserThread cu = new CrawlUserThread(comment.getUid());
					pool.execute(cu);
				}
			}
		}
		dir = new File("E:/repost");
		files = dir.listFiles();
		for (File f: files) {
			List<Repost> reposts = IO.readRepost(f.getAbsolutePath());
			for (Repost repost: reposts) {
				if (!crawled.contains(repost.getUid())) {
					CrawlUserThread cu = new CrawlUserThread(repost.getUid());
					pool.execute(cu);
				}
			}
		}
		pool.shutdown();
	}
}

class CrawlUserThread implements Runnable{
	private String uid;//uid
	public String getUid() {return uid;}
	public void setUid(String uid) {this.uid = uid;}
	public CrawlUserThread(String uid) {
		this.uid = uid;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		CrawlUser cu=new CrawlUser(this.uid);
		User user=cu.getUser();
	}
}