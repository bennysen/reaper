package org.cabbage.crawler.reaper.worker.utils;

import java.io.Serializable;
import java.util.Set;

public class ReduceBean implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5886948104497953831L;
	
	private String key;

	private Set<String> outLinks;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Set<String> getOutLinks() {
		return outLinks;
	}

	public void setOutLinks(Set<String> outLinks) {
		this.outLinks = outLinks;
	}
	
	
}
