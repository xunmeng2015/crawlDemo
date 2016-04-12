package cn.edu.gdufs.iiip.entity;

import java.util.*;

import cn.edu.gdufs.iiip.util.*;

/*微博类*/
public class Weibo {
	private String mid;//微博mid
	private String uid;//用户uid
	private String ruid;//原微博uid，非转发时为11111111
	private String text;//微博内容
	private String originText;//原微博内容，非转发时为空
	private Date weiboDate;//日期
	private int commentCount;//评论数
	private int repostCount;//转发数
	private int zanCount;//赞数
	private String source;//来源
	private String url;//URL
	private String time;
	
	public Weibo(String mid, String uid, String ruid, String time, String text,
			String originText, Date weiboDate, int commentCount,
			int repostCount, int zanCount, String source, String url) {
		this.mid = mid;
		this.uid = uid;
		this.ruid = ruid;
		this.time = time;
		this.text = Decode.characterEntities(text);
		this.originText = Decode.characterEntities(originText);
		this.weiboDate = weiboDate;
		this.commentCount = commentCount;
		this.repostCount = repostCount;
		this.zanCount = zanCount;
		this.source = source;
		this.url = url;
	}
	
	public String getMid() {return mid;}
	public void setMid(String mid) {this.mid = mid;}
	public String getUid() {return uid;}
	public void setUid(String uid) {this.uid = uid;}
	public String getRuid() {return ruid;}
	public void setRuid(String ruid) {this.ruid = ruid;}
	public String getText() {return text;}
	public String getTime(){return time;}
	public void setTime(String time){this.time = time;}
	public void setText(String text) {this.text = text;}
	public String getOriginText() {return originText;}
	public void setOriginText(String originText)
		{this.originText = originText;}
	public Date getWeiboDate() {return weiboDate;}
	public void setWeiboDate(Date weiboDate) {this.weiboDate = weiboDate;}
	public int getCommentCount() {return commentCount;}
	public void setCommentCount(int commentCount)
		{this.commentCount = commentCount;}
	public int getRepostCount() {return repostCount;}
	public void setRepostCount(int repostCount)
		{this.repostCount = repostCount;}
	public int getZanCount() {return zanCount;}
	public void setZanCount(int zanCount) {this.zanCount = zanCount;}
	public String getSource() {return source;}
	public void setSource(String source) {this.source = source;}
	public String getUrl() {return url;}
	public void setUrl(String url) {this.url = url;}
	
	//输出成员变量
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<weibo>");
		sb.append("<mid>" + this.mid + "</mid>");
		sb.append("<uid>" + this.uid + "</uid>");
		sb.append("<ruid>" + this.ruid + "</ruid>");
		sb.append("<time>" + this.time + "</time>");
		sb.append("<text>" + this.text + "</text>");
		sb.append("<originText>" + this.originText + "</originText>");
		sb.append("<weiboDate>" + this.weiboDate.getTime() + "</weiboDate>");
		sb.append("<commentCount>" + this.commentCount + "</commentCount>");
		sb.append("<repostCount>" + this.repostCount + "</repostCount>");
		sb.append("<zanCount>" + this.zanCount + "</zanCount>");
		sb.append("<source>" + this.source + "</source>");
		sb.append("<url>" + this.url + "</url>");
		sb.append("</weibo>");
		return sb.toString();
	}
}
