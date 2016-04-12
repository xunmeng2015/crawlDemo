package cn.edu.gdufs.iiip.crawler;

import java.util.*;
import java.util.regex.*;

import cn.edu.gdufs.iiip.entity.*;
import cn.edu.gdufs.iiip.util.*;

/*爬取一个用户特定关键词的微博*/
public class CrawlTopicWeibo {
	private String uid;//用户uid
	private String topic;//关键词
	private String page_id;
	private String domain;
	private String headStr;//mblog,feed,weibo....
	
	public String getUid() {return uid;}
	public void setUid(String uid) {this.uid = uid;}
	public String getTopic() {return topic;}
	public void setTopic(String topic) {this.topic = topic;}
	
	public CrawlTopicWeibo(String uid, String topic) {
		this.uid = uid;
		this.topic = topic;
	}
	
	//获取用户微博
	public List<Weibo> getWeibo() {
		Crawl c = new Crawl();
		String html = c.crawl("http://weibo.com/u/"+uid);
		setField(html);
		html = crawlPage(1);
		List<Weibo> weiboList = matchPageWeibo(html);
		int pageNum = matchPageNum(html);
		System.out.println("1/"+pageNum+" get "+weiboList.size()+" weibo");
		for (int i=2; i<=pageNum; ++i) {
			try {Thread.sleep(1000);} catch (Exception ex) {}
			html = crawlPage(i);
			List<Weibo> pageWeibo = matchPageWeibo(html);
			if (pageWeibo.size() == 0) return weiboList;
			System.out.println(i+"/"+pageNum+" get "
					+pageWeibo.size()+" weibo");
			weiboList.addAll(pageWeibo);
		}
		return weiboList;
	}
	
	//爬取一页微博
	private String crawlPage(int page) {
		String urlMain="http://weibo.com/p/"+this.page_id+"/home?from=page_"+this.domain //+this.headStr
				+"&is_search=1&key_word="+this.topic+"&page="+page;
		String urlAdd1="http://weibo.com/p/aj/v6/mblog/mbloglist?domain="
				+this.domain+"&pre_page="+page+"&page="+page+"&pagebar=0&id="
				+this.page_id+"&key_word="+this.topic+"&is_search=1";
		String urlAdd2="http://weibo.com/p/aj/v6/mblog/mbloglist?domain="
				+this.domain+"&pre_page="+page+"&page="+page+"&pagebar=1&id="
				+this.page_id+"&key_word="+this.topic+"&is_search=1";
		Crawl c = new Crawl();
		String content = c.crawl(urlMain);
		String content1 = c.crawl(urlAdd1);
		String content2 = c.crawl(urlAdd2);
		return insertHtml(content, content1, content2);
	}
	
	//将一页微博的二三部分内容插入第一部分(html博文部分插入"<div node-type=\"lazyload"之前)
	private String insertHtml(String content, String content1, String content2) {
		String contentAdd =
				content1.replaceFirst("^.*?data\":\"","")
					.replaceFirst("\"}$","")
				+
				content2.replaceFirst("^.*?data\":\"", "")
					.replaceFirst("\"}$","");
		int insertPos = content.indexOf("<div node-type=\\\"lazyload");
		if (insertPos==-1) return content;
		String html = content.substring(0, insertPos) + contentAdd
				+ content.substring(insertPos, content.length());
		//去除加载中
		html = html
				.replaceAll("<div node-type=\\\\\"lazyload.*?<\\\\/div>","");
		return html;
	}
	
	//匹配用户网页url中的domain,page_id,page_id后的headStr?字符串
	private void setField(String html) {
		//domain
		String domain = null;
		Pattern pat = Pattern.compile("<head>.*?</head>");
		Matcher mat = pat.matcher(html);
		if (mat.find()) domain = mat.group();
		pat = Pattern.compile("CONFIG\\['domain'\\]\\s?=\\s?'.*?'");
		mat = pat.matcher(domain);
		if (mat.find()) domain = mat.group();
		this.domain = domain
				.replaceFirst("^.*?=\\s?'", "").replaceFirst("'$", "");
		//page_id
		String page_id = null;
		pat = Pattern.compile("<head>.*?</head>");
		mat = pat.matcher(html);
		if (mat.find()) page_id = mat.group();
		pat = Pattern.compile("CONFIG\\['page_id'\\]\\s?=\\s?'.*?'");
		mat = pat.matcher(page_id);
		if (mat.find()) page_id = mat.group();
		this.page_id = page_id
				.replaceFirst("^.*?=\\s?'", "").replaceFirst("'$", "");
//		System.out.println(this.page_id);
		//headStr
		String headStr = null;
		pat = Pattern.compile("<ul\\s+class=\\\\\"pftb_ul S_line1.*?"
				+ "<li\\s+class=\\\\\"pftb_itm pftb_itm_lst S_line1");
		mat = pat.matcher(html);
		if (mat.find()) {
			headStr = mat.group();
			pat = Pattern.compile("<[^<]*?微博.*?<\\\\/a>");
			mat = pat.matcher(headStr);
			if (mat.find()) {
				headStr = mat.group();
				pat = Pattern.compile("[A-Za-z]*?\\?");
				mat = pat.matcher(headStr);
				if (mat.find()) this.headStr = mat.group().replace("?", "");
				System.out.println(this.headStr);
			}
		}
		else this.headStr = "weibo";
	}
	
	//匹配微博页数
	private int matchPageNum(String html) {
		Pattern pat = Pattern
				.compile("<div node-type=\\\\\"feed_list_page.*?<\\\\/a>");
		Matcher mat = pat.matcher(html);
		if (mat.find()) {
			String str = mat.group();
			str = str.replaceFirst("^.*?&nbsp;", "")
					.replaceFirst("&nbsp;.*?$", "");
			return Integer.parseInt(str);
		}
		else return 1;
	}
	
	//匹配网页微博
	private List<Weibo> matchPageWeibo(String html) {
		List<Weibo> pageWeibo = new ArrayList<Weibo>();
		int[] tbinfo = new int[107], minfo = new int[107];
		int tbnum = 0, mnum = 0;
		Pattern pat = Pattern.compile("<div\\s+minfo");
		Matcher mat = pat.matcher(html);
		while (mat.find()) minfo[mnum++] = mat.start();
		pat = Pattern.compile("<div\\s+tbinfo");
		mat = pat.matcher(html);
		while (mat.find()) tbinfo[tbnum++] = mat.start();
		for (int i=0; i<mnum; ++i) tbinfo[tbnum+i] = minfo[i];
		tbnum += mnum;
		for (int i=0; i<tbnum; ++i) for (int j=i+1; j<tbnum; ++j) {
			if (tbinfo[i] > tbinfo[j]) {
				int tmp=tbinfo[i];tbinfo[i]=tbinfo[j];tbinfo[j]=tmp;
			}
		}
		for (int i=0; i<tbnum-1; ++i) {
			Weibo weibo = matchWeibo(html.substring(tbinfo[i], tbinfo[i+1]));
			if (weibo != null) pageWeibo.add(weibo);
		}
		if (tbnum > 0) {
			Weibo weibo = matchWeibo(html
					.substring(tbinfo[tbnum-1], html.length()));
			if (weibo != null) pageWeibo.add(weibo);
		}
		return pageWeibo;
	}
	
	//匹配一条微博
	private Weibo matchWeibo(String html) {
		String mid, uid, ruid, time = null, text, originText, source, url;
		int commentCount, repostCount, zanCount;
		Date weiboDate = null;
		mid="";uid="";ruid="";text="";originText="";source="";url="";
		commentCount=repostCount=zanCount=0;
		Pattern pat;
		Matcher mat;
		
		//原微博已删除的转发微博
		if(html.contains("<div class=\\\"WB_deltxt")) return null;
		
		pat = Pattern.compile("^<.*?>");
		mat = pat.matcher(html);
		mat.find();
		String headStr = mat.group();
		if (headStr.contains("minfo")) {//转发微博
			pat = Pattern.compile("rouid=\\d+\\\\");//ruid
			mat = pat.matcher(html);
			if (mat.find()) ruid = mat.group()
					.replaceAll("^.*?=","").replaceAll("\\\\$","");
			//text,originText
			pat=Pattern.compile("<div\\s+class=\\\\\"WB_text.*?<\\\\/div>");
			mat = pat.matcher(html);
			if (mat.find()) text = Decode.expressionCharacter(mat.group());
			if (mat.find()) originText = Decode
					.expressionCharacter(mat.group());
		}
		else {//原创微博
			ruid = "11111111";//ruid
			//text
			pat = Pattern
					.compile("<div\\s+class=\\\\\"WB_text.*?<\\\\/div>");
			mat = pat.matcher(html);
			if (mat.find()) text = Decode.expressionCharacter(mat.group());
			originText = "";//originText
		}
		
		pat = Pattern.compile("<a\\s+class=\\\\\"S_link2.*?<\\\\/a>");//source
		mat = pat.matcher(html);
		if (mat.find()) source = Decode.expressionCharacter(mat.group());
		pat = Pattern.compile("\\Wmid=\\d+\\D");//mid
		mat = pat.matcher(html);
		if (mat.find()) mid = mat.group()
				.replaceAll("^.*?=", "").replaceAll(".$", "");
		pat = Pattern.compile("\\Wouid=\\d+\\D");//uid
		mat = pat.matcher(html);
		if (mat.find()) uid = mat.group()
				.replaceAll("^.*?=", "").replaceAll(".$", "");
		
		//赞,转发,评论
		pat=Pattern
				.compile("<div\\s+class=\\\\\"WB_handle\\\\\">.*?<\\\\/div>");
		mat = pat.matcher(html);
		if (mat.find()) {
			String str = mat.group();
			pat = Pattern.compile("<em\\s+class=\\\\\"W_ico20"
					+ "\\s+icon_praised_b\\\\\"><\\\\/em>.*?<");
			mat = pat.matcher(str);
			if (mat.find()) {
				if (mat.group().matches("^.*?\\(.*?$")) {
					zanCount = Integer.parseInt(mat.group()
							.replaceAll("^.*?\\(", "")
							.replaceAll("\\).*?$", ""));
				}
			}
			pat = Pattern.compile("转发.*?<");
			mat = pat.matcher(str);
			if (mat.find()) {
				String zhuanfa = mat.group();
				if (zhuanfa.matches("^.*?\\(.*?$")) {
					repostCount = Integer.parseInt(zhuanfa
							.replaceAll("^.*?\\(", "")
							.replaceAll("\\).*?$", ""));
				}
			}
			pat = Pattern.compile("评论.*?<");
			mat = pat.matcher(str);
			if (mat.find()) {
				String pinglun = mat.group();
				if (pinglun.matches("^.*?\\(.*?$")) {
					commentCount = Integer.parseInt(pinglun
							.replaceAll("^.*?\\(", "")
							.replaceAll("\\).*?$", ""));
				}
				else commentCount = 0;
			}
		}
		
		pat = Pattern.compile("<a\\s+name.*?<\\\\/a>");
		mat = pat.matcher(html);
		if (mat.find()) {
			String urlHtml = mat.group();
			pat = Pattern.compile("date=\\\\\".*?\\\\\"");//weiboDate
			mat = pat.matcher(urlHtml);
			if (mat.find()) {
				String dateStr = mat.group().replaceAll("^.*?\"", "")
						.replaceAll("\\\\\"$", "");
				weiboDate = new Date(Long.parseLong(dateStr));
			}
			pat = Pattern.compile("href=\\\\\".*?\"");//url
			mat = pat.matcher(urlHtml);
			if (mat.find()) url = "http://weibo.com" +mat.group()
					.replaceAll("\\\\", "").replaceFirst("^.*?\"", "")
					.replaceAll("\"$", "").replaceFirst("\\?.*?$", "");
		}
		
		return new Weibo(mid,uid,ruid,time,text,originText,weiboDate,
				commentCount,repostCount,zanCount,source,url);
	}
	
	public static void main(String[] args) {
		CrawlTopicWeibo c=new CrawlTopicWeibo("2287260393", "我们");
		c.getWeibo();
	}
}
