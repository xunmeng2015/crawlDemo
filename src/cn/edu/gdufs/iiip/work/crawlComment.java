package cn.edu.gdufs.iiip.work;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import cn.edu.gdufs.iiip.crawler.Crawl;
import cn.edu.gdufs.iiip.login.Cookie;

public class crawlComment {
@SuppressWarnings("deprecation")
public static void main(String[] args) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
	Connection con=null;
//	String mysqlUrl="jdbc:mysql://192.168.235.36:3306/lime?"
//			+ "user=lime&password=lime&useUnicode=true&characterEncoding=UTF8";
	String mysqlUrl="jdbc:mysql://localhost:3306/baseinformation?"
			+ "user=root&password=553352836w&useUnicode=true&characterEncoding=UTF8";
	Class.forName("com.mysql.jdbc.Driver");
	System.out.println("成功加载MySQL驱动");
	con=(Connection) DriverManager.getConnection(mysqlUrl);
	String sql;
	Statement sta,stmt;
	sta = (Statement) con.createStatement();
	stmt = (Statement) con.createStatement();
	ResultSet re = null;
	Cookie.login("gwcrawler20@163.com","321654");
	Crawl c=new Crawl();
	sql = "select * from girl";
	re = sta.executeQuery(sql);
	re.last();
	int number=re.getRow();
	System.out.println(number);
	for (int i = 1 ; i<=number ; i++){
	sql = "select * from girl where id="+i;
	re = sta.executeQuery(sql);
	while (re.next()){
	String mid = re.getString("mid");
//	System.out.println(mid);
	int commentNum = Integer.parseInt(re.getString("commentNum"));
	if (commentNum != 0){
	String url="http://weibo.com/aj/comment/big?&id="+mid+"&page=1";
	String html;
	int pageNum;
	html = c.crawl(url);
//	System.out.println(html);System.exit(0);
	Pattern page = Pattern.compile("\\d*<\\\\/a>\\S*<a class=\\\\\"W_btn_c btn_page_next\\\\\"");
	Matcher mat11 = page.matcher(html);
	if(mat11.find())
		pageNum = Integer.parseInt(mat11.group().replaceAll("<.*", ""));
	else pageNum = 1;
	System.out.println(pageNum);
	for (int num = 1; num <= pageNum;num++){
		url = "http://weibo.com/aj/comment/big?&id="+mid+"&page="+num;
		html = c.crawl(url);
	Pattern pat = Pattern.compile("<a href=\\\\\"\\\\/\\d*\\\\\" title=\\\\\"\\S*\\\\\" usercard=\\\\\"id=\\d*\\\\\">.*?(?=span>)");
	Matcher mat = pat.matcher(html);
	while (mat.find()){
		String text = mat.group();
		String uName;
		Pattern pat1=Pattern.compile("usercard=\\\\\"id=\\d*\\\\\">.*?(?=<)");
		Matcher mat1=pat1.matcher(text);
		mat1.find();
		uName = mat1.group().replaceAll(".*>", "");
		Pattern pat2 = Pattern.compile("<\\\\/a>：.*<span");
		Matcher mat2 = pat2.matcher(text);
		String comment;
		mat2.find();
		comment = mat2.group().replaceAll("<.*：|<.*?>", "").replaceAll("<.*", "").replaceAll(" ", "");
		Pattern pat3 = Pattern.compile("(?<=S_txt2\\\\\">).*?(?=<\\\\/)");
		Matcher mat3 = pat3.matcher(text);
		String time;
		mat3.find();
		time = mat3.group().replaceAll("\\(|\\)","").replace("今天",new Date().getMonth()+"月"+new Date().getDate()+"日");
		System.out.println(i+"  "+num+"  "+uName+"  "+comment+"  "+time);
		sql = "insert into comment_girl(uname,comment,time) values('"+uName+"','"+biaoqing(comment)+"','"+time+"')";
		stmt.executeUpdate(sql);
	}
	}
	}
	}
	}
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
//     		System.out.println(string);
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
