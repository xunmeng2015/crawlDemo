package cn.edu.gdufs.iiip.work;

import java.util.List;

import cn.edu.gdufs.iiip.crawler.CrawlWeibo;
import cn.edu.gdufs.iiip.entity.Weibo;

public class CrawlerWeiboTest {

	public static void main(String[] args) {
		CrawlWeibo cw = new CrawlWeibo("1763906075");
		List<Weibo> weibos = cw.getWeibo();
		for(int i =0;i<weibos.size();i++){
			System.out.println(weibos.get(i));
		}
		
		
		

	}

}
