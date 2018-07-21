package org.cabbage.crawler.reaper.commons.filter;

import java.util.HashSet;
import java.util.Set;

/**
 * 后缀过滤器
 * 
 * @author wkshen
 *
 */
public class SuffixFilter extends AbstractFilter {

	private Set<String> links = null;

	/**
	 * 构造方法
	 * 
	 * @param links
	 *            网页上的links集合
	 */
	public SuffixFilter(Set<String> links) {
		this.links = links;
	}

	private static Set<String> SUFFIXS = new HashSet<String>();

	private static Set<String> SCRIPT_SUFFIXS = new HashSet<String>();

	private static Set<String> CSS_SUFFIXS = new HashSet<String>();

	private static Set<String> FILE_SUFFIXS = new HashSet<String>();

	private static Set<String> PIC_SUFFIXS = new HashSet<String>();

	private static Set<String> VEDIO_SUFFIXS = new HashSet<String>();

	private static Set<String> AUDIO_SUFFIXS = new HashSet<String>();

	static {
		CSS_SUFFIXS.add(".css");
		PIC_SUFFIXS.add(".bmp");
		PIC_SUFFIXS.add(".png");
		PIC_SUFFIXS.add(".jpg");
		PIC_SUFFIXS.add(".jpeg");
		PIC_SUFFIXS.add(".gif");
		VEDIO_SUFFIXS.add(".swf");
		VEDIO_SUFFIXS.add(".flv");
		VEDIO_SUFFIXS.add(".dat");
		VEDIO_SUFFIXS.add(".asf");
		VEDIO_SUFFIXS.add(".mkv");
		VEDIO_SUFFIXS.add(".mp4");
		VEDIO_SUFFIXS.add(".avi");
		VEDIO_SUFFIXS.add(".wmv");
		VEDIO_SUFFIXS.add(".mov");
		VEDIO_SUFFIXS.add(".vob");
		VEDIO_SUFFIXS.add(".asx");
		VEDIO_SUFFIXS.add(".3pg");
		VEDIO_SUFFIXS.add(".m4v");
		VEDIO_SUFFIXS.add(".rmvb");
		VEDIO_SUFFIXS.add(".rm");
		SCRIPT_SUFFIXS.add(".js");
		FILE_SUFFIXS.add(".zip");
		FILE_SUFFIXS.add(".tar");
		FILE_SUFFIXS.add(".gz");
		SCRIPT_SUFFIXS.add(".do");
		AUDIO_SUFFIXS.add(".mp3");
		AUDIO_SUFFIXS.add(".wma");
		AUDIO_SUFFIXS.add(".wav");

		SUFFIXS.addAll(SCRIPT_SUFFIXS);
		SUFFIXS.addAll(CSS_SUFFIXS);
		SUFFIXS.addAll(FILE_SUFFIXS);
		SUFFIXS.addAll(PIC_SUFFIXS);
		SUFFIXS.addAll(VEDIO_SUFFIXS);
		SUFFIXS.addAll(AUDIO_SUFFIXS);
	}

	@Override
	public Set<String> filter() {
		Set<String> result = new HashSet<String>();
		if (null == links || links.size() == 0 || null == SUFFIXS || SUFFIXS.size() == 0) {
			return links;
		}
		for (String link : links) {
			if (null == link || link.trim().length() == 0) {
				continue;
			}
			for (String suffix : SUFFIXS) {
				if (null == suffix || suffix.trim().length() == 0) {
					continue;
				}
				if (link.endsWith(suffix) || link.endsWith(suffix.toUpperCase())) {
					continue;
				}
				result.add(link);
			}
		}
		if (result.size() == 0) {
			result = null;
		}
		return result;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
