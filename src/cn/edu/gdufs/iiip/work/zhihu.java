package cn.edu.gdufs.iiip.work;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
/*
 * 
 * 利用httpclient post爬取知乎问题的答案
 * 
 */
public class zhihu {
 
    public static void main(String[] args) throws ClientProtocolException, IOException
    {
        RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        for (int page = 0; page <= 1072/20; page++){
            HttpPost post = new HttpPost("https://www.zhihu.com/node/QuestionAnswerListV2?method=next&params=%7B%22url_token%22%3A41504353%2C%22pagesize%22%3A20%2C%22offset%22%3A"+page*20+"%7D");
            HttpResponse httpResponse = httpClient.execute(post);
            HttpEntity he = httpResponse.getEntity();
            if (he != null) {
                String responseString = EntityUtils.toString(he);
//                System.out.println("response length:" + responseString.length());
//                System.out.println(UTF8(responseString));
                String html = UTF8(responseString);
                Pattern pat = Pattern.compile("<div tabindex.*?meta-item copyright");
                Matcher mat = pat.matcher(html);
                String user = null, text = null, time = null, commentNum = null;
                int i = 0;
                while (mat.find()){
                	i++;
//                	System.out.println(mat.group());
                	String comment = mat.group();
                	Pattern pat1 = Pattern.compile("zm-item-answer-author-info.*?<\\\\/span>");
                	Matcher mat1 = pat1.matcher(comment);
                	if (mat1.find())		user = mat1.group().replaceAll("<span title.*>", "").replaceAll("<.*?>", "").replaceAll(".*?>", "").replaceAll("\\\\n", "");
                	Pattern pat2 = Pattern.compile("zm-editable-content clearfix.*?zm-item-meta");
                	Matcher mat2 = pat2.matcher(comment);
                	if (mat2.find())		text = mat2.group().replaceAll("<.*?>", "").replaceAll(".*?>", "").replaceAll("<.*", "").replaceAll("\\\\n", "");
                	Pattern pat3 = Pattern.compile("answer-date-link-wrap.*?addcomment");
                	Matcher mat3 = pat3.matcher(comment);
                	if(mat3.find())		time = mat3.group().replaceAll("<.*?>", "").replaceAll(".*?>", "").replaceAll("<.*", "").replaceAll("\\\\n", "").replaceAll("发布于|编辑于", "");
                	Pattern pat4 = Pattern.compile("z-icon-comment.*?thanks");
                	Matcher mat4 = pat4.matcher(comment);
                	if (mat4.find())		commentNum = mat4.group().replaceAll("<.*?>", "").replaceAll(".*?>", "").replaceAll("<.*", "").replaceAll("\\\\n", "");
                	outPut(user+"|"+text+"|"+time+"|"+commentNum, "C:/Users/sk/Desktop/zhihu.txt");
                    System.out.println(page+"  "+i+":  "+user+"|"+text+"|"+time+"|"+commentNum);
                }

                }
        }
        }
    public static void outPut(String string,String filepath) throws IOException{	//记录已经过滤的数据
    	FileWriter fw=new FileWriter(filepath,true);
    	PrintWriter pw=new PrintWriter(fw);
    	pw.println(string);
    	pw.close();
    	fw.close();
    }
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
}