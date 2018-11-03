package org.cabbage.crawler.reaper.commons.filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 黑链过滤器
 * 
 * @author wkshen
 *
 */
public class BlackLinkFilter extends AbstractFilter {

	private Map<String, String> link2text = null;

	/**
	 * 构造方法
	 * 
	 * @param link2text
	 *            网页上link与对应node.text的键值对
	 */
	public BlackLinkFilter(Map<String, String> link2text) {
		this.link2text = link2text;
	}

	/**
	 * 
	 */
	@Override
	public Set<String> filter() {
		Set<String> result = new HashSet<String>();
		if (null == this.link2text) {
			return null;
		}
		Iterator<String> i = this.link2text.keySet().iterator();
		while (i.hasNext()) {
			String link = i.next();
			String value = this.link2text.get(link);
			if (null == value || value.trim().length() == 0) {
				continue;
			} else {
				result.add(link);
			}
		}
		if (result.size() == 0) {
			result = null;
		}
		return result;
	}

	public static void main(String[] args) {

	}
}
