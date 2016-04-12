package cn.edu.gdufs.iiip.login;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.apache.http.client.CookieStore;

import cn.edu.gdufs.iiip.crawler.Crawl;
import cn.edu.gdufs.iiip.util.*;

/*cookie存储，更新，读取的管理*/
public class Cookie {
	// cookie更新时间路径
	private static String loginTimePath = "record/logintime.txt";
	// cookie保存文件夹路径
	private static String cookiePath = "cookie/";
	// 账号保存路径
	private static String accountPath = "record/account.txt";
	// cookie更新时间间隔下限
	private static Long updateCookieTime = Time.distance(0, 8, 0, 0, 0);// 8h

	// 登录，检查cookie更新时间
	public static void login() {
		String timeStr = "<timestamp>0</timestamp>";
		if ((new File("record/logintime.txt")).exists()) {
			timeStr = IO.read(loginTimePath);
		}
		Pattern pat = Pattern.compile("<timestamp>.*?</timestamp>");
		Matcher mat = pat.matcher(timeStr);
		mat.find();
		timeStr = mat.group().replaceAll("<.*?>", "");
		Date updateTime = new Date(Long.parseLong(timeStr) + updateCookieTime);
		Date now = new Date();
		if (now.after(updateTime)) {
			try {
				Scanner scan = new Scanner(new File(accountPath));
				String user, passwd;
				while ((user = scan.next()) != null) {
					passwd = scan.next();
					login(user, passwd);
				}
				scan.close();
			} catch (Exception ex) {
			}
			String loginTimeStr = "<timestamp>" + Long.toString(now.getTime())
					+ "</timestamp>" + "<timeStr>" + now + "</timeStr>";
			IO.print(loginTimeStr, loginTimePath);
		}
	}

	// 登陆，不检查cookie更新时间直接登陆
	public static void loginnow() {
		try {
			Scanner scan = new Scanner(new File(accountPath));
			String user, passwd;
			while ((user = scan.next()) != null) {
				passwd = scan.next();
				login(user, passwd);
			}
			scan.close();
		} catch (Exception ex) {
		}
		Date nowTime = new Date();
		String loginTimeStr = "<timestamp>" + Long.toString(nowTime.getTime())
				+ "</timestamp>" + "<timeStr>" + nowTime + "</timeStr>";
		IO.print(loginTimeStr, loginTimePath);
	}

	// 登陆某一个账号
	public static void login(String user, String passwd) {
		(new File(cookiePath)).mkdirs();
		CookieStore cookie = (new Login()).getCookieStore(user, passwd);
		IO.print(cookie, cookiePath + user);
		System.out.println("generate cookie: \n  " + user + "\n");
	}

	// 随机返回cookie
	public static CookieStore getCookie() {
		File[] files = (new File(cookiePath)).listFiles();
//		int num = files.length;
//		int i = (int) (Math.random() * num);
//		// 0<=i<=9
//		if (i == num)
//			i = 0;
		return IO.readCookieStore(files[0].getAbsolutePath());
	}

	// 测试每个cookie的可用性
	public static void testCookie() {
		File[] files = (new File(cookiePath)).listFiles();
		for (int i = 0; i < files.length; ++i) {
			testCookie(files[i].getName());
		}
	}

	// 测试某个cookie的可用性
	public static void testCookie(String user) {
		String url = "http://weibo.com/2863259912/AhMRtlt3M";
		Crawl c = new Crawl();
		CookieStore cookie = IO.readCookieStore(cookiePath + user);
		String html = c.crawl(url, cookie);
		System.out.print(user + ":\n  ");
		if (html.contains("bonjour")) {
			System.out.println(html);
			System.out.println("succeed\n");
		} //else {
//			System.out.println("fail\n");
//		}
	}

	public static void main(String[] args) {
		 login();
//		loginnow();
		testCookie();
	}
}
