package org.cabbage.crawler.reaper.coordinator.das.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "nsdomain")
public class NSDomainBean implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 5783429694764653667L;

	@Column(name = "id")
	private Long ID;

	@Column(name = "rid")
	private String rid;

	@Column(name = "domain")
	private String domain;

	@Column(name = "domainname")
	private String domainname;

	@Column(name = "domainName_en")
	private String domainName_en;

	@Column(name = "domainurl")
	private String domainurl;

	@Column(name = "level")
	private Long level;

	@Column(name = "pdomain")
	private String pdomain;
	
	@Column(name = "rootdomain")
	private String rootdomain;

	@Column(name = "fullcover")
	private Long fullcover;
	
	@Column(name = "domaintype")
	private Long domaintype;

	@Column(name = "SEO")
	private Long SEO;
	
	@Column(name = "SITEAREA")
	private String SITEAREA;

	@Column(name = "SITEAREACODE")
	private Long SITEAREACODE;

	@Column(name = "siteClassificationID")
	private Long siteClassificationID;

	@Column(name = "ICP_NO")
	private String ICP_NO;

	@Column(name = "ICP_NAME")
	private String ICP_NAME;

	@Column(name = "ICP_AREA")
	private String ICP_AREA;

	@Column(name = "ICP_AREACODE")
	private Long ICP_AREACODE;

	@Column(name = "ipLong")
	private Long ipLong;

	@Column(name = "ipString")
	private String ipString;

	@Column(name = "ipAddr")
	private String ipAddr;

	@Column(name = "ipAreaCode")
	private Long ipAreaCode;

	@Column(name = "languageID")
	private Long languageID;
	
	@Column(name = "mediaClassificationID")
	private Long mediaClassificationID;

	@Column(name = "weight")
	private Long weight;

	@Column(name = "updatetime")
	private Long updatetime;

	@Column(name = "updateuser")
	private String updateuser;

	@Id
	public Long getID() {
		return ID;
	}

	public void setID(Long id) {
		ID = id;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDomainname() {
		return domainname;
	}

	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}

	public String getDomainName_en() {
		return domainName_en;
	}

	public void setDomainName_en(String domainName_en) {
		this.domainName_en = domainName_en;
	}

	public String getDomainurl() {
		return domainurl;
	}

	public void setDomainurl(String domainurl) {
		this.domainurl = domainurl;
	}

	public Long getLevel() {
		return level;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	public String getPdomain() {
		return pdomain;
	}

	public void setPdomain(String pdomain) {
		this.pdomain = pdomain;
	}

	public String getRootdomain() {
		return rootdomain;
	}

	public void setRootdomain(String rootdomain) {
		this.rootdomain = rootdomain;
	}

	public Long getFullcover() {
		return fullcover;
	}

	public void setFullcover(Long fullcover) {
		this.fullcover = fullcover;
	}

	public Long getDomaintype() {
		return domaintype;
	}

	public void setDomaintype(Long domaintype) {
		this.domaintype = domaintype;
	}

	public Long getSEO() {
		return SEO;
	}

	public void setSEO(Long sEO) {
		SEO = sEO;
	}

	public String getSITEAREA() {
		return SITEAREA;
	}

	public void setSITEAREA(String sITEAREA) {
		SITEAREA = sITEAREA;
	}

	public Long getSITEAREACODE() {
		return SITEAREACODE;
	}

	public void setSITEAREACODE(Long sITEAREACODE) {
		SITEAREACODE = sITEAREACODE;
	}

	public Long getSiteClassificationID() {
		return siteClassificationID;
	}

	public void setSiteClassificationID(Long siteClassificationID) {
		this.siteClassificationID = siteClassificationID;
	}

	public String getICP_NO() {
		return ICP_NO;
	}

	public void setICP_NO(String iCP_NO) {
		ICP_NO = iCP_NO;
	}

	public String getICP_NAME() {
		return ICP_NAME;
	}

	public void setICP_NAME(String iCP_NAME) {
		ICP_NAME = iCP_NAME;
	}

	public String getICP_AREA() {
		return ICP_AREA;
	}

	public void setICP_AREA(String iCP_AREA) {
		ICP_AREA = iCP_AREA;
	}

	public Long getICP_AREACODE() {
		return ICP_AREACODE;
	}

	public void setICP_AREACODE(Long iCP_AREACODE) {
		ICP_AREACODE = iCP_AREACODE;
	}

	public Long getIpLong() {
		return ipLong;
	}

	public void setIpLong(Long ipLong) {
		this.ipLong = ipLong;
	}

	public String getIpString() {
		return ipString;
	}

	public void setIpString(String ipString) {
		this.ipString = ipString;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public Long getIpAreaCode() {
		return ipAreaCode;
	}

	public void setIpAreaCode(Long ipAreaCode) {
		this.ipAreaCode = ipAreaCode;
	}

	public Long getLanguageID() {
		return languageID;
	}

	public void setLanguageID(Long languageID) {
		this.languageID = languageID;
	}

	public Long getMediaClassificationID() {
		return mediaClassificationID;
	}

	public void setMediaClassificationID(Long mediaClassificationID) {
		this.mediaClassificationID = mediaClassificationID;
	}

	public Long getWeight() {
		return weight;
	}

	public void setWeight(Long weight) {
		this.weight = weight;
	}

	public Long getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Long updatetime) {
		this.updatetime = updatetime;
	}

	public String getUpdateuser() {
		return updateuser;
	}

	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}



}
