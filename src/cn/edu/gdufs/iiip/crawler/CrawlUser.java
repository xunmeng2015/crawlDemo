package cn.edu.gdufs.iiip.crawler;

import java.util.*;
import java.util.regex.*;

import cn.edu.gdufs.iiip.entity.*;
import cn.edu.gdufs.iiip.login.Cookie;
import cn.edu.gdufs.iiip.util.*;

/*爬取一个用户的个人信息*/
//多种匹配的网页形式，尚未统一！！
public class CrawlUser {
	private String uid;//用户uid
	
	public String getUid() {return uid;}
	public void setUid(String uid) {this.uid = uid;}
	
	public CrawlUser(String uid) {
		this.uid = uid;
	}
	
	public static void main(String[] args) {
		Cookie.login();
		CrawlUser cu = new CrawlUser("1035933493");
		User u = cu.getUser();
		System.out.println(u);
	}
	
	//获取用户信息
	public User getUser() {
		String url = "http://weibo.com/u/" + uid;
		Crawl c = new Crawl();
		String html = c.crawl(url);
//		IO.print(html, "D:/1.html");
//		System.out.println("get user "+this.uid);
		return matchUser(html);
	}
	
	//匹配用户信息
	private User matchUser(String html) {
		String uid, nickName,introduction,gender,location,school,company,imgurl,birthday;
		int followCount, fanCount, weiboCount;
		Set<String> label = new HashSet<String>();
		uid="";nickName="";introduction="";gender="";
		location="";school="";company="";imgurl="";birthday="";
		followCount=fanCount=weiboCount=-1;
		
		Pattern pat = Pattern.compile("<div\\s+class=\\\\\"user_atten.*?<\\\\/div>");
		Matcher mat = pat.matcher(html);
		if (mat.find()) {
			/*普通用户可用但特殊用户不可用
			String attenHtml = mat.group();
			pat = Pattern.compile("<strong\\s+class=\\\\\"\\\\\">"
					+ ".*?<\\\\/strong>");//followCount
			mat = pat.matcher(attenHtml);
			if (mat.find()) followCount=Integer.parseInt(
					mat.group().replaceAll("^.*?>", "")
					.replaceAll("<.*?$", ""));
			if (mat.find()) fanCount=Integer.parseInt(//fanCount
					mat.group().replaceAll("^.*?>", "")
					.replaceAll("<.*?$", ""));
			pat = Pattern.compile("\\\\\"weibo\\\\\">"
					+ ".*?<\\\\/strong");//weiboCount
			mat = pat.matcher(attenHtml);
			if (mat.find()) weiboCount=Integer.parseInt(
					mat.group().replaceAll("^.*?>", "")
					.replaceAll("<.*?$", ""));
			*/
			String attenHtml = mat.group();
			pat = Pattern.compile("<strong\\s+class=\\\\\"\\\\\">"
					+ ".*?<\\\\/strong>");//followCount
			mat = pat.matcher(attenHtml);
			if (mat.find()) followCount=Integer.parseInt(
					mat.group().replaceAll("^.*?>", "")
					.replaceAll("<.*?$", ""));
			if (mat.find()) fanCount=Integer.parseInt(//fanCount
					mat.group().replaceAll("^.*?>", "")
					.replaceAll("<.*?$", ""));
			pat = Pattern.compile("\\\\\"weibo\\\\\">"
					+ ".*?<\\\\/strong");//weiboCount
			mat = pat.matcher(attenHtml);
			if (mat.find()) weiboCount=Integer.parseInt(
					mat.group().replaceAll("^.*?>", "")
					.replaceAll("<.*?$", ""));
		}
		
		int start=0, end=0;
//		pat = Pattern.compile("<div\\s+class=\\\\\"pf_info W_fl");//微博认证蓝
		pat = Pattern.compile("<div\\s+class=\\\\\"pf_info_left_border");//微博认证红
		mat = pat.matcher(html);
		if (mat.find()) start = mat.start();
//		pat = Pattern.compile("<div\\s+class=\\\\\"pf_info_identity");//微博认证蓝
		pat = Pattern.compile("<div\\s+class=\\\\\"pf_info_right");//微博认证红
		mat = pat.matcher(html);
		if (mat.find()) end = mat.start();
		html = html.substring(start, end);
		
		pat = Pattern.compile("<div\\s+class=\\\\\"tags.*?<\\\\/div>");
		mat = pat.matcher(html);
		if (mat.find()) {
			String tag = mat.group();
			pat = Pattern.compile("loc=infgender.*?><.*?>");//gender
			mat = pat.matcher(tag);
			if (mat.find()) {
				gender = mat.group();
				pat = Pattern.compile("title=\\\\\".*?\\\\\"");
				mat = pat.matcher(gender);
				mat.find();
				gender = mat.group().replace("title=","").replace("\\\"","");
			}
			pat = Pattern.compile("loc=infplace.*?>.*?<");//location
			mat = pat.matcher(tag);
			if (mat.find()) {
				location = mat.group();
				pat = Pattern.compile("title=\\\\\".*?\\\\\"");
				mat = pat.matcher(location);
				mat.find();
				location = mat.group().replace("title=","").replace("\\\"","");
			}
			pat = Pattern.compile("loc=infedu.*?>.*?<");//school
			mat = pat.matcher(tag);
			if (mat.find()) {
				school = mat.group();
				pat = Pattern.compile("title=\\\\\".*?\\\\\"");
				mat = pat.matcher(school);
				mat.find();
				school = mat.group().replace("title=","").replace("\\\"","");
			}
			pat = Pattern.compile("loc=infjob.*?>.*?<");//company
			mat = pat.matcher(tag);
			if (mat.find()) {
				company = mat.group();
				pat = Pattern.compile("title=\\\\\".*?\\\\\"");
				mat = pat.matcher(company);
				mat.find();
				company = mat.group().replace("title=","").replace("\\\"","");
			}
		}
		
		pat = Pattern.compile("id=.*?\\\\\"");//uid
		mat = pat.matcher(html);
		if (mat.find()) uid = mat.group()
				.replaceFirst("^.*?=", "").replaceFirst("\\\\\"$", "");
		
		
		//nickName
//		pat = Pattern.compile("<span\\s+class=\\\\\"name\\\\\">"//for normal users
//				+ ".*?<\\\\/span>");//nickName
//		mat = pat.matcher(html);
//		if (mat.find()) nickName = Decode
//				.expressionCharacter(mat.group()).trim();
//		pat = Pattern.compile("<div\\s+class=\\\\\"username\\\\\">.*?<\\\\/strong>");//微博认证蓝
//		mat = pat.matcher(html);
//		if (mat.find()) {
//			nickName = Decode.expressionCharacter(mat.group()).trim();
//		}
		pat = Pattern.compile("<span\\s+class=\\\\\"name\\\\\">.*?<\\\\/span>");//微博认证红
		mat = pat.matcher(html);
		if (mat.find()) {
			nickName = Decode.expressionCharacter(mat.group()).trim();
		}
		
		pat = Pattern.compile("<div\\s+class=\\\\\"pf_intro"
				+ ".*?<\\\\/div>");//introduction
		mat = pat.matcher(html);
		if (mat.find()) introduction = Decode
				.expressionCharacter(mat.group()).trim();
		
		pat = Pattern.compile("<div\\s+class=\\\\\""
				+ "layer_menulist_tags.*?<\\\\/div>");//label
		mat = pat.matcher(html);
		if (mat.find()) {
			String labelHtml = mat.group().replace("标签：", "");
			labelHtml = Decode.expressionCharacter(labelHtml);
			String labels[] = labelHtml.split(" ");
			for (String str: labels) label.add(str);
		}
		
		return new User(uid,nickName,followCount,fanCount,weiboCount,
				introduction,label,gender,location,school,company,imgurl,birthday);
	}
}

//TODO:
//正则表达式匹配有bug，平常用户和一些媒体用户网页规则不一样