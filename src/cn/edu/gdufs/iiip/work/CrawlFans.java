package cn.edu.gdufs.iiip.work;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.gdufs.iiip.crawler.Crawl;
import cn.edu.gdufs.iiip.login.Cookie;

public class CrawlFans {
	public String getFansuid(String html, String path) throws IOException{
		String filepath = "E:/weibofans/";//文件保存的路径
		(new File(filepath)).mkdirs();
		FileOutputStream fout = null;
	    BufferedOutputStream bout = null;
	    OutputStreamWriter output = null;
	    fout = new FileOutputStream(filepath+path, true);
	    bout = new BufferedOutputStream(fout);
	    output = new OutputStreamWriter(bout);
	        
		String str="";
		Matcher mat1 = Pattern.compile("follow_inner.*?WB_cardpage S_line1").matcher(html);
		if(mat1.find()){
			str=mat1.group();
			Matcher mat2 = Pattern.compile("uid=.*?&nick").matcher(str);
			while(mat2.find()){
				System.out.println(mat2.group().replaceAll("&fnick.*?&nick", "").replaceAll("uid=", ""));
				output.write(mat2.group().replaceAll("&fnick.*?&nick", "").replaceAll("uid=", ""));
				output.write("\r\n");
			}
			output.flush();
			output.close();
		}
		return null;
		
	}
	public static void main(String[] args) throws IOException {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("MM-dd");	
		System.out.println(df.format(date));
		CrawlFans  cf = new CrawlFans();
		Cookie.login("gwcrawler20@163.com","321654");
		Crawl c = new Crawl();
//		for(int i =1;i<=5;i++){
//			String str = "";
//			String url = "http://weibo.com/p/1002061988800805/follow?relate=fans&page="+1;	
//			str=c.crawl(url).toString();
//			System.out.println(str);
//			cf.getFansuid(string,"凤凰财经粉丝"+df.format(date).toString()+"uid.txt");
//		}
//		for(int i =1;i<=5;i++){
//			String string = "";
//			String url = "http://weibo.com/p/1006062683843043/follow?relate=fans&page="+i;	
//			string = c.crawl(url, Cookie.getCookie()).toString();
//			cf.getFansuid(string,"魅族科技粉丝"+df.format(date).toString()+"uid.txt");
//		}
//		for(int i =1;i<=5;i++){
//			String string = "";
//			String url = "http://weibo.com/p/1002061216431741/follow?relate=fans&page="+i;	
//			string = c.crawl(url, Cookie.getCookie()).toString();
//			cf.getFansuid(string,"南都娱乐周刊粉丝"+df.format(date).toString()+"uid.txt");
//		}
//		for(int i =1;i<=5;i++){
//			String string = "";
//			String url = "http://weibo.com/p/1005055466594184/follow?relate=fans&page="+i;	
//			string = c.crawl(url, Cookie.getCookie()).toString();
//			cf.getFansuid(string,"全球娱乐趣事粉丝"+df.format(date).toString()+"uid.txt");
//		}
//		for(int i =1;i<=5;i++){
//			String string = "";
//			String url = "http://weibo.com/p/1002061644088831/follow?relate=fans&page="+i;	
//			string = c.crawl(url, Cookie.getCookie()).toString();
//			cf.getFansuid(string,"新浪汽车粉丝"+df.format(date).toString()+"uid.txt");
//		}
//		for(int i =1;i<=5;i++){
//			String string = "";
//			String url = "http://weibo.com/p/1002061638781994/follow?relate=fans&page="+i;	
//			string = c.crawl(url, Cookie.getCookie()).toString();
//			cf.getFansuid(string,"新浪体育粉丝"+df.format(date).toString()+"uid.txt");
//		}

	}

}
