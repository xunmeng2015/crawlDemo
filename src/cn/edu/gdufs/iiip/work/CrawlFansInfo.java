package cn.edu.gdufs.iiip.work;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.gdufs.iiip.crawler.Crawl;
import cn.edu.gdufs.iiip.entity.*;
import cn.edu.gdufs.iiip.login.Cookie;
import cn.edu.gdufs.iiip.util.*;

/*爬取一个用户的个人信息*/
//多种匹配的网页形式，尚未统一！！
public class CrawlFansInfo {
	private String uid;//用户uid
	
	public String getUid() {return uid;}
	public void setUid(String uid) {this.uid = uid;}
	
	public CrawlFansInfo(String uid) {
		this.uid = uid;
	}
	
	public static void main(String[] args) {
		Cookie.login("niketim@163.com","321456");
		CrawlFansInfo cu = new CrawlFansInfo("2677424891");
		User u = cu.getUser();
		System.out.println(u);
	}
	
	//获取用户信息
	public User getUser() {
		String url = "http://weibo.com/u/" + uid;
		String page_id="";
		Crawl c = new Crawl();
		String html = c.crawl(url);
//		System.out.println(html);
		Pattern pat = Pattern.compile("page_id']='.*?'");
		Matcher mat = pat.matcher(html);
		if(mat.find()){
			page_id=mat.group().replace("page_id']='", "").replace("'", "");
		}
		url="http://weibo.com/p/"+page_id+"/info?mod=pedit_more";
		html=c.crawl(url);
		IO.print(html, "E:/1.txt");
//		System.out.println(html);
		String info = null;
		pat = Pattern.compile("基本信息.*?</script>");
		mat = pat.matcher(html);
		if(mat.find()){
//			System.out.println(mat.group());
			info=mat.group();
		}
		pat = Pattern.compile("PCD_counter.*?</script>");
		mat = pat.matcher(html);
		if(mat.find()){
//			System.out.println(mat.group());
			info=info+mat.group();
		}
		pat = Pattern.compile("photo_wrap.*?photo");
		mat = pat.matcher(html);
		if(mat.find()){
//			System.out.println(Decode.expressionCharacter(mat.group()));
			info=info+Decode.expressionCharacter(mat.group());
		}
//		IO.print(html, "D:/1.html");
//		System.out.println("get user "+this.uid);
		System.out.println(info);
		return matchUser(info);
	}
	
	//匹配用户信息
	private User matchUser(String html) {
		String uid, nickName,introduction,gender,location,school,company,imgurl,birthday;
		int followCount, fanCount, weiboCount;
		Set<String> label = new HashSet<String>();
		uid="";nickName="";introduction="";gender="";
		location="";school="";company="";imgurl="";birthday="";
		followCount=fanCount=weiboCount=-1;
		
//		Pattern pat = Pattern.compile("<div\\s+class=\\\\\"user_atten.*?<\\\\/div>");
//		Pattern pat = Pattern.compile("PCD_counter\">.*?标签");
//		Matcher mat = pat.matcher(html);
//		if (mat.find()) {
			/**///普通用户可用但特殊用户不可用
//			System.out.println(mat.group());
//			String attenHtml = mat.group();
		Pattern pat = Pattern.compile("<strong"
					+ ".*?<\\\\/strong>");//followCount
		Matcher mat = pat.matcher(html);
			if (mat.find()) followCount=Integer.parseInt(
					mat.group().replaceAll("^.*?>", "")
					.replaceAll("<.*?$", ""));
			if (mat.find()) fanCount=Integer.parseInt(//fanCount
					mat.group().replaceAll("^.*?>", "")
					.replaceAll("<.*?$", ""));
//			pat = Pattern.compile("\\\\\"weibo\\\\\">"
//					+ ".*?<\\\\/strong");//weiboCount
//			mat = pat.matcher(html);
			if (mat.find()) weiboCount=Integer.parseInt(
					mat.group().replaceAll("^.*?>", "")
					.replaceAll("<.*?$", ""));
			
//			String attenHtml = mat.group();
//			pat = Pattern.compile("<strong\\s+class=\\\\\"\\\\\">"
//					+ ".*?<\\\\/strong>");//followCount
//			mat = pat.matcher(attenHtml);
//			if (mat.find()) followCount=Integer.parseInt(
//					mat.group().replaceAll("^.*?>", "")
//					.replaceAll("<.*?$", ""));
//			if (mat.find()) fanCount=Integer.parseInt(//fanCount
//					mat.group().replaceAll("^.*?>", "")
//					.replaceAll("<.*?$", ""));
//			pat = Pattern.compile("\\\\\"weibo\\\\\">"
//					+ ".*?<\\\\/strong");//weiboCount
//			mat = pat.matcher(attenHtml);
//			if (mat.find()) weiboCount=Integer.parseInt(
//					mat.group().replaceAll("^.*?>", "")
//					.replaceAll("<.*?$", ""));
//		}
		
//		int start=0, end=0;
////		pat = Pattern.compile("<div\\s+class=\\\\\"pf_info W_fl");//微博认证蓝
//		pat = Pattern.compile("<div\\s+class=\\\\\"pf_info_left_border");//微博认证红
//		mat = pat.matcher(html);
//		if (mat.find()) start = mat.start();
////		pat = Pattern.compile("<div\\s+class=\\\\\"pf_info_identity");//微博认证蓝
//		pat = Pattern.compile("<div\\s+class=\\\\\"pf_info_right");//微博认证红
//		mat = pat.matcher(html);
//		if (mat.find()) end = mat.start();
//		html = html.substring(start, end);
		
//		pat = Pattern.compile("<div\\s+class=\\\\\"tags.*?<\\\\/div>");
//		mat = pat.matcher(html);
//		if (mat.find()) {
//			String tag = mat.group();
			pat = Pattern.compile("性别：<\\\\/span>.*?span>");//gender
			mat = pat.matcher(html);
			if (mat.find()) {
//				gender = mat.group();
//				pat = Pattern.compile("title=\\\\\".*?\\\\\"");
//				mat = pat.matcher(gender);
//				mat.find();
				gender = mat.group().replaceAll("<.*?>", "").replace("性别：", "");
			}
			pat = Pattern.compile("所在地：<\\\\/span>.*?span>");//location
			mat = pat.matcher(html);
			if (mat.find()) {
				location = mat.group().replaceAll("<.*?>", "").replace("所在地：", "");
//				pat = Pattern.compile("title=\\\\\".*?\\\\\"");
//				mat = pat.matcher(location);
//				mat.find();
//				location = mat.group().replace("title=","").replace("\\\"","");
			}
			pat = Pattern.compile("loc=infedu.*?>.*?<");//school
			mat = pat.matcher(html);
			if (mat.find()) {
				school = mat.group().replace("loc=infedu\\\">", "").replace("<", "");
//				System.out.println(school);
//				pat = Pattern.compile("title=\\\\\".*?\\\\\"");
//				mat = pat.matcher(school);
//				mat.find();
//				school = mat.group().replace("title=","").replace("\\\"","");
			}
			pat = Pattern.compile("loc=infjob.*?>.*?<");//company
			mat = pat.matcher(html);
			if (mat.find()) {
				company = mat.group().replace("loc=infjob\\\">", "").replace("<", "");
//				pat = Pattern.compile("title=\\\\\".*?\\\\\"");
//				mat = pat.matcher(company);
//				mat.find();
//				company = mat.group().replace("title=","").replace("\\\"","");
			}
//		}
		uid = this.uid;
//		pat = Pattern.compile("id=.*?\\\\\"");//uid
//		mat = pat.matcher(html);
//		if (mat.find()) uid = mat.group()
//				.replaceFirst("^.*?=", "").replaceFirst("\\\\\"$", "");
		
		//nickName
		pat = Pattern.compile("昵称：<.*?<\\\\/span>");//nickName
		mat = pat.matcher(html);
		if (mat.find()) {
			nickName = Decode.expressionCharacter(mat.group()).replace("昵称：", "").trim();

		}
		pat = Pattern.compile("生日：<.*?<\\\\/span>");//birthday
		mat = pat.matcher(html);
		if (mat.find()) {
			birthday = Decode.expressionCharacter(mat.group()).replace("生日：", "").trim();
			birthday=birthday.replace("年", ".").replace("月", ".").replace("日", "");

		}
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
		
		pat = Pattern.compile("简介：<\\\\/span>"
				+ ".*?span>");//introduction
		mat = pat.matcher(html);
		if (mat.find()) introduction = Decode
				.expressionCharacter(mat.group()).trim().replace("简介：", "");
		
		pat = Pattern.compile("标签：.*?</script>");//label
		mat = pat.matcher(html);
		if (mat.find()) {
			String labelHtml = mat.group().replace("标签：", "");
			labelHtml = Decode.expressionCharacter(labelHtml).replaceAll("\"", "").replaceAll("\\)", "").replaceAll("}", "");
			String labels[] = labelHtml.split(" ");
			for (String str: labels) label.add(str);
		}
		pat = Pattern.compile("<img src=.*?photo");//imgurl
		mat = pat.matcher(html);
		if (mat.find()) {
			imgurl=mat.group();
			pat = Pattern.compile("\".*?\"");//imgurl
			mat = pat.matcher(imgurl);
			if(mat.find()){
				imgurl=mat.group().replaceAll("\"", "");
			}
		}
		
		return new User(uid,nickName,followCount,fanCount,weiboCount,
				introduction,label,gender,location,school,company,imgurl,birthday);
	}
}

//TODO:
//正则表达式匹配有bug，平常用户和一些媒体用户网页规则不一样