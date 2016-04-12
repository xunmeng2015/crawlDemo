package cn.edu.gdufs.iiip.util;

import java.util.*;
import java.util.regex.*;

/*编码的转换，匹配，替换之类的*/
public class Decode {
	//将所有\u1234串替换为utf-8字符
	public static String UTF8(String html) {
		String regex = "\\\\u[0-9A-Fa-f]{4}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String str = matcher.group();
			matcher.appendReplacement(sb, string2UTF8(str));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
	
	//将一个\u1234字符串转为utf-8字符
	private static String string2UTF8(String str) {
		int[] tmp = new int[4];
		for (int i=0;i<4;++i) {
			if (str.charAt(i+2) >= 'a') {
				tmp[i] = 10 + str.charAt(i+2) - 'a';
			}
			else if (str.charAt(i+2) >= 'A') {
				tmp[i] = 10 + str.charAt(i+2) - 'A';
			}
			else {
				tmp[i] = str.charAt(i+2) - '0';
			}
		}
		char[] c = new char[1];
		c[0] = (char)((((((tmp[0]*16)+tmp[1])*16)+tmp[2])*16)+tmp[3]);
		return Arrays.toString(c).substring(1,2);
	}
	
	//转换文本中的字符实体
	public static String characterEntities(String text) {
		return text.replace("&nbsp;", " ").replace("&lt;", "<")
				.replace("&gt;", ">").replace("&amp;", "&")
				.replace("&quot;", "\"").replace("&apos;", "'")
				.replace("&cent;", "￠").replace("&pound;", "£")
				.replace("&yen;", "¥").replace("&euro;", "€")
				.replace("&sect;", "§").replace("&copy;", "©")
				.replace("&reg;", "®").replace("&trade;", "™")
				.replace("&times;", "×").replace("&divide;", "÷");
	}
	
	//处理正文HTML得出表情字符
	public static String expressionCharacter(String text) {
		Pattern pat = Pattern.compile("<img.*?>");
		Matcher mat = pat.matcher(text);
		StringBuffer sb = new StringBuffer();
		while (mat.find()) {//表情符号
			String str = mat.group();
			Pattern pat1 = Pattern.compile("\\[.*?\\]");
			Matcher mat1 = pat1.matcher(str);
			if (mat1.find()) str = mat1.group();
			mat.appendReplacement(sb, str);
		}
		mat.appendTail(sb);
		return sb.toString().replaceAll("<.*?>", "").replaceAll("\\\\r", " ")
				.replaceAll("\\\\t", " ").replaceAll("\\\\n", " ")
				.replaceAll("\\\\/", "/").replaceAll("\\s+", " ").trim();
	}
	
	//匹配文本中用标签包含起来的文本，如<uid>213478141289481</uid>
	public static String matchField(String text, String field) {
		Pattern pat = Pattern.compile("<"+field+">.*?</"+field+">");
		Matcher mat = pat.matcher(text);
		if (mat.find()) return mat.group()
				.replace("<"+field+">", "").replace("</"+field+">", "");
		else return null;
	}
	
	//匹配文本中用标签包含起来的文本，如<uid>213478141289481</uid>
	public static List<String> matchFields(String text, String field) {
		List<String> matchs = new ArrayList<>();
		Pattern pat = Pattern.compile("<"+field+">.*?</"+field+">");
		Matcher mat = pat.matcher(text);
		while (mat.find()) {
			matchs.add(mat.group()
					.replace("<"+field+">", "").replace("</"+field+">", ""));
		}
		return matchs;
	}
}
