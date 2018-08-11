package org.cabbage.crawler.reaper.commons.object.business.statistics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Point对象，一个URL就是一个Point，并且会附属前置URL集合和外链URL集合，以及访问次数。
 * 
 * @author wkshen
 *
 */
public class Point {

	private String URL;
	private String domain;
	private long touchCount = 1;
	private Set<String> perPoint;
	private Set<String> outPonit;

	public long touch() {
		if (this.touchCount < 0) {
			this.touchCount = 0;
		}
		this.touchCount++;
		return this.touchCount;
	}

	public void addPerPoint(String URL) {
		if (null == URL || URL.trim().length() == 0) {
			return;
		}
		if (null == this.perPoint) {
			this.perPoint = new HashSet<String>();
		}
		this.perPoint.add(URL);
	}

	public void addPerPoint(Collection<String> perPoint) {
		if (null == perPoint || perPoint.size() == 0) {
			return;
		}
		if (null == this.perPoint) {
			this.perPoint = new HashSet<String>();
		}
		this.perPoint.addAll(perPoint);
	}

	
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}

	public long getTouchCount() {
		return touchCount;
	}

	public void setTouchCount(long touchCount) {
		this.touchCount = touchCount;
	}

	public Set<String> getPerPoint() {
		return perPoint;
	}

	public void setPerPoint(Set<String> perPoint) {
		this.perPoint = perPoint;
	}

	public Set<String> getOutPonit() {
		return outPonit;
	}

	public void setOutPonit(Set<String> outPonit) {
		this.outPonit = outPonit;
	}

}
