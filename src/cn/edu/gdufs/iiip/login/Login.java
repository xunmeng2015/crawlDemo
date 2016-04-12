package cn.edu.gdufs.iiip.login;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author jaly
 */
@SuppressWarnings("unused")
public class Login {
	private String rsakv;
	private long servertime;
	private String nonce;
	private String pcid;
	private String pubkey;
	private int retcode;
	private String sp;
	private String su;

	/**
	 * 使用HttpClient4实现自动微博登陆
	 *
	 * @param username
	 *            登录账号
	 * @param password
	 *            登录密码
	 * @return cookie 登陆后返回的cookie
	 */

	public CookieStore getCookieStore(String username, String password) {
		CookieStore cookieStore = null;
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			client.getParams().setParameter("http.protocol.cookie-policy",
					CookiePolicy.BEST_MATCH);
			client.getParams().setParameter(
					HttpConnectionParams.CONNECTION_TIMEOUT, 5000);
			getParam(client);
			sp = rsaCrypt(pubkey, "10001", password, servertime, nonce);
			su = encodeUserName(username);
			String url = login(client);
			if (url.equals("-1")) {
				System.out.println("登录失败！");
			} else {
				HttpGet getMethod = new HttpGet(url);
				HttpResponse response = client.execute(getMethod);
				String entity = EntityUtils.toString(response.getEntity());
				cookieStore = client.getCookieStore();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return cookieStore;
	}

	private void getParam(DefaultHttpClient client) throws IOException,
			JSONException {
		String preloginurl = "http://login.sina.com.cn/sso/prelogin.php?"
				+ "entry=sso&"
				+ "callback=sinaSSOController.preloginCallBack&su=dW5kZWZpbmVk&"
				+ "rsakt=mod&" + "client=ssologin.js(v1.4.11)&" + "_="
				+ String.valueOf(new Date().getTime() / 1000);
		HttpGet get = new HttpGet(preloginurl);
		HttpResponse response = client.execute(get);
		String getResp = EntityUtils.toString(response.getEntity());
		int firstLeftBracket = getResp.indexOf("(");
		int lastRightBracket = getResp.lastIndexOf(")");
		String jsonStr = getResp.substring(firstLeftBracket + 1,
				lastRightBracket);
		JSONObject jsonInfo = new JSONObject(jsonStr);
		nonce = jsonInfo.getString("nonce");
		pcid = jsonInfo.getString("pcid");
		pubkey = jsonInfo.getString("pubkey");
		retcode = jsonInfo.getInt("retcode");
		rsakv = jsonInfo.getString("rsakv");
		servertime = jsonInfo.getLong("servertime");
	}

	private String rsaCrypt(String modeHex, String exponentHex,
			String password, long serverTime, String nonce)
			throws IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidKeyException,
			UnsupportedEncodingException {
		KeyFactory factory = KeyFactory.getInstance("RSA");
		BigInteger m = new BigInteger(modeHex, 16); /* public exponent */
		BigInteger e = new BigInteger(exponentHex, 16); /* modulus */
		RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);
		RSAPublicKey pub = (RSAPublicKey) factory.generatePublic(spec);
		Cipher enc = Cipher.getInstance("RSA");
		enc.init(Cipher.ENCRYPT_MODE, pub);
		String confusrPassword = serverTime + "\t" + nonce + "\n" + password;
		byte[] encryptedContentKey = enc.doFinal(confusrPassword
				.getBytes("GB2312"));
		return new String(Hex.encodeHex(encryptedContentKey));
	}

	// 用户名的编码
	private String encodeUserName(String username) throws Exception {// MTMxMzg1ODY5ODY%3D
		try {
			username = new String(Base64.encodeBase64(URLEncoder.encode(
					username, "UTF-8").getBytes()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return username;
		// username = username.replaceFirst("@", "%40");
		// return new String(Base64.encodeBase64(username.getBytes()));

	}

	private String login(DefaultHttpClient client)
			throws UnsupportedEncodingException, IOException {
		HttpPost post = new HttpPost(
				"http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.11)");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("entry", "weibo"));
		nvps.add(new BasicNameValuePair("gateway", "1"));
		nvps.add(new BasicNameValuePair("from", ""));
		nvps.add(new BasicNameValuePair("savestate", "7"));
		nvps.add(new BasicNameValuePair("useticket", "1"));
		nvps.add(new BasicNameValuePair("ssosimplelogin", "1"));
		nvps.add(new BasicNameValuePair("vsnf", "1"));
		nvps.add(new BasicNameValuePair(
				"pagerefer",
				"http://login.sina.com.cn/sso/logout.php?"
						+ "entry=miniblog&r=http%3A%2F%2Fweibo.com%2Flogout.php%3Fbackurl%3D%252F"));
		nvps.add(new BasicNameValuePair("su", su));
		nvps.add(new BasicNameValuePair("service", "miniblog"));
		nvps.add(new BasicNameValuePair("servertime", servertime + ""));
		nvps.add(new BasicNameValuePair("nonce", nonce));
		nvps.add(new BasicNameValuePair("pwencode", "rsa2"));
		nvps.add(new BasicNameValuePair("rsakv", rsakv));
		nvps.add(new BasicNameValuePair("sp", sp));
		nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
		nvps.add(new BasicNameValuePair("prelt", "115"));
		nvps.add(new BasicNameValuePair("returntype", "META"));
		nvps.add(new BasicNameValuePair(
				"url",
				"http://weibo.com/ajaxlogin.php?"
						+ "framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
		post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		HttpResponse response = client.execute(post);
		String entity = EntityUtils.toString(response.getEntity());
//		System.out.println(entity);
		String url = "";
		String location = "";
		try {
			Pattern p = Pattern.compile("location\\.replace\\(\"(.+?)\"\\);");
			Matcher m = p.matcher(entity);
			// if(m.find()){
			// location=m.group(1);
			// if(location.contains("reason=")){
			// String errInfo = location.substring(location.indexOf("reason=") +
			// 7);
			// errInfo = URLDecoder.decode(errInfo, "GBK");
			// System.out.println(errInfo);
			// }else{
			url = entity.substring(
					entity.indexOf("http://weibo.com/ajaxlogin.php?"),
					entity.indexOf("code=0") + 6);
			// }
			// }

		} catch (Exception e) {
			return "-1";
		}
		return url;
	}

	public static void main(String[] args) {
		DefaultHttpClient client = new DefaultHttpClient();
		Login l = new Login();
		for (int i = 1; i <= 20; i++) {
			CookieStore cookie = l.getCookieStore("gwcrawler" + i + "@163.com",
					"gdufsiiip");
			if (cookie != null) {
				System.out.println("succeed");
			}
		}
	}
}
