package cn.edu.gdufs.iiip.util;

import java.io.*;
import java.util.*;

import cn.edu.gdufs.iiip.entity.*;

import org.apache.http.client.CookieStore;

/*读写文件*/
public class IO {
	private static String charset = "UTF-8";
	
	//字符串输出到文件
	public static void print(String content, String path) {
		try {
			PrintWriter pw = new PrintWriter(new File(path), charset);
			pw.print(content);
			pw.close();
		} catch (Exception ex) {}
	}
	
	//字符串输出追加到文件
	public static void append(String content, String path) {
		try {
			PrintWriter pw = new PrintWriter(
					new FileWriter(new File(path), true));
			pw.append(content);
			pw.close();
		} catch (Exception ex) {}
	}
	 
	//字符串数组输出到文件
	public static void print(List<String> list, String path) {
		try {
			PrintWriter pw = new PrintWriter(new File(path), charset);
			for (String line: list) pw.println(line);
			pw.close();
		} catch (Exception ex) {}
	}
	//字符串数组追加到文件
		public static void append(List<String> list, String path) {
			try {
				PrintWriter pw = new PrintWriter(
						new FileWriter(new File(path), true));
				for (String line: list) pw.println(line);
				pw.close();
			} catch (Exception ex) {}
		}
		 
	
	//将CookieStore保存到文件
	public static void print(CookieStore cookie, String path) {
		try {
		    ObjectOutputStream oos =  new ObjectOutputStream(
		    		new FileOutputStream(new File(path)));
		    oos.writeObject(cookie);
		    oos.close();
		} catch (Exception ex) {ex.printStackTrace();}
	}
	
	//输出Weibo数组
	public static void printWeibo(List<Weibo> list, String path) {
		try {
			PrintWriter pw = new PrintWriter(new File(path), charset);
			for (Weibo weibo: list) {
				pw.println(weibo);
			}
			pw.close();
		} catch (Exception ex) {}
	}
	
	//输出User数组
	public static void printUser(List<User> list, String path) {
		try {
			PrintWriter pw = new PrintWriter(new File(path), charset);
			for (User user: list) {
				pw.println(user);
			}
			pw.close();
		} catch (Exception ex) {}
	}
	
	//输出User数组的uid
	public static void printUid(List<User> list, String path) {
		try {
			PrintWriter pw = new PrintWriter(new File(path), charset);
			for (int i=0; i<list.size(); ++i) {
				pw.println(list.get(i).getUid());
			}
			pw.close();
		} catch (Exception ex) {}
	}
	
	//输出Comment数组
	public static void printComment(List<Comment> list, String path) {
		try {
			PrintWriter pw = new PrintWriter(new File(path), charset);
			for (Comment comment: list) {
				pw.println(comment);
			}
			pw.close();
		} catch (Exception ex) {}
	}
	
	//输出Repost数组
	public static void printRepost(List<Repost> list, String path) {
		try {
			PrintWriter pw = new PrintWriter(new File(path), charset);
			for (Repost repost: list) {
				pw.println(repost);
			}
			pw.close();
		} catch (Exception ex) {}
	}
	
	//从文件读取内容
	public static String read(String path) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(new File(path)));
			String line;
			while((line = reader.readLine()) != null) sb.append(line+"\n");
			reader.close();
		} catch (Exception ex) {}
		return sb.toString();
	}
	
	//从文件读取一行内容
	public static String readLine(String path) {
		String line = "";
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(new File(path)));
			line = reader.readLine();
			reader.close();
		} catch (Exception ex) {}
		return line; 
	}
	
	//读取字符串列表
	public static List<String> readList(String path) {
		List<String> idList = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(new File(path)));
			String line;
			while((line = reader.readLine()) != null) {
				if (line.replaceAll("\\s+","").length() > 0) idList.add(line);
			}
			reader.close();
		} catch (Exception ex) {}
		return idList;
	}
	
	//读取CookieStore对象
	public static CookieStore readCookieStore(String path) {
		CookieStore cookie = null;
		try {
			ObjectInputStream ois =  new ObjectInputStream(
					new FileInputStream(new File(path)));
			cookie = (CookieStore)ois.readObject();
			ois.close();
		} catch (Exception ex) {}
		return cookie;
	}
	
	//读取weibo数组
	public static List<Weibo> readWeibo(String path) {
		String texts = read(path);
		List<Weibo> weibos = new ArrayList<>();
		List<String> weiboStrs = Decode.matchFields(texts, "weibo");
		for (String weiboStr: weiboStrs) {
			String mid = Decode.matchField(weiboStr, "mid");
			String uid = Decode.matchField(weiboStr, "uid");
			String ruid = Decode.matchField(weiboStr, "ruid");
			String time = Decode.matchField(weiboStr, "time");
			String text = Decode.matchField(weiboStr, "text");
			String originText = Decode.matchField(weiboStr, "originText");
			String dateStr = Decode.matchField(weiboStr, "weiboDate");
			Date weiboDate = new Date(Long.parseLong(dateStr));
			String cstr = Decode.matchField(weiboStr, "commentCount");
			int commentCount = Integer.parseInt(cstr);
			String rstr = Decode.matchField(weiboStr, "repostCount");
			int repostCount = Integer.parseInt(rstr);
			String zstr = Decode.matchField(weiboStr, "zanCount");
			int zanCount = Integer.parseInt(zstr);
			String source = Decode.matchField(weiboStr, "source");
			String url = Decode.matchField(weiboStr, "url");
			Weibo weibo = new Weibo(mid,uid,ruid,time,text,originText,weiboDate,
					commentCount,repostCount,zanCount,source,url);
			weibos.add(weibo);
		}
		return weibos;
	}
	
	//读取repost数组
	public static List<Repost> readRepost(String path) {
		String texts = read(path);
		List<Repost> reposts = new ArrayList<>();
		List<String> repostStrs = Decode.matchFields(texts, "repost");
		for (String repostStr: repostStrs) {
			String mid = Decode.matchField(repostStr, "mid");
			String uid = Decode.matchField(repostStr, "uid");
			String text = Decode.matchField(repostStr, "text");
			Repost repost = new Repost(mid,uid,text);
			reposts.add(repost);
		}
		return reposts;
	}
	
	//读取comment数组
	public static List<Comment> readComment(String path) {
		String texts = read(path);
		List<Comment> comments = new ArrayList<>();
		List<String> commentStrs = Decode.matchFields(texts, "comment");
		for (String commentStr: commentStrs) {
			String uid = Decode.matchField(commentStr, "uid");
			String text = Decode.matchField(commentStr, "text");
			Comment comment = new Comment(uid,text);
			comments.add(comment);
		}
		return comments;
	}
}
