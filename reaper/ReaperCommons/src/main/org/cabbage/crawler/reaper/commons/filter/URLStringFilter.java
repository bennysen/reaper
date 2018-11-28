package org.cabbage.crawler.reaper.commons.filter;

import java.util.HashSet;
import java.util.Set;

public class URLStringFilter extends AbstractFilter {

	public static final String SEED = "SEED";

	public static final String OUTPUT = "OUTPUT";

	private Set<String> links = null;

	private Set<String> filters = null;

	public URLStringFilter() {
	}

	@Override
	public Set<String> filter() {
		Set<String> result = new HashSet<String>();
		if ( null == links || links.size() == 0 || null == filters || filters.size() == 0) {
			return links;
		}
		for (String link : links) {
			if (null == link || link.trim().length() == 0) {
				continue;
			}
			boolean kick = false;
			for (String filterStr : filters) {
				if (null == filterStr || filterStr.trim().length() == 0) {
					continue;
				}
				if (link.contains(filterStr) || link.contains(filterStr.toUpperCase())) {
					kick = true;
					break;
				}
			}
			if (!kick) {
				result.add(link);
			}
		}
		if (result.size() == 0) {
			result = null;
		}
		return result;
	}

	
	
	public Set<String> getLinks() {
		return links;
	}

	public void setLinks(Set<String> links) {
		this.links = links;
	}
	
	

	public Set<String> getFilters() {
		return filters;
	}

	public void setFilters(Set<String> filters) {
		this.filters = filters;
	}

	public static void main(String[] args) {
		String url = "http://product.auto.163.com/product/000CEJAQ.html";
		System.out.println(url.contains("product.auto.163.com"));
	}
}
