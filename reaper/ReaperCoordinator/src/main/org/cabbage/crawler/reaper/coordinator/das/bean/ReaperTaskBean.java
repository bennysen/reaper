package org.cabbage.crawler.reaper.coordinator.das.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reaper_task")
public class ReaperTaskBean implements Serializable {


	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4529161676803536606L;

	@Column
	private Long ID;

	@Column
	private Long domainID;

	@Column
	private Long siteType;

	@Column
	private Long domainType;

	@Column
	private Long proxyType;
	
	@Column
	private Long enable;
	
	@Column
	private String charset;
	
	/**
	 * 任务状态，0，等待生成任务；1，已经生成任务；2，正在执行；
	 */
	@Column
	private Long status;
	
	@Column
	private String processHost;
	
	@Column
	private Long lastUpdateTime;

	@Id
	public Long getID() {
		return ID;
	}

	public void setID(Long id) {
		ID = id;
	}

	public Long getDomainID() {
		return domainID;
	}

	public void setDomainID(Long domainID) {
		this.domainID = domainID;
	}

	public Long getSiteType() {
		return siteType;
	}

	public void setSiteType(Long siteType) {
		this.siteType = siteType;
	}

	public Long getDomainType() {
		return domainType;
	}

	public void setDomainType(Long domainType) {
		this.domainType = domainType;
	}

	public Long getProxyType() {
		return proxyType;
	}

	public void setProxyType(Long proxyType) {
		this.proxyType = proxyType;
	}

	public Long getEnable() {
		return enable;
	}

	public void setEnable(Long enable) {
		this.enable = enable;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getProcessHost() {
		return processHost;
	}

	public void setProcessHost(String processHost) {
		this.processHost = processHost;
	}

	public Long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

}
