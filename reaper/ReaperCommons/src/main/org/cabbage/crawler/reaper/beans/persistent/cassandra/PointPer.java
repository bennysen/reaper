package org.cabbage.crawler.reaper.beans.persistent.cassandra;

public class PointPer {
	
	private String domain;
	
	private String URL;
	
	private String perPoint;
	
	private String title;
	
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getPerPoint() {
		return perPoint;
	}
	public void setPerPoint(String perPoint) {
		this.perPoint = perPoint;
	}
	
}
