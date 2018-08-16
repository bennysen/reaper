package org.cabbage.commons.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class URLUtils {

	private static final String URL_REGEX = "[a-zA-z]+://[^\\s]*";
	private static final String[] INTERNAL_DOMAIN = { "com", "edu", "gov",
			"mil", "arpa", "net", "org", "biz", "info", "pro", "name", "coop",
			"aero", "museum", "mobi", "asia", "tel", "int", "cc", "tv", "us",
			"travel", "xxx", "idv", "ws", "fm", "post", "rec", "arts", "firm",
			"nom", "store", "web" };

	private static final String[] COUNTRY_DOMAIN = { "ac", "ad", "ae", "af",
			"ag", "ai", "al", "am", "an", "ao", "aq", "ar", "as", "at", "au",
			"aw", "az", "ba", "bb", "bd", "be", "bf", "bg", "bh", "bi", "bj",
			"bm", "bn", "bo", "br", "bs", "bt", "bv", "bw", "by", "bz", "ca",
			"cc", "cf", "cg", "ch", "ci", "ck", "cl", "cm", "cn", "co", "cq",
			"cr", "cu", "cv", "cx", "cy", "cz", "de", "dj", "dk", "dm", "do",
			"dz", "ec", "ee", "eg", "eh", "es", "et", "ev", "fi", "fj", "fk",
			"fm", "fo", "fr", "ga", "gb", "gd", "ge", "gf", "gh", "gi", "gl",
			"gm", "gn", "gp", "gr", "gt", "gu", "gw", "gy", "hk", "hm", "hn",
			"hr", "ht", "hu", "id", "ie", "il", "in", "io", "iq", "ir", "is",
			"it", "jm", "jo", "jp", "ke", "kg", "kh", "ki", "km", "kn", "kp",
			"kr", "kw", "ky", "kz", "la", "lb", "lc", "li", "lk", "lr", "ls",
			"lt", "lu", "lv", "ly", "ma", "mc", "md", "mg", "mh", "ml", "mm",
			"mn", "mo", "mp", "mq", "mr", "ms", "mt", "mv", "mw", "mx", "my",
			"mz", "na", "nc", "ne", "nf", "ng", "ni", "nl", "no", "np", "nr",
			"nt", "nu", "nz", "om", "pa", "pe", "pf", "pg", "ph", "pk", "pl",
			"pm", "pn", "pr", "pt", "pw", "py", "qa", "re", "ro", "ru", "rw",
			"sa", "sb", "sc", "sd", "se", "sg", "sh", "si", "sj", "sk", "sl",
			"sm", "sn", "so", "sr", "st", "su", "sy", "sz", "tc", "td", "tf",
			"tg", "th", "tj", "tk", "tm", "tn", "to", "tp", "tr", "tt", "tv",
			"tw", "tz", "ua", "ug", "uk", "us", "uy", "va", "vc", "ve", "vg",
			"vn", "vu", "wf", "ws", "ye", "yu", "za", "zm", "zr", "zw" };

	private static final String[] FILE_SUFFIX = { ".apk", ".tar.gz", ".tar",
			".zip", ".7zip", ".rar", ".exe", ".txt", ".pdf", ".rmvb", ".rm",
			".jpg", ".png", ".bmp", ".tmp", ".avi", ".mp3", ".mov", ".asf",
			".wmv", ".ra", ".flv", ".js" };

	public synchronized static String getDomain(String url)
			throws MalformedURLException {
		String host = getHost(url);

		if ((host == null) || (host.equals(""))) {
			return null;
		}

		String[] segDomains = host.split("\\.");
		if (segDomains.length <= 0) {
			return null;
		}
		for (int i = segDomains.length - 1; i >= 0; i--) {
			if ((!isInternalDomain(segDomains[i]))
					&& (!isCountryDomain(segDomains[i]))) {
				return getDomainString(segDomains, i);
			}
		}
		return null;
	}

	private synchronized static String getDomainString(String[] segDomains,
			int offset) {
		StringBuilder sb = new StringBuilder();
		for (int i = offset; i < segDomains.length; i++) {
			sb.append(segDomains[i]);
			sb.append('.');
		}

		if (sb.charAt(sb.length() - 1) == '.') {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public synchronized static String getHost(String url)
			throws MalformedURLException {
		String lowerCaseURL = url.toLowerCase();
		if (!lowerCaseURL.matches(URL_REGEX)) {
			url = new StringBuilder().append("http://").append(url).toString();
		}
		URL u = new URL(url);
		return u.getHost();
	}

	public synchronized static boolean isInternalDomain(String domain) {
		for (String value : INTERNAL_DOMAIN) {
			if (value.equalsIgnoreCase(domain)) {
				return true;
			}
		}
		return false;
	}

	public synchronized static boolean isCountryDomain(String domain) {
		for (String value : COUNTRY_DOMAIN) {
			if (value.equalsIgnoreCase(domain)) {
				return true;
			}
		}
		return false;
	}

	public synchronized static boolean isFileURL(String url) {
		for (String value : FILE_SUFFIX) {
			if (value.equalsIgnoreCase(url)) {
				return true;
			}
		}
		return false;
	}

	public synchronized static String cn2code(String url) {
		String dest = url;
		if (null != url && CharsetUtils.isContainsChinese(url)) {
			try {
				dest = URLEncoder.encode(url, "UTF-8").replace("%2F", "/")
						.replace("%3A", ":").replace("%23", "#").replace("%3D",
								"=").replace("%26", "&").replace("%20", "+")
						.replace("%3F", "?");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return dest;
	}

	public synchronized static String assembledUrl(String targetURL,
			String siteURL, String baseURL) {
		if (targetURL.startsWith("http:") || targetURL.startsWith("ftp:")) {
			return targetURL;
		}

		if (null != baseURL && baseURL.trim().length() > 0) {
			siteURL = baseURL;
		}

		if (targetURL.startsWith("../")) {
			int flag = 0;
			while (targetURL.indexOf("../") != -1) {
				targetURL = targetURL.substring(3);
				flag++;
			}
			for (int i = 0; i <= flag; i++) {
				int index = siteURL.lastIndexOf("/");
				if (index != -1) {
					siteURL = siteURL.substring(0, index);
				}
			}
			return siteURL + "/" + targetURL;
		} else if (targetURL.startsWith("/")) {
			int lf = siteURL.indexOf("//");
			if (lf != -1) {
				int rf = siteURL.indexOf("/", lf + 2);
				if (rf != -1) {
					siteURL = siteURL.substring(0, rf);
				}
			}
			return siteURL + targetURL;
		} else {
			int index = siteURL.lastIndexOf("/");
			if (index != -1) {
				siteURL = siteURL.substring(0, index + 1);
			}
			if (targetURL.startsWith("./")) {
				return siteURL + targetURL.substring(2);
			} else {
				return siteURL + targetURL;
			}
		}
	}

	public synchronized static boolean invalid(String href) {
		boolean isInvalid = true;
		try {
			new URL(href);
		} catch (MalformedURLException e) {
			isInvalid = false;
		}
		return isInvalid;
	}

	/**
	 * @param args
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) throws MalformedURLException {
		String url = "http://cnepaper.com/ytrb/";
		System.out.println(URLUtils.getDomain(url));
		System.out.println(URLUtils.getHost(url));
	}

}
