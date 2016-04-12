package cn.edu.gdufs.iiip.test;

import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;

public class encodeName {

	public static void main(String[] args) throws Exception {

		String username="吴小佳willnotletyoudown";
		String str=encodeName.encodeUserName(username);
		System.out.println(str);
	}
	
	public static String encodeUserName(String username) throws Exception {// MTMxMzg1ODY5ODY%3D
//		return new String(Base64.encodeBase64(URLEncoder.encode(username,
//				"utf-8").getBytes()));
		 username = username.replaceFirst("@", "%40");
		 return new String(Base64.encodeBase64(username.getBytes()));

	}

}
