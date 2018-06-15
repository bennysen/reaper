package org.cabbage.crawler.reaper.worker.classifier.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * URL进行分类及计算比例对象
 * 
 * @author wkshen
 *
 */
public class URLClassifierEntity {
	private String originalURL;
	private String featureURL;
	private int size;
	private String ratio;
	private Set<String> URLSet = new HashSet<String>();
	public String getOriginalURL() {
		return originalURL;
	}
	public void setOriginalURL(String originalURL) {
		this.originalURL = originalURL;
	}
	public String getFeatureURL() {
		return featureURL;
	}
	public void setFeatureURL(String featureURL) {
		this.featureURL = featureURL;
	}

	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getRatio() {
		return ratio;
	}
	public void setRatio(String ratio) {
		this.ratio = ratio;
	}
	public Set<String> getURLSet() {
		return URLSet;
	}
	public void setURLSet(Set<String> uRLSet) {
		URLSet = uRLSet;
	}



}
