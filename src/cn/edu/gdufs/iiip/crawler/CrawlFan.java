package cn.edu.gdufs.iiip.crawler;

import java.util.*;
import java.util.regex.*;

import cn.edu.gdufs.iiip.entity.*;

/*爬取一个用户的粉丝*/
public class CrawlFan {
	private String uid;//用户uid
	
	public String getUid() {return uid;}
	public void setUid(String uid) {this.uid = uid;}
	
	public CrawlFan(String uid) {
		this.uid = uid;
	}
	
	//获取用户粉丝
	public List<User> getFan() {
		//http://weibo.com/"+uid+"/myfans
//		String url="http://weibo.com/"+uid+"/fans";
		String url = "http://weibo.com/"+uid+"/follow?relate=fans";
		Crawl c = new Crawl();
		String html = c.crawl(url);
		List<User> fanList = matchPageFan(html);
		int pageNum = matchPageNum(html);
		if (pageNum > 10) pageNum = 10;//新浪微博限制只能获取前10页
		System.out.println("1/"+pageNum);
		for (int i=2; i<=pageNum; ++i) {
			html = crawlPage(i);
			System.out.println(i+"/"+pageNum);
			List<User> pageFan = matchPageFan(html);
			fanList.addAll(pageFan);
		}
		return fanList;
	}
	
	//爬取一页粉丝
	private String crawlPage(int page) {
//		http://weibo.com/5220650532/follow?page=2
		String url="http://weibo.com/"+this.uid+"/follow?page="+page;
		return (new Crawl()).crawl(url);
	}
	
	//匹配粉丝页数
	private int matchPageNum(String html) {
		Pattern pat = Pattern
				.compile("<div\\s+class=\\\\\"W_pages.*?<\\\\/div>");
		Matcher mat = pat.matcher(html);
		if (mat.find()) {
			html = mat.group();
			pat = Pattern.compile(">\\d+<");
			mat = pat.matcher(html);
			int pageNum = 1;
			while (mat.find()) {
				int i = Integer.parseInt(mat.group()
						.replace(">", "").replace("<", ""));
				if (i > pageNum) pageNum = i;
			}
			return pageNum;
		}
		else return 1;
	}
	
	//匹配一页粉丝
	private List<User> matchPageFan(String html) {
		List<User> pageUser = new ArrayList<User>();
		int[] pos = new int[107];
		int num = 0;
		Pattern pat = Pattern.compile("<li\\s+class=\\\\\"clearfix");
		Matcher mat = pat.matcher(html);
		while (mat.find()) pos[num++] = mat.start();
		for (int i=0; i<num-1; ++i) {
			User user = matchFan(html.substring(pos[i], pos[i+1]));
			if (user != null) pageUser.add(user);
		}
		if (num > 0) {
			User user = matchFan(html.substring(pos[num-1], html.length()));
			if (user != null) pageUser.add(user);
		}
		return pageUser;
	}
	
	//匹配一个粉丝
	private User matchFan(String html) {
		String uid="", nickName="";
		int followCount=-1, fanCount=-1, weiboCount=-1;
		
		Pattern pat = Pattern.compile("uid=\\d*");//uid
		Matcher mat = pat.matcher(html);
		if (mat.find()) uid = mat.group().replace("uid=", "");
		pat = Pattern.compile("&fnick=.*?&");//nickName
		mat = pat.matcher(html);
		if (mat.find()) nickName = mat.group()
				.replace("&fnick=", "").replace("&", "");
		
		pat = Pattern
				.compile("<div\\s+class=\\\\\"connect\\\\\">.*?<\\\\/div>");
		mat = pat.matcher(html);
		if (mat.find()) {
			String userHtml = mat.group();
			pat = Pattern.compile(">\\d+<");//followCount,fanCount,weiboCount
			mat = pat.matcher(userHtml);
			if (mat.find()) {
				String followStr = mat.group().replace(">","").replace("<","");
				followCount = Integer.parseInt(followStr);
			}
			if (mat.find()) {
				String fanStr = mat.group().replace(">","").replace("<","");
				fanCount = Integer.parseInt(fanStr);
			}
			if (mat.find()) {
				String weiboStr = mat.group().replace(">","").replace("<","");
				weiboCount = Integer.parseInt(weiboStr);
			}
		}
		
		return new User(uid,nickName,followCount,fanCount,weiboCount);
	}
}
