package cn.edu.gdufs.iiip.util;

import java.util.*;

/*Date对象的转化*/
public class Time {
	//根据Date对象获取年月日字符串YYYYMMDD，如20140307
	public static String getDayStr(Date date) {
		String[] months = {"","Jan","Feb","Mar","Apr","May"
				,"Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		String[] timeStr = date.toString().split(" ");
		String year = timeStr[5], month="", day = timeStr[2];
		for (int i=1; i<=12; ++i) {
			if (timeStr[1].equals(months[i])) {
				month = i + "";
				if (i < 10) month = "0" + month;
				break;
			}
		}
		if (day.length() == 1) day = "0" + day;
		return year+month+day;//YYYYMMDD
	}
	
	//根据年月日获取当天时刻的Date对象
	@SuppressWarnings("deprecation")
	public static Date getDate(int year, int month, int day) {
		return new Date(year-1900,month-1,day);
	}
	
	//对于给定Date返回这一天的0时0分0秒的Date
	public static Date thisDay(Date date) {
		int year, month = 0, day;
		String[] timeStr = date.toString().split(" ");
		year = Integer.parseInt(timeStr[5]);
		String[] months = {"","Jan","Feb","Mar","Apr","May"
				,"Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		for (int i=1; i<=12; ++i) {
			if (timeStr[1].equals(months[i])) month = i;
		}
		day = Integer.parseInt(timeStr[2]);
		return getDate(year, month, day);
	}
	
	//获得指定时间间隔的毫秒数
	public static Long distance(int d, int h, int min, int s, int ms) {
		return ((((long)d*24+h)*60+min)*60+s)*1000+ms;
	}
	
	public static void main(String[] args) {
		Date d = getDate(2014,5,2);
		System.out.println(d);
	}
}
