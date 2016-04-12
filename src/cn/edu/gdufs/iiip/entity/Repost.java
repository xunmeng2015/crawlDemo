package cn.edu.gdufs.iiip.entity;

import cn.edu.gdufs.iiip.util.*;

/*转发类*/
public class Repost {
	private String mid;//微博mid
	private String uid;//用户uid
	private String text;//回复文本
	
	public Repost(String mid, String uid, String text) {
		this.mid = mid;
		this.uid = uid;
		this.text = Decode.characterEntities(text);
	}

	public String getMid() {return mid;}
	public void setMid(String mid) {this.mid = mid;}
	public String getUid() {return uid;}
	public void setUid(String uid) {this.uid = uid;}
	public String getText() {return text;}
	public void setText(String text) {this.text = text;}
	
	//输出成员变量
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<repost>");
		sb.append("<mid>" + this.mid + "</mid>");
		sb.append("<uid>" + this.uid + "</uid>");
		sb.append("<text>" + this.text + "</text>");
		sb.append("</repost>");
		return sb.toString();
	}
}
