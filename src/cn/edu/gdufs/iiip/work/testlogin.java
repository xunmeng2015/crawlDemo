package cn.edu.gdufs.iiip.work;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.impl.conn.tsccm.WaitingThread;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import cn.edu.gdufs.iiip.crawler.Crawl;
import cn.edu.gdufs.iiip.login.Cookie;

public class testlogin {
public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException ,UnsupportedEncodingException{
//	Date date = new Date();
//	int hour = date.get;
//	System.out.println(hour);
//	System
//	.exit(0);
	Connection con=null;
//	String mysqlUrl="jdbc:mysql://192.168.235.36:3306/lime?"
//			+ "user=lime&password=lime&useUnicode=true&characterEncoding=UTF8";
	String mysqlUrl="jdbc:mysql://localhost:3306/baseinformation?"
			+ "user=root&password=553352836w&useUnicode=true&characterEncoding=UTF8";
	Class.forName("com.mysql.jdbc.Driver");
	System.out.println("成功加载MySQL驱动");
	con=(Connection) DriverManager.getConnection(mysqlUrl);
	String sql;
	Statement sta;
	sta = (Statement) con.createStatement();
	ResultSet re = null;
	Cookie.login("gwcrawler20@163.com","321654");
//	String url="http://s.weibo.com/weibo/%25E5%25B1%25B1%25E4%25B8%259C%25E7%2596%25AB%25E8%258B%2597%25E4%25BA%258B%25E4%25BB%25B6&page=1";
	String url="http://s.weibo.com/weibo/%2523%25E5%25A5%25B3%25E5%25A4%25A7%25E5%25AD%25A6%25E7%2594%259F%25E5%25A4%25B1%25E8%25B8%25AA%2523&nodup=1&page=1";
	Crawl c=new Crawl();
//	c.crawl(url);
	String html=c.crawl(url);
//	System.out.println(html);System.exit(0);
	int pageNum;
	Pattern num=Pattern.compile(">第\\d*页<\\\\/a><\\\\/li><\\\\/ul><\\\\/div>");
	Matcher matt=num.matcher(html);
	matt.find();
//	System.out.println(matt.group());
	pageNum=Integer.parseInt(matt.group().replaceAll("<.*?>", "").replaceAll("第|页", "").replaceAll(">", ""));
	System.out.println(pageNum);
	for (int page=1;page<=pageNum;page++){
		String url1="http://s.weibo.com/weibo/%2523%25E5%25A5%25B3%25E5%25A4%25A7%25E5%25AD%25A6%25E7%2594%259F%25E5%25A4%25B1%25E8%25B8%25AA%2523&nodup=1&page="+page;
		html=c.crawl(url1);
//	System.out.println(html);
//	System.exit(0);
	String blog="(<p\\sclass=\\\\\"comment_txt).*?(?=\\/feed_action-->)";
	Pattern pat=Pattern.compile(blog);
	Matcher mat=pat.matcher(html);
	while(mat.find()){
//		System.out.println(mat.group());
		String code = mat.group();
		String name="(?<=nick-name=\\\\\").*?(?=\\\\\")";
		Pattern pat1=Pattern.compile(name);
		Matcher mat1=pat1.matcher(code);
//		while (mat1.find()){
//			System.out.print(mat1.group()+"		");
//		}
		mat1.find();	String namee=mat1.group();
		String uid="(?<=uid=).*?(?=&)";
		Pattern pat2=Pattern.compile(uid);
		Matcher mat2=pat2.matcher(code);
		mat2.find();	String uidd=mat2.group();
//		while (mat2.find()){
//			System.out.print(mat2.group()+"		");
//		}
		String mid="(?<=mid=).*?(?=&name=)";
		Pattern pat3=Pattern.compile(mid);
		Matcher mat3=pat3.matcher(code);
		mat3.find();	String midd=mat3.group().replaceAll("&.*", "").replaceAll("\\\\\".*", "");
//		while (mat3.find()){
//			System.out.print(mat3.group().replaceAll("&.*", "").replaceAll("\\\\\".*", "")+"		");
//		}
		String text="(<p\\sclass=\\\\\"comment_txt).*?(?=(<div|<\\/div))";
		Pattern pat4=Pattern.compile(text);
		Matcher mat4=pat4.matcher(code);
		mat4.find();	String textt=mat4.group().replaceAll("<.*?>", "").replaceAll("n|t|http.*|\\\\", "");
//		while (mat4.find()){
//			System.out.print(mat4.group().replaceAll("<.*?>", "").replaceAll("n|t|http.*|\\\\", "")+"		");
//		}
		String oriText="(?<=<span class=\\\\\"S_txt2\\\\\">).*?(?=<div class=\\\\\"WB_feed_spec_row)";
		Pattern pat5=Pattern.compile(oriText);
		Matcher mat5=pat5.matcher(code);
		String oriTextt;
		if(mat5.find())	oriTextt=mat5.group().replaceAll("<.*", "");
		else oriTextt=null;
//		while (mat5.find()){
//			System.out.print(mat5.group().replaceAll("<.*", "")+"		");
//		}
		String time="(?<=target=\\\\\"_blank\\\\\" title=\\\\\")\\d.*?(?=date=)";
		Pattern pat6=Pattern.compile(time);
		Matcher mat6=pat6.matcher(code);
		mat6.find();	String timee=mat6.group().replaceAll("\\\\.*", "");
//		while (mat6.find()){
//			System.out.print(mat6.group().replaceAll("\\\\.*", "")+"		");
//		}
		String zhuanfa="(?<=转发<em>).*?(?=<\\\\/em)";
		Pattern pat7=Pattern.compile(zhuanfa);
		Matcher mat7=pat7.matcher(code);
		String zhuanfaa;
		mat7.find();	
		if(mat7.group().equals("")) zhuanfaa="0";
		else zhuanfaa=mat7.group();
//		while (mat7.find()){
//			if(mat7.group().equals(""))	System.out.print("0"+"		");
//			else  System.out.print(mat7.group()+"		");
//		}
		String comment="(?<=评论<em>).*?(?=<\\\\/em)";
		Pattern pat8=Pattern.compile(comment);
		Matcher mat8=pat8.matcher(code);
		String commentt;
		if (mat8.find()){
//			System.out.print(mat8.group()+"		");
			commentt=mat8.group();
		}
		else 	commentt="0";
		
		String zan="(?<=<\\\\/i><em>).*?(?=<\\\\/em>)";
		Pattern pat9=Pattern.compile(zan);
		Matcher mat9=pat9.matcher(code);
		String zann;
		mat9.find();
		if(mat9.group().equals(""))		zann="0";
		else zann=mat9.group();
//		while (mat9.find()){
//			if(mat9.group().equals(""))	System.out.println("0");
//			else  System.out.println(mat9.group());
//		}
//		System.out.println(mat1.group()+"  "+mat2.group());
		sql="insert into girl(uname,uid,mid,text,oriText,time,commentNum,zhuanfaNum,zanNum) values ('"+namee+"','"+uidd+"','"+midd+"','"+biaoqing(textt)+"','"+oriTextt+"','"+timee+"','"+commentt+"','"+zhuanfaa+"','"+zann+"')";
		System.out.println(page+"	"+namee);
		sta.executeUpdate(sql);
//		String data="|用户名："+namee+"|内容："+textt+"|时间："+timee+"|评论数："+commentt+"|转发数："+zhuanfaa+"|赞数："+zann;
//		numSave(data, "C:/Users/sk/Desktop/girl.txt");
		
	}
	
	} 
	}
public static void numSave(String string,String filepath) throws IOException{	//记录已经过滤的数据
	FileWriter fw=new FileWriter(filepath,true);
	PrintWriter pw=new PrintWriter(fw);
	pw.println(string);
	pw.close();
	fw.close();
}
public static String biaoqing(String a) throws UnsupportedEncodingException{
	 String hexRaw = String.format("%x", new BigInteger(1, a.getBytes("UTF-8")));
    char[] hexRawArr = hexRaw.toCharArray();
    StringBuilder hexFmtStr = new StringBuilder();
    final String SEP = "\\x";
    for (int i = 0; i < hexRawArr.length&&hexRawArr.length>1; i++) {
    	String string =""+hexRawArr[i];
    	string +=hexRawArr[++i];
    	if((string.equals("f0")||string.equals("f3"))&&hexRawArr.length>4){
    		i+=4;
    	}else{
//    		System.out.println(string);
	            hexFmtStr.append(SEP).append(string);
    	}	
    }
   	String strArr[] = hexFmtStr.toString().split("\\\\"); // 分割拿到形如 xE9 的16进制数据    replaceAll("\\\\x8b", "").
       byte[] byteArr = new byte[strArr.length - 1];
       for (int i1 = 1; i1 < strArr.length; i1++) {
           Integer hexInt = Integer.decode("0" + strArr[i1]);
           byteArr[i1 - 1] = hexInt.byteValue();
       }
	return new String(byteArr, "UTF-8");
  }
}
