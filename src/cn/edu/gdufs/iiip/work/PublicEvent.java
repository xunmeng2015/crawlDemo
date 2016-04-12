package cn.edu.gdufs.iiip.work;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

import cn.edu.gdufs.iiip.crawler.*;
import cn.edu.gdufs.iiip.entity.*;
import cn.edu.gdufs.iiip.login.Cookie;
import cn.edu.gdufs.iiip.util.IO;

/*
 * 丽云
 * 公共事件语料获取
 * 爬取约2200个媒体会微博的所有微博及评论转发
 */
//抗震小英雄诈骗
//APEC村庄禁烧柴火
//呼格吉勒图冤杀案
//中移动短信1毛1条
//激光祛斑
//跨区域用盐被罚款
//68年前新四军借条
//蓝翔技校
public class PublicEvent {
	static String weiboPath = "/weibo/";
	static String commentPath = "/comment/";
	static String repostPath = "/repost/";
	static String eventPath = "D:/event.txt";
	
	static String topic = "中移动短信1毛1条";
	static String root="d:/公共事件/"+topic;
	public static void main(String[] args) {
		if(!IO.readList(eventPath).contains(topic)){
			new File(root).mkdirs();
			Cookie.login();
//			getWeibo(topic);
			getCommentRepost(topic);
		}else{
			System.out.println("Event has been crawled!");
		}
	}
	
	//从媒体汇微博中获取话题的微博
	private static void getWeibo(String topic) {
		ExecutorService pool = Executors.newFixedThreadPool(10);
		(new File(root+weiboPath)).mkdirs();
		List<String> uids = IO.readList("uid/media_user.txt");
		for (String uid: uids) {
			File f = new File(root+weiboPath+uid+".txt");
			if (!f.exists()) {
				CrawlTopicWeiboThread ctwt =
						new CrawlTopicWeiboThread(uid, topic);
				pool.execute(ctwt);
			}
		}
		
		pool.shutdown();
	}
		
	//从话题微博中获取评论和转发内容
	private static void getCommentRepost(String topic) {
		File dir = new File(root+weiboPath);
		File files[] = dir.listFiles();
		List<String> mids = new ArrayList<String>();
		for (File file: files) {
			String text = IO.read(file.getAbsolutePath());
			Pattern pat = Pattern.compile("<mid>.*?</mid>");
			Matcher mat = pat.matcher(text);
			while (mat.find()) {
				String mid = mat.group().replaceAll("<.*?>", "");
				mids.add(mid);
			}
		}
		System.out.println("get "+mids.size()+" weibo");
		(new File(root+commentPath)).mkdirs();
		(new File(root+repostPath)).mkdirs();
		ExecutorService pool = Executors.newFixedThreadPool(10);
		for (String mid: mids) {
			File f1 = new File(root+commentPath+mid+".txt");
			if (!f1.exists()) {
				CrawlCommentThread cct = new CrawlCommentThread(mid);
				pool.execute(cct);
			}
			File f2 = new File(root+repostPath+mid+".txt");
			if (!f2.exists()) {
				CrawlRepostThread crt = new CrawlRepostThread(mid);
				pool.execute(crt);
			}
		}
		pool.shutdown();
	}
}

//对CrawlTopicWeibo的多线程实现
class CrawlTopicWeiboThread implements Runnable {
	private String uid;//uid
	private String topic;//主题
	public String getUid() {return uid;}
	public void setUid(String uid) {this.uid = uid;}
	public CrawlTopicWeiboThread(String uid, String topic) {
		this.uid = uid;
		this.topic = topic;
	}
	
	@Override
	public void run() {
		CrawlTopicWeibo cw = new CrawlTopicWeibo(this.uid, this.topic);
		List<Weibo> weibos = cw.getWeibo();
		IO.printWeibo(weibos, PublicEvent.root+PublicEvent.weiboPath+this.uid+".txt");
		System.out.println(this.uid+"    "+weibos.size()+" weibo");
	}
}

//对CrawlComment的多线程实现
class CrawlCommentThread implements Runnable {
	private String mid;//mid
	
	public String getUid() {return mid;}
	public void setUid(String uid) {this.mid = uid;}
	
	public CrawlCommentThread(String mid) {
		this.mid = mid;
	}
	
	@Override
	public void run() {
		CrawlComment cw = new CrawlComment(this.mid);
		List<Comment> comments = cw.getComment();
		IO.printComment(comments, PublicEvent.root+PublicEvent.commentPath+this.mid+".txt");
		System.out.println(this.mid+"    "+comments.size()+" comment");
	}
}

//对CrawlRepost的多线程实现
class CrawlRepostThread implements Runnable {
	private String mid;//mid
	
	public String getUid() {return mid;}
	public void setUid(String uid) {this.mid = uid;}
	
	public CrawlRepostThread(String mid) {
		this.mid = mid;
	}
	
	@Override
	public void run() {
		CrawlRepost cr = new CrawlRepost(this.mid);
		List<Repost> reposts = cr.getRepost();
		IO.printRepost(reposts, PublicEvent.root+PublicEvent.repostPath+this.mid+".txt");
		System.out.println(this.mid+"    "+reposts.size()+" repost");
	}
}
