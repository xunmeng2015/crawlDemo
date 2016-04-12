package cn.edu.gdufs.iiip.crawler;

import java.util.*;
import java.util.regex.*;

import cn.edu.gdufs.iiip.entity.*;
import cn.edu.gdufs.iiip.util.*;

/*爬取一个用户的所有或部分微博*/
public class CrawlWeibo {
	private String uid;//用户uid
	private Date date;
	private String page_id;
	private String domain;
	private String headStr;//mblog,feed,weibo....
	
	public String getUid() {return uid;}
	public void setUid(String uid) {this.uid = uid;}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public CrawlWeibo(String uid) {
		this.uid = uid;
		this.date=null;
	}
	public CrawlWeibo(String uid,Date date) {
		this.uid = uid;
		this.date=date;
	}
	
	//获取用户微博
	public List<Weibo> getWeibo() {
		Crawl c = new Crawl();
		String html = c.crawl("http://weibo.com/u/"+uid);
//		System.out.println(html);
		setField(html);
		html = crawlPage(51);
//		System.out.println(html);
		List<Weibo> weiboList = matchPageWeibo(html);
		int pageNum = matchPageNum();
		System.out.println(pageNum);
		System.out.println("51/"+pageNum+" get "+weiboList.size()+" weibo");
		for (int i=52; i<=70; ++i) {
			try {Thread.sleep(5000);} catch (Exception ex) {}
			html = crawlPage(i);
//			html=c.crawl("http://weibo.com/u/"+uid+"?page="+i);
//			System.out.println(html);
			List<Weibo> pageWeibo = matchPageWeibo(html);
			if (pageWeibo.size() == 0) return weiboList;
			System.out.println(i+"/"+pageNum+" get "
					+pageWeibo.size()+" weibo");
			weiboList.addAll(pageWeibo);
		}
		return weiboList;
	}
	
	//更新给定时间到现在的用户微博
	public List<Weibo> getWeibo(Date date) {
		Crawl c = new Crawl();
		String html = c.crawl("http://weibo.com/u/"+uid);
		setField(html);
		html = crawlPage(1);
		List<Weibo> pageWeibo = matchPageWeibo(html);
		List<Weibo> weiboList = new ArrayList<Weibo>();
		List<Weibo> newWeibo = new ArrayList<Weibo>();
		for (Weibo w: pageWeibo) {
			if (w.getWeiboDate().after(date)) newWeibo.add(w);
		}
		int pageNum = matchPageNum();
		System.out.println("1/"+pageNum+" get "+newWeibo.size()+" weibo");
		int topWeibo = matchTop(html);//置顶微博数
		weiboList.addAll(newWeibo);
		if (newWeibo.size()+topWeibo<pageWeibo.size() || newWeibo.size()==0) {
			return weiboList;//爬取到更新
		}
		for (int i=2; i<=pageNum; ++i) {
			html = crawlPage(i);
			pageWeibo = matchPageWeibo(html);
			newWeibo.clear();
			if (pageWeibo.size() == 0) return weiboList;
			for (Weibo w: pageWeibo) {
				if (w.getWeiboDate().after(date)) newWeibo.add(w);
			}
			System.out.println(i+"/"+pageNum+" get "+newWeibo.size()+" weibo");
			weiboList.addAll(newWeibo);
			if (newWeibo.size()<pageWeibo.size()
					|| newWeibo.size()==0) break;//爬取到更新
			try {Thread.sleep(1000);} catch (Exception ex) {}
		}
		return weiboList;
	}

	//获取用户微博，前num条，不足则爬完
	public List<Weibo> getWeibo(int num) {
		int sum = 0;
		Crawl c = new Crawl();
		String html = c.crawl("http://weibo.com/u/"+uid);
		setField(html);
		html = crawlPage(1);
		List<Weibo> pageWeibo = matchPageWeibo(html);
		List<Weibo> weiboList = new ArrayList<>();
		int pageNum = matchPageNum();
		System.out.println("1/"+pageNum+" get "+pageWeibo.size()+" weibo");
		if (pageWeibo.size() <=num) {
			weiboList.addAll(pageWeibo);
			sum += pageWeibo.size();
		}
		else {//第一页足够多微博
			for (int i=0; i<num; ++i) weiboList.add(pageWeibo.get(i));
			return weiboList;
		}
		for (int i=2; i<=pageNum; ++i) {
			pageWeibo = matchPageWeibo(html);
			if (pageWeibo.size() == 0) return weiboList;
			System.out.println(i+"/"+pageNum
					+" get "+pageWeibo.size()+" weibo");
			try {Thread.sleep(1000);} catch (Exception ex) {}
			html = crawlPage(i);
			if (pageWeibo.size() + sum <num) {
				weiboList.addAll(pageWeibo);
				sum += pageWeibo.size();
			}
			else {//第i页足够多微博
				for (int j=0; j<num-sum; ++j) weiboList.add(pageWeibo.get(j));
				return weiboList;
			}
		}
		return weiboList;
	}
	
	//爬取一页微博
	private String crawlPage(int page) {
//		String urlMain="http://weibo.com/p/"+this.page_id+"/"
//				+this.headStr+"?page="+page;
		String urlMain="http://weibo.com/u/"+this.uid+"?is_search=0&visible=0&is_tag=0&profile_ftype=1&page="+page;
		String urlAdd1="http://weibo.com/p/aj/mblog/mbloglist?domain="
				+this.domain+"&pre_page="+page+"&page="+page
				+"&pagebar=0&id="+this.page_id+"&feed_type=0";
		String urlAdd2="http://weibo.com/p/aj/mblog/mbloglist?domain="
				+this.domain+"&pre_page="+page+"&page="+page
				+"&pagebar=1&id="+this.page_id+"&feed_type=0";
		Crawl c = new Crawl();
		String content = c.crawl(urlMain);
		String content1 = c.crawl(urlAdd1);
		String content2 = c.crawl(urlAdd2);
//		System.out.println(content);
//		System.out.println("++++");
//		System.out.println(Decode.UTF8(content1));
//		System.out.println("----");
//		System.out.println(Decode.UTF8(content2));
		return content+content1+content2;
	}
	
	//将一页微博的二三部分内容插入第一部分
	//html博文部分插入"<div node-type=\"lazyload"之前
	private String insertHtml(String content,
			String content1, String content2) {
		String contentAdd = 
				content1
				.replaceFirst("^.*?data\":\"","")
				.replaceFirst("\"}$","")
				+
				content2
				.replaceFirst("^.*?data\":\"", "")
				.replaceFirst("\"}$","");
		int insertPos = content.indexOf("<div node-type=\\\"lazyload");
		if (insertPos==-1) return content;
		String html = content.substring(0, insertPos) + contentAdd
				+ content.substring(insertPos, content.length());
		html = html.replaceAll("<div node-type=\\\\\""
				+ "lazyload.*?<\\\\/div>","");//去除加载中
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
		this.domain = domain.replaceFirst("^.*?=\\s?'", "")
				.replaceFirst("'$", "");
		//page_id
		String page_id = null;
		pat = Pattern.compile("<head>.*?</head>");
		mat = pat.matcher(html);
		if (mat.find()) page_id = mat.group();
		pat = Pattern.compile("CONFIG\\['page_id'\\]\\s?=\\s?'.*?'");
		mat = pat.matcher(page_id);
		if (mat.find()) page_id = mat.group();
		this.page_id = page_id.replaceFirst("^.*?=\\s?'", "")
				.replaceFirst("'$", "");
		//headStr
		String headStr = null;
		pat = Pattern.compile("<ul\\s+class=\\\\\"pftb_ul S_line1"
				+ ".*?<li\\s+class=\\\\\"pftb_itm pftb_itm_lst S_line1");
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
			}
		}
		else this.headStr = "weibo";
	}
	
	//匹配微博页数
	private int matchPageNum() {
		String html;
		String urlAdd2="http://weibo.com/p/aj/mblog/mbloglist?domain="
				+this.domain+"&pre_page=1&page=1&pagebar=1&id="+this.page_id+"&feed_type=0";
		Crawl c = new Crawl();
		html=c.crawl(urlAdd2);
		Pattern pat = Pattern.compile("<div node-type=\\\\\""
				+ "feed_list_page.*?<\\\\/a>");
		Matcher mat = pat.matcher(html);
		if (mat.find()) {
			String str = mat.group();
			str = str.replaceFirst("^.*?&nbsp;", "")
					.replaceFirst("&nbsp;.*?$", "");
			return Integer.parseInt(str);
		}
		else return 1;
	}
	
	//匹配置顶微博数量
	private int matchTop(String html) {
		int count = 0;
		Pattern pat = Pattern.compile("feedtype=\\\\\"top\\\\\"");
		Matcher mat = pat.matcher(html);
		while (mat.find()) ++count;
		return count;
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
			Weibo weibo = matchWeibo(html.substring(
					tbinfo[tbnum-1], html.length()));
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
			pat=Pattern.compile("<div\\s+class=\\\\\""
					+ "WB_text.*?<\\\\/div>");//text,originText
			mat = pat.matcher(html);
			if (mat.find()) text = Decode.expressionCharacter(mat.group());
			if (mat.find()) originText = Decode.expressionCharacter(mat.group());
		}
		else {//原创微博
			ruid = "11111111";//ruid
			pat = Pattern.compile("<div\\s+class=\\\\\""
					+ "WB_text.*?<\\\\/div>");//text
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
		
		pat=Pattern.compile("<div\\s+class=\\\\\""
				+ "WB_handle\\\\\">.*?<\\\\/div>");//赞,转发,评论
		mat = pat.matcher(html);
		if (mat.find()) {
			String str = mat.group();
//			System.out.println(str);//test
			pat = Pattern.compile("icon_praised_b.*?/em>");
			mat = pat.matcher(str);
			if (mat.find()) {
				pat = Pattern.compile("<em>.*?/em>");
				mat = pat.matcher(mat.group());
				if (mat.find()) {
					String zan="";
					zan=mat.group().replaceAll("<.*?>", "").trim();
					if(zan.length()==0){
						zan="0";
					}
					zanCount = Integer.parseInt(zan);
				}
			}
			pat = Pattern.compile("转发.*?<");
			mat = pat.matcher(str);
			if (mat.find()) {
				String zhuanfa = mat.group().replace("转发", "").replace("<", "").trim().replace("(", "").replace(")", "");
				if(zhuanfa.length()==0){
					zhuanfa="0";
				}
					repostCount = Integer.parseInt(zhuanfa);

			}
			
			pat = Pattern.compile(">评论.*?<");
			mat = pat.matcher(str);
			if (mat.find()) {
				String pinglun = mat.group().replace("评论", "").replace("<", "").replace(">", "").trim().replace("(", "").replace(")", "");
				if (pinglun.length()==0) {
					pinglun="0";
				}
				commentCount = Integer.parseInt(pinglun);
			}
		}
		
		pat = Pattern.compile("<a\\s+name.*?<\\\\/a>");
		mat = pat.matcher(html);
		if (mat.find()) {
			String urlHtml = mat.group();
			pat = Pattern.compile("date=\\\\\".*?\\\\\"");//weiboDate
			mat = pat.matcher(urlHtml);
			if (mat.find()) {
				String dateStr = mat.group()
						.replaceAll("^.*?\"", "").replaceAll("\\\\\"$", "");
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
}
