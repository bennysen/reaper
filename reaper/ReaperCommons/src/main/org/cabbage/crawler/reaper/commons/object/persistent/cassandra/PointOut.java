package org.cabbage.crawler.reaper.commons.object.persistent.cassandra;

public class PointOut {

	private String domain;

	private String URL;

	private String outPoint;

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

	public String getOutPoint() {
		return outPoint;
	}

	public void setOutPoint(String outPoint) {
		this.outPoint = outPoint;
	}

}
