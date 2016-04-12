package cn.edu.gdufs.iiip.crawler;

import java.util.*;
import java.util.regex.*;

import cn.edu.gdufs.iiip.entity.*;
import cn.edu.gdufs.iiip.util.*;

/*在微博搜索搜索微博*/
public class CrawlSearch {
	private String topic;//搜索关键词
	
	public String getTopic() {return topic;}
	public void setTopic(String topic) {this.topic = topic;}
	
	public CrawlSearch(String topic) {
		this.topic = topic;
	}
	
	//获取搜索微博
	public List<Weibo> getSearch() {//爬取限制替换账号
		String html = crawlPage(1);
		List<Weibo> weiboList = matchPageWeibo(html);
		int pageNum = matchPageNum(html);
		for (int i=2; i<=pageNum; ++i) {
			try {Thread.sleep(1000);} catch (Exception ex) {}
			html = crawlPage(i);
			List<Weibo> pageWeibo = matchPageWeibo(html);
			weiboList.addAll(pageWeibo);
		}
		return weiboList;
	}
	
	//爬取一页搜索
	public String crawlPage(int page) {
		String url = "http://s.weibo.com/weibo/"+this.topic+"&page="+page;
		return (new Crawl()).crawl(url);
	}
	
	//匹配搜索页数
	private int matchPageNum(String html) {
		Pattern pat = Pattern
				.compile("<div\\s+class=\\\\\"search_page.*?<\\\\/div>");
		Matcher mat = pat.matcher(html);
		mat.find();
		html = mat.group();
		pat = Pattern.compile(">\\d+<");
		mat = pat.matcher(html);
		int pageNum = 1;
		while (mat.find()) {
			int page = Integer.parseInt(mat.group()
					.replace(">","").replace("<",""));
			if (page > pageNum) pageNum = page;
		}
		return pageNum;
	}
	
	//匹配网页微博
	public List<Weibo> matchPageWeibo(String html) {
		List<Weibo> pageWeibo = new ArrayList<>();
		int[] pos = new int[107];
		int num = 0;
		Pattern pat = Pattern.compile("<dl\\s+class=\\\\\"feed_list");
		Matcher mat = pat.matcher(html);
		while (mat.find()) pos[num++] = mat.start();
		for (int i=0; i<num-1; ++i) {
			Weibo weibo = matchWeibo(html.substring(pos[i], pos[i+1]));
			if (weibo != null) pageWeibo.add(weibo);
		}
		if (num > 0) {
			Weibo weibo = matchWeibo(html.substring(pos[num-1],html.length()));
			if (weibo != null) pageWeibo.add(weibo);
		}
		return pageWeibo;
	}
	
	//匹配一条微博
	private Weibo matchWeibo(String html) {
		String mid, uid, ruid, time = null, text, originText, source, url;
		int commentCount, repostCount, zanCount;
		Date weiboDate = null;
		mid="";uid="";ruid="";text="";
		originText="";source="未通过审核应用";url="";
		commentCount=repostCount=zanCount=0;
		Pattern pat;
		Matcher mat;
		
		//原微博已删除的转发微博
		if(html.contains("<div class=\\\"WB_deltxt")) return null;
		
		if (html.contains("isforward=\\\"1\\\"")) {//转发微博
			pat = Pattern.compile("<em>.*?<\\\\/em>");//text,originText
			mat = pat.matcher(html);
			if (mat.find()) text = Decode.expressionCharacter(mat.group());
			if (mat.find()) originText = Decode
					.expressionCharacter(mat.group());
			pat = Pattern.compile("rootuid=\\d*");//ruid
			mat = pat.matcher(html);
			if (mat.find()) ruid=mat.group().replace("rootuid=","");
		}
		else {//原创微博
			pat = Pattern.compile("<em>.*?<\\\\/em>");//text
			mat = pat.matcher(html);
			if (mat.find()) text = Decode.expressionCharacter(mat.group());
			originText = "";//originText
			ruid = "11111111";//ruid
		}
		
		pat = Pattern.compile("mid=\\\\\"\\d*");//mid
		mat = pat.matcher(html);
		if (mat.find()) mid = mat.group().replace("mid=\\\"", "");
		pat = Pattern.compile("\"id=\\d*");//uid
		mat = pat.matcher(html);
		if (mat.find()) uid = mat.group().replace("\"id=", "");
		
		//赞,转发,评论
		pat=Pattern.compile("<p\\s+class=\\\\\"info\\s+W_linkb.*?<\\\\/p>");
		mat = pat.matcher(html);
		if (mat.find()) {
			String str = mat.group();
			pat = Pattern.compile("赞<\\\\/em>\\(\\d+\\)");//赞
			mat = pat.matcher(str);
			if (mat.find()) {
				zanCount=Integer.parseInt(mat.group()
						.replaceAll("^.*?\\(","").replace(")",""));
			}
			pat = Pattern.compile("转发\\(\\d+\\)");//转发
			mat = pat.matcher(str);
			if (mat.find()) {
				repostCount = Integer.parseInt(mat.group()
						.replaceAll("^.*?\\(", "").replaceAll("\\).*?$", ""));
			}
			pat = Pattern.compile("评论\\(\\d+\\)");//评论
			mat = pat.matcher(str);
			if (mat.find()) {
				commentCount = Integer.parseInt(mat.group()
						.replaceAll("^.*?\\(", "").replaceAll("\\).*?$", ""));
			}
			pat = Pattern.compile("date=\\\\\"\\d*");//weiboDate
			mat = pat.matcher(str);
			if (mat.find()) {
				String dateStr = mat.group().replace("date=\\\"", "");
				weiboDate = new Date(Long.parseLong(dateStr));
			}
			pat = Pattern.compile("<a\\s+href=\\\\\"http.*?\"");//url
			mat = pat.matcher(str);
			if (mat.find()) url = mat.group().replace("<a href=\\","")
					.replace("\"","").replace("\\","");
			pat = Pattern.compile("target.*?<\\\\/a>");//source
			mat = pat.matcher(str);
			if (mat.find()) source = Decode.expressionCharacter(mat.group()
					.replaceFirst("^.*?>", ""));
		}
		
		return new Weibo(mid,uid,ruid,time,text,originText,weiboDate,
				commentCount,repostCount,zanCount,source,url);
	}
}
