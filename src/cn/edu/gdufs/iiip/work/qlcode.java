package cn.edu.gdufs.iiip.work;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.client.HttpClient;

public class qlcode {
public static void main(String[] args) {
//	 try {
//         URL url = new URL("http://www.qlcoder.com/train/proxy");
//         HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
////         httpConnection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)");
////         System.setProperty("http.maxRedirects", "50");  
////         System.getProperties().setProperty("proxySet", "true");
//         System.getProperties().setProperty("http.proxyHost","121.201.63.168");  
//         System.getProperties().setProperty("http.proxyPort", "8080");  
////         httpConnection.setRequestProperty("User-Agent", "qlcoder spider");
//         InputStream input = httpConnection.getInputStream();
//         BufferedInputStream bf = new BufferedInputStream(input);
//         byte[] buff = new byte[1024];
//         int bytesRead = 0;
//         while ((bytesRead = bf.read(buff)) != -1) {
//             String str = new String(buff, 0, bytesRead);
//             System.out.println(str);
//         }
//     } catch (Exception e) {
//         e.printStackTrace();
//     }
	System.setProperty("http.maxRedirects", "50");  
    System.getProperties().setProperty("proxySet", "true");  
    // 如果不设置，只要代理IP和代理端口正确,此项不设置也可以  
    String ip = "121.201.63.168";  

    System.getProperties().setProperty("http.proxyHost", ip);  
    System.getProperties().setProperty("http.proxyPort", "8080");  
      
    //确定代理是否设置成功  
//    log.info(getHtml("http://www.ip138.com/ip2city.asp"));
    String html = getHtml("http://www.qlcoder.com/train/proxy");
    System.out.println(html);
}
private static String getHtml(String address){  
    StringBuffer html = new StringBuffer();  
    String result = null;  
    try{  
        URL url = new URL(address);  
        URLConnection conn = url.openConnection();  
        conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)");  
        BufferedInputStream in = new BufferedInputStream(conn.getInputStream());  
          
        try{  
            String inputLine;  
            byte[] buf = new byte[4096];  
            int bytesRead = 0;  
            while (bytesRead >= 0) {  
                inputLine = new String(buf, 0, bytesRead, "ISO-8859-1");  
                html.append(inputLine);  
                bytesRead = in.read(buf);  
                inputLine = null;  
            }  
            buf = null;  
        }finally{  
            in.close();  
            conn = null;  
            url = null;  
        }  
        result = new String(html.toString().trim().getBytes("ISO-8859-1"), "utf-8").toLowerCase();  
          
    }catch (Exception e) {  
        e.printStackTrace();  
        return null;  
    }finally{  
        html = null;              
    }  
    return result;  
}  
}
