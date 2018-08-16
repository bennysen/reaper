package org.cabbage.commons.utils;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CharsetUtils {

	public final static Map<String, Charset> map = Charset.availableCharsets();

	public synchronized static boolean isInvalidCharset(String charset) {
		boolean ifInvalidCharset = true;
		if (null != map.get(charset) || null != map.get(charset.toLowerCase())
				|| null != map.get(charset.toUpperCase())) {
			if (null != map.get(charset)
					|| null != map.get(charset.toLowerCase())
					|| null != map.get(charset.toUpperCase())) {
				ifInvalidCharset = false;
			}
		}
		return ifInvalidCharset;
	}


	// 完整的判断中文汉字和符号
	public synchronized static boolean isContainsChinese(String str) {
		boolean isContainsChinese = false;
		String regEx = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		isContainsChinese = m.find();
		return isContainsChinese;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(isContainsChinese(" \n \r aaaaa"));
	}

}
