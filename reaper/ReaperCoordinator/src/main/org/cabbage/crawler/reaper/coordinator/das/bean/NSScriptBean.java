package org.cabbage.crawler.reaper.coordinator.das.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "nsscript")
public class NSScriptBean implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3644751143827461419L;

	@Column
	private Long ID;
	@Column
	private String siteName;
	@Column
	private Long domainID;
	@Column
	private Long dataSourceType;
	@Column
	private String charset;
	@Column
	private String reverserClassPath;
	@Column
	private String extractorClassPath;
	@Column
	private String floorXPath;
	@Column
	private String topicURLFormat;
	@Column
	private String topicSubpageURLFormat;
	@Column
	private String forumPageURLFormat;
	@Column
	private String forumSubpageURLFormat;
	@Column
	private String levelSwitch;
	@Column
	private String name;
	@Column
	private String comment;
	@Column
	private Long createTime;
	@Column
	private Long updateTime;
	@Column
	private Long createUser;
	@Column
	private Long updateUser;
	@Column
	private Long status;
	
	@Id
	public Long getID() {
		return ID;
	}
	public void setID(Long iD) {
		ID = iD;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public Long getDomainID() {
		return domainID;
	}
	public void setDomainID(Long domainID) {
		this.domainID = domainID;
	}
	public Long getDataSourceType() {
		return dataSourceType;
	}
	public void setDataSourceType(Long dataSourceType) {
		this.dataSourceType = dataSourceType;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public String getReverserClassPath() {
		return reverserClassPath;
	}
	public void setReverserClassPath(String reverserClassPath) {
		this.reverserClassPath = reverserClassPath;
	}
	public String getExtractorClassPath() {
		return extractorClassPath;
	}
	public void setExtractorClassPath(String extractorClassPath) {
		this.extractorClassPath = extractorClassPath;
	}
	public String getFloorXPath() {
		return floorXPath;
	}
	public void setFloorXPath(String floorXPath) {
		this.floorXPath = floorXPath;
	}
	public String getTopicURLFormat() {
		return topicURLFormat;
	}
	public void setTopicURLFormat(String topicURLFormat) {
		this.topicURLFormat = topicURLFormat;
	}
	public String getTopicSubpageURLFormat() {
		return topicSubpageURLFormat;
	}
	public void setTopicSubpageURLFormat(String topicSubpageURLFormat) {
		this.topicSubpageURLFormat = topicSubpageURLFormat;
	}
	public String getForumPageURLFormat() {
		return forumPageURLFormat;
	}
	public void setForumPageURLFormat(String forumPageURLFormat) {
		this.forumPageURLFormat = forumPageURLFormat;
	}
	public String getForumSubpageURLFormat() {
		return forumSubpageURLFormat;
	}
	public void setForumSubpageURLFormat(String forumSubpageURLFormat) {
		this.forumSubpageURLFormat = forumSubpageURLFormat;
	}
	public String getLevelSwitch() {
		return levelSwitch;
	}
	public void setLevelSwitch(String levelSwitch) {
		this.levelSwitch = levelSwitch;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public Long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	public Long getCreateUser() {
		return createUser;
	}
	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
	}
	public Long getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(Long updateUser) {
		this.updateUser = updateUser;
	}
	public Long getStatus() {
		return status;
	}
	public void setStatus(Long status) {
		this.status = status;
	}
}
