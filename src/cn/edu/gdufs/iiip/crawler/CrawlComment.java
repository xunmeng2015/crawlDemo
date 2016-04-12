package cn.edu.gdufs.iiip.crawler;

import java.util.*;
import java.util.regex.*;

import cn.edu.gdufs.iiip.entity.*;
import cn.edu.gdufs.iiip.util.*;

/*爬取一条微博的评论*/
public class CrawlComment {
	private String mid;//微博mid
	
	public String getMid() {return mid;}
	public void setMid(String mid) {this.mid = mid;}
	
	public CrawlComment(String mid) {
		this.mid = mid;
	}
	
	//获取微博评论
	public List<Comment> getComment() {
		List<Comment> commentList = new ArrayList<Comment>();
		String html = crawlPage(1);
		commentList.addAll(matchPageComment(html));
		int pageNum = matchPageNum(html);
		System.out.println("get 1/"+pageNum);
		if (pageNum > 1000) pageNum = 1000;//太多页受不了
		for (int i=2; i<=pageNum; ++i) {
			try {Thread.sleep(1000);} catch (Exception ex) {}
			html = crawlPage(i);
			System.out.println("get "+i+"/"+pageNum);
			List<Comment> pageComment = matchPageComment(html);
			commentList.addAll(pageComment);
		}
		return commentList;
	}
	
	//爬取一页评论
	private String crawlPage(int page) {
		String url = "http://weibo.com/aj/comment/big?&id="
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
	
	//匹配网页评论
	private List<Comment> matchPageComment(String html) {
		List<Comment> pageComment = new ArrayList<Comment>();
		int[] pos = new int[107];
		int num = 0;
		Pattern pat = Pattern.compile("<dl\\s+class=\\\\\"comment_list");
		Matcher mat = pat.matcher(html);
		while (mat.find()) pos[num++] = mat.start();
		for (int i=0; i<num-1; ++i) {
			Comment comment= matchComment(html.substring(pos[i], pos[i+1]));
			if (comment != null) pageComment.add(comment);
		}
		if (num > 0) {
			Comment comment= matchComment(
					html.substring(pos[num-1], html.length()));
			if (comment != null) pageComment.add(comment);
		}
		return pageComment;
	}
	
	//匹配一条评论
	private Comment matchComment(String html) {
		String uid, text;
		uid="";text="";
		Pattern pat = Pattern.compile("<dd>.*?<\\\\/span>");
		Matcher mat = pat.matcher(html);
		if (mat.find()) html = mat.group();
		pat = Pattern.compile("usercard=\\\\\"id=.*?\\\\\"");
		mat = pat.matcher(html);
		if (mat.find()) uid = mat.group()
				.replaceFirst("^.*?id=", "")
				.replaceFirst("\\\\\"$", "");
		text = html.replaceAll("<a.*?<\\\\/a>", "")
				.replaceAll("<span.*?<\\\\/span>", "")
				.replaceFirst("^.*?：", "")
				.replaceFirst("<.*?>", "");
		text = Decode.expressionCharacter(text);
		return new Comment(uid, text);
	}
}
