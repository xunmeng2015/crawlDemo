package cn.edu.gdufs.iiip.work;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.*;

import cn.edu.gdufs.iiip.crawler.CrawlWeibo;
import cn.edu.gdufs.iiip.entity.Weibo;
import cn.edu.gdufs.iiip.login.Cookie;
import cn.edu.gdufs.iiip.util.*;

/*
 * 哲影，高校热点聚合
 * 定时爬取微博
 */
public class CrawlWeiboTiming {
	// 更新的微博区间，1天内
	static long interval = Time.distance(1, 0, 0, 0, 0);
	// 更新间隔，每4小时
	static long updateTime = Time.distance(0, 4, 0, 0, 0);
	// 用户uid路径
	static String uidPath = "全球娱乐趣事粉丝08-13uid.txt";

	public static void main(String[] args) {
		Timer timer = new Timer();
		CrawlWeiboOnce cwo = new CrawlWeiboOnce();
		timer.schedule(cwo, new Date(), updateTime);
	}
}

// 一次运行所做的工作
class CrawlWeiboOnce extends TimerTask {
	@Override
	public void run() {
		Cookie.login();
		List<String> uids = IO.readList(CrawlWeiboTiming.uidPath);
		Date now = new Date();
		Date fromTime = new Date(now.getTime() - CrawlWeiboTiming.interval);
		ExecutorService pool = Executors.newFixedThreadPool(10);
		for (String uid : uids) {
			CrawlWeiboThread cw = new CrawlWeiboThread(uid, fromTime);
			pool.execute(cw);
		}
		pool.shutdown();
	}
}

// 对cn.edu.gdufs.iiip.crawler.CrawlWeibo的多线程实现
class CrawlWeiboThread implements Runnable {
	private String uid;// uid
	private Date date;// 更新时间

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Date getDate(){
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public CrawlWeiboThread(String uid) {
		this.uid = uid;
		this.date = null;
	}

	public CrawlWeiboThread(String uid, Date date) {
		this.uid = uid;
		this.date = date;
	}

	@Override
	public void run() {
		CrawlWeibo cw = new CrawlWeibo(this.uid);
		List<Weibo> weibos = cw.getWeibo();
		// TODO: 对weibos进行存入文件或数据库操作
		String filepath = "F:/weibo/项目/";
		(new File(filepath)).mkdirs();
		String crawledPath = filepath + "crawled.txt";
		FileOutputStream fout1 = null;
	    BufferedOutputStream bout1 = null;
	    OutputStreamWriter output1 = null;
	    try {
			fout1 = new FileOutputStream(filepath+this.uid+".txt", true);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	    bout1 = new BufferedOutputStream(fout1);
	    output1 = new OutputStreamWriter(bout1);
	    for(int i =0;i<weibos.size();i++){
	    	try {
				output1.write(weibos.get(i).toString());
				output1.write("\r\n");
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
	    }
	    try {
			output1.flush();
			output1.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		FileOutputStream fout2 = null;
	    BufferedOutputStream bout2 = null;
	    OutputStreamWriter output2 = null;
		try {
			fout2 = new FileOutputStream(crawledPath, true);
		} catch (FileNotFoundException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		bout2 = new BufferedOutputStream(fout2);
		output2 = new OutputStreamWriter(bout2);
	    try {
			output2.write(uid+"\r\n");
		    output2.flush();
		    output2.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

	   

  	}
}
