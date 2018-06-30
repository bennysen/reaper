package org.cabbage.crawler.reaper.worker.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLUtils {

	public synchronized static String toUTF8(String url) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < url.length(); i++) {
			char c = url.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = String.valueOf(c).getBytes("utf-8");
				} catch (Exception ex) {
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}

	public static synchronized String decode2ZN(String url) {
		String u = url;
		if (isContainsChinese(url)) {
			try {
				u = URLEncoder.encode(url, "UTF-8").replace("%2F", "/").replace("%3A", ":").replace("%23", "#")
						.replace("%3D", "=").replace("%26", "&").replace("%20", "+").replace("%3F", "?");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return u;
	}

	private static boolean isContainsChinese(String url) {
		boolean isContainsChinese = false;
		String regEx = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(url);
		isContainsChinese = m.find();
		return isContainsChinese;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
