package cn.edu.gdufs.iiip.crawler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.*;

import cn.edu.gdufs.iiip.login.*;
import cn.edu.gdufs.iiip.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/*爬取一个网页*/
public class Crawl {
	private static String charset = "UTF-8";
	private CookieStore cookie;//登陆cookie

	public Crawl() {
		this.cookie = Cookie.getCookie();
	}
	public Crawl(CookieStore cookie) {
		this.cookie = cookie;
	}
	
	//使用随机cookie
	public  String crawl(String url) {
		StringBuilder sb = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();
		client.setCookieStore(this.cookie);
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,"
				+ "image/webp,*/*;q=0.8");
		httpGet.addHeader("Accept-Encoding", "deflate");
		httpGet.addHeader("Accept-Language",
				"zh-CN,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		httpGet.addHeader("Connection", "	keep-alive");
		httpGet.addHeader("Host", getHost(url));
		httpGet.addHeader("Referer", "http://weibo.com/");
		httpGet.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) "
				+ "Chrome/31.0.1650.63 Safari/537.36");
		try {
			HttpResponse response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(entity.getContent(),charset));
			String line = null;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				sb.append(line);
//			Pattern pat3=Pattern.compile("(?<=(\\$CONFIG\\['onick']=')).*(?=')");
//			Matcher mat1=pat3.matcher(line);
//			while (mat1.find()){
//				System.out.println(mat1.group());
//			}
//			Pattern pat1=Pattern.compile("(?<=(<strong\\sclass=\\\\\"W_f16\\\\\">)).*?(?=<\\\\/strong>)");
//			Matcher mat2=pat1.matcher(line);
//			while (mat2.find()){
//				System.out.println(mat2.group().replaceAll("<\\\\/strong><span class=\\\\\"S_txt2\\\\\">", ""));
//			}
//				System.out.println(line);
			}
		} catch (Exception ex) {}
		String html = Decode.UTF8(sb.toString());
		return html;
		
	}
	
	//使用指定cookie
	public String crawl(String url, CookieStore cookie) {
		StringBuilder sb = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();
		client.setCookieStore(cookie);
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Accept","text/html,application/xhtml+xml,"
				+ "application/xml;q=0.9,*/*;q=0.8");
		httpGet.addHeader("Accept-Encoding", "deflate");
		httpGet.addHeader("Accept-Language",
				"zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		httpGet.addHeader("Connection", "	keep-alive");
		httpGet.addHeader("Host", getHost(url));
		httpGet.addHeader("Referer", "http://weibo.com/");
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; "
				+ "WOW64; rv:20.0) Gecko/20100101 Firefox/20.0");
		try {
			HttpResponse response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(entity.getContent(),charset));
			String line = null;
			while ((line = br.readLine()) != null) sb.append(line);
		} catch (Exception ex) {}
		String html = Decode.UTF8(sb.toString());
		return html;
	}
	
	//根据url得出host，只用于微博网页
	private String getHost(String url) {
		String host = url.replaceFirst("^http://", "");
		Pattern pat = Pattern.compile("^.*?weibo\\.com");
		Matcher mat = pat.matcher(host);
		mat.find();
		return mat.group();
	}
	
	
	
	//不使用cookie
	public String open(String url) {
		StringBuilder sb = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(entity.getContent(),charset));
			String line = null;
			while ((line = br.readLine()) != null){
				sb.append(line);
			}
		} catch (Exception ex) {}
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {
		Crawl c=new Crawl();
		String html=c.getHost("http://weibo.com/u/2915126043");
		System.out.println(html);
//		String html=c.getHost("http://weibo.com/hkstv");
//		System.out.println(html);
		
		//URLConnection con=new URL("http://weibo.com/u/2915126043").openConnection();
//		URLConnection con=new URL("http://s.weibo.com/?topnav=1&wvr=6").openConnection();
//		con.setConnectTimeout(5000);
//		InputStream is=con.getInputStream();
//		BufferedReader br=new BufferedReader(new InputStreamReader(is,"gbk"));
//		StringBuffer sb=new StringBuffer();
//		String line="";
//		while((line=br.readLine())!=null){
//			sb.append(line);
//		}
//		br.close();
//		is.close();
//		System.out.println(sb.toString());
	}
}
