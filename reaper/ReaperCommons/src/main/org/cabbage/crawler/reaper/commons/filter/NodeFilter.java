package org.cabbage.crawler.reaper.commons.filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 后缀过滤器
 * 
 * @author wkshen
 *
 */
public class NodeFilter extends AbstractFilter {

	private Map<String, String> link2node = null;

	private static Set<String> WHITE_NODES = new HashSet<String>();

	static {
		WHITE_NODES.add("a");
	}

	/**
	 * 构造方法
	 * 
	 * @param link2node
	 *            网页上link与node名称的键值对
	 */
	public NodeFilter(Map<String, String> link2node) {
		this.link2node = link2node;
	}

	@Override
	public Set<String> filter() {
		Set<String> result = new HashSet<String>();
		if (null == this.link2node || this.link2node.size() == 0) {
			return null;
		}
		Iterator<String> i = link2node.keySet().iterator();
		while (i.hasNext()) {
			String link = i.next();
			String node = this.link2node.get(link);
			if (null == node || node.trim().length() == 0) {
				continue;
			}
			for (String whiteNode : WHITE_NODES) {
				if (node.equals(whiteNode) || node.equals(whiteNode.toUpperCase())) {
					result.add(link);
				}
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
