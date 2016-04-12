package cn.edu.gdufs.iiip.entity;

import cn.edu.gdufs.iiip.util.*;

/*评论类*/
public class Comment {
	private String uid;//用户uid
	private String text;//回复文本
	
	public Comment(String uid, String text) {
		this.uid = uid;
		this.text = Decode.characterEntities(text);
	}
	
	public String getUid() {return uid;}
	public void setUid(String uid) {this.uid = uid;}
	public String getText() {return text;}
	public void setText(String text) {this.text = text;}
	
	//输出成员变量
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<comment>");
		sb.append("<uid>" + this.uid + "</uid>");
		sb.append("<text>" + this.text + "</text>");
		sb.append("</comment>");
		return sb.toString();
	}
}
