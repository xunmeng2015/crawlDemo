package cn.edu.gdufs.iiip.crawler;

import java.util.*;
import java.util.regex.*;

import cn.edu.gdufs.iiip.entity.*;
import cn.edu.gdufs.iiip.util.*;

/*爬取一条微博的转发*/
public class CrawlRepost {
	private String mid;//微博mid
	
	public String getMid() {return mid;}
	public void setMid(String mid) {this.mid = mid;}
	
	public CrawlRepost(String mid) {
		this.mid = mid;
	}
	
	//获取微博转发
	public List<Repost> getRepost() {
		List<Repost> repostList = new ArrayList<Repost>();
		String html = crawlPage(1);
		repostList.addAll(matchPageRepost(html));
		int pageNum = matchPageNum(html);
		System.out.println("get 1/"+pageNum);
		if (pageNum > 1000) pageNum = 1000;//太多页受不了
		for (int i=2; i<=pageNum; ++i) {
			try {Thread.sleep(1000);} catch (Exception ex) {}
			html = crawlPage(i);
			System.out.println("get "+i+"/"+pageNum);
			List<Repost> pageRepost = matchPageRepost(html);
			repostList.addAll(pageRepost);
		}
		return repostList;
	}
	
	//爬取一页转发
	private String crawlPage(int page) {
		String url = "http://weibo.com/aj/mblog/info/big?&id="
				+this.mid+"&page="+page;
		return (new Crawl()).crawl(url);
	}
	
	//匹配评论页数
	private int matchPageNum(String html) {
		Pattern pat = Pattern.compile("totalpage\":\\d+");
		Matcher mat = pat.matcher(html);
		if (mat.find()) {
			html = mat.group().replace("totalpage\":", "");
			return Integer.parseInt(html);
		}
		else return 1;
	}
	
	//匹配网页转发
	private List<Repost> matchPageRepost(String html) {
		List<Repost> pageRepost = new ArrayList<Repost>();
		int[] pos = new int[107];
		int num = 0;
		Pattern pat = Pattern
				.compile("<dl\\s+class=\\\\\"comment_list\\s+S_line1");
		Matcher mat = pat.matcher(html);
		while (mat.find()) pos[num++] = mat.start();
		for (int i=0; i<num-1; ++i) {
			Repost repost= matchRepost(html.substring(pos[i], pos[i+1]));
			if (repost != null) pageRepost.add(repost);
		}
		if (num > 0) {
			Repost repost= matchRepost(html
					.substring(pos[num-1], html.length()));
			if (repost != null) pageRepost.add(repost);
		}
		return pageRepost;
	}
	
	//匹配一条转发
	private Repost matchRepost(String html) {
		String mid, uid, text;
		mid="";uid="";text="";
		Pattern pat = Pattern.compile("mid=\\\\\".*?\\\\\"");
		Matcher mat = pat.matcher(html);
		if (mat.find()) mid = mat.group()
				.replaceFirst("^.*?\"", "").replaceFirst("\\\\\"$", "");
		pat = Pattern.compile("usercard=\\\\\"id=.*?\\\\\"");
		mat = pat.matcher(html);
		if (mat.find()) uid = mat.group()
				.replaceFirst("^.*?id=", "").replaceFirst("\\\\\"$", "");
		pat = Pattern.compile("<em.*?<\\\\/em>");
		mat = pat.matcher(html);
		if (mat.find()) text = Decode.expressionCharacter(mat.group());
		return new Repost(mid, uid, text);
	}
}
