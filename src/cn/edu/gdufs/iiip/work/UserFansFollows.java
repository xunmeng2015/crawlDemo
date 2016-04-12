package cn.edu.gdufs.iiip.work;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import cn.edu.gdufs.iiip.crawler.*;
import cn.edu.gdufs.iiip.entity.User;
import cn.edu.gdufs.iiip.login.Cookie;
import cn.edu.gdufs.iiip.util.IO;

/*
 * 娟娜
 * 爬取给定用户的关注与粉丝uid
 * 新浪微博限制只能获取前10页
 */
public class UserFansFollows {
	private static String uidPath = "uid/juanna_user.txt";
	static String fanPath = "D:/fan/";
	static String followPath = "D:/follow/";
	
	
	public static void main(String[] args) {
		Cookie.login();
		List<String> uids = IO.readList(uidPath);
		(new File(fanPath)).mkdirs();
		(new File(followPath)).mkdirs();
		ExecutorService pool = Executors.newFixedThreadPool(10);
		for (String uid: uids) {
			File f = new File(fanPath+uid+".txt");
			if (!f.exists()) {
				CrawlFanThread cft = new CrawlFanThread(uid);
				pool.execute(cft);
			}
			f = new File(followPath+uid+".txt");
			if (!f.exists()) {
				CrawlFollowThread cft = new CrawlFollowThread(uid);
				pool.execute(cft);
			}
		}
		pool.shutdown();
	}
}

//对CrawlFan的多线程实现
class CrawlFanThread implements Runnable {
	private String uid;//uid
	public CrawlFanThread(String uid) {
		this.uid = uid;
	}
	
	@Override
	public void run() {
		CrawlFan cf = new CrawlFan(this.uid);
		List<User> fans = cf.getFan();
		System.out.println(this.uid+"    "+fans.size()+" fans");
		String fanPath="D:/fan/";
		IO.printUid(fans, fanPath+this.uid+".txt");
		//TODO
	}
}

//对CrawlFollow的多线程实现
class CrawlFollowThread implements Runnable {
	private String uid;//uid
	public CrawlFollowThread(String uid) {
		this.uid = uid;
	}
	
	@Override
	public void run() {
		CrawlFollow cr = new CrawlFollow(this.uid);
		List<User> follows = cr.getFollow();
		System.out.println(this.uid+"    "+follows.size()+" follows");
		String followPath="D:/follow/";
		IO.printUid(follows, followPath+this.uid+".txt");
		//TODO
	}
}
