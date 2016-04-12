package cn.edu.gdufs.iiip.entity;

import java.util.*;

import cn.edu.gdufs.iiip.util.*;

/*微博用户类*/
public class User {
	private String uid;//用户uid
	private String nickName;//昵称
	private int followCount;//关注数
	private int fanCount;//粉丝数
	private int weiboCount;//微博数
	private String introduction;//介绍文字
	private Set<String> label;//标签
	private String gender;//性别
	private String location;//所在地
	private String school;//学校
	private String company;//公司
	
	private String imgurl;//头像链接
	private String birthday;//生日
	
	public User(String uid, String nickName, int followCount, int fanCount,
			int weiboCount,String introduction, Set<String> label,
			String gender, String location, String school, String company, String imgurl, String birthday) {
		this.uid = uid;
		this.nickName = nickName;
		this.followCount = followCount;
		this.fanCount = fanCount;
		this.weiboCount = weiboCount;
		this.introduction = Decode.characterEntities(introduction);
		this.label = label;
		this.gender = gender;
		this.location = location;
		this.school = school;
		this.company = company;
		
		this.imgurl = imgurl;
		this.birthday = birthday;
	}
	
	public User(String uid, String nickName, int followCount,
			int fanCount, int weiboCount) {
		this.uid = uid;
		this.nickName = nickName;
		this.followCount = followCount;
		this.fanCount = fanCount;
		this.weiboCount = weiboCount;
	}
	
	public String getUid() {return uid;}
	public void setUid(String uid) {this.uid = uid;}
	public String getNickName() {return nickName;}
	public void setNickName(String nickName) {this.nickName = nickName;}
	public int getFollowCount() {return followCount;}
	public void setFollowCount(int followCount)
		{this.followCount = followCount;}
	public int getFanCount() {return fanCount;}
	public void setFanCount(int fanCount) {this.fanCount = fanCount;}
	public int getWeiboCount() {return weiboCount;}
	public void setWeiboCount(int weiboCount) {this.weiboCount = weiboCount;}
	public String getIntroduction() {return introduction;}
	public void setIntroduction(String introduction)
		{this.introduction = introduction;}
	public Set<String> getLabel() {return label;}
	public void setLabel(Set<String> label) {this.label = label;}
	public String getGender() {return gender;}
	public void setGender(String gender) {this.gender = gender;}
	public String getLocation() {return location;}
	public void setLocation(String location) {this.location = location;}
	public String getSchool() {return school;}
	public void setSchool(String school) {this.school = school;}
	public String getCompany() {return company;}
	public void setCompany(String company) {this.company = company;}
	
	public String getimgurl(){return imgurl;}
	public void setimgurl(String imgurl){ this.imgurl = imgurl;}
	public String getbirthday(){return birthday;}
	public void setbirthday(){this.birthday = birthday;}
	
	//输出成员变量
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<user>\n");
		sb.append("<uid>" + this.uid + "</uid>\n");
		sb.append("<nickName>" + this.nickName + "</nickName>\n");
		sb.append("<followCount>" + this.followCount + "</followCount>\n");
		sb.append("<fanCount>" + this.fanCount + "</fanCount>\n");
		sb.append("<weiboCount>" + this.weiboCount + "</weiboCount>\n");
		sb.append("<introduction>" + this.introduction + "</introduction>\n");
		sb.append("<label>" + this.label + "</label>\n");
		sb.append("<gender>" + this.gender + "</gender>\n");
		sb.append("<location>" + this.location + "</location>\n");
		sb.append("<school>" + this.school + "</school>\n");
		sb.append("<company>" + this.company + "</company>\n");
		sb.append("<birthday>" + this.birthday + "</birthday>\n");
		sb.append("<imgurl>" + this.imgurl + "</imgurl>\n");
		
		sb.append("</user>\n");
		return sb.toString();
	}
}
