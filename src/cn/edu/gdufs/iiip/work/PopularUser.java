package cn.edu.gdufs.iiip.work;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

import cn.edu.gdufs.iiip.crawler.*;
import cn.edu.gdufs.iiip.entity.*;
import cn.edu.gdufs.iiip.login.Cookie;
import cn.edu.gdufs.iiip.util.*;

public class PopularUser {
	private static String path = "D:/popularuser/";
	
	public static void main(String[] args) {
		Cookie.login();
		new File(path).mkdir();
		List<String> uids = IO.readList("uid/media_user.txt");
		ExecutorService pool = Executors.newFixedThreadPool(10);
		for (String uid: uids) {
			File file = new File(path+uid+".txt");
			if (!file.exists()) {
				CrawlUserPopularity cup = new CrawlUserPopularity(uid);
				pool.execute(cup);
			}
		}
		pool.shutdown();
	}
}

class CrawlUserPopularity implements Runnable {
	private static int weiboNum = 1000;
	private static String path = "D:/popularuser/";
	
	private String uid;
	
	public String getUid() {return uid;}
	public void setUid(String uid) {this.uid = uid;}
	public CrawlUserPopularity(String uid) {
		this.uid = uid;
	}
	
	@Override
	public void run() {
		CrawlUser cu = new CrawlUser(this.uid);
		User user = cu.getUser();
		CrawlWeibo cw = new CrawlWeibo(this.uid);
		List<Weibo> weibos = cw.getWeibo(weiboNum);
		(new File(path)).mkdirs();
		IO.print(user.getFollowCount()+" "+user.getFanCount()
				+" "+averageRepost(weibos)+" "+averageComment(weibos),
				path+this.uid+".txt");
	}
	
	private static int averageComment(List<Weibo> weibos) {
		int sum = 0;
		for (int i=0; i<weibos.size(); ++i) {
			sum += weibos.get(i).getCommentCount();
		}
		return sum / weibos.size();
	}
	
	private static int averageRepost(List<Weibo> weibos) {
		int sum = 0;
		for (int i=0; i<weibos.size(); ++i) {
			sum += weibos.get(i).getRepostCount();
		}
		return sum / weibos.size();
	}
}