package org.cabbage.crawler.reaper.coordinator.das.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "nsproxy")
public class NSProxyBean implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4878375605002902121L;

	@Column
	private Long proxyID;
	@Column
	private Long IP;
	@Column
	private Long port;
	@Column
	private Long type;
	@Column
	private String userName;
	@Column
	private String password;
	@Column
	private Long available;
	@Column
	private Long proxyType;

	@Id
	public Long getProxyID() {
		return proxyID;
	}

	public void setProxyID(Long proxyID) {
		this.proxyID = proxyID;
	}

	public Long getIP() {
		return IP;
	}

	public void setIP(Long iP) {
		IP = iP;
	}

	public Long getPort() {
		return port;
	}

	public void setPort(Long port) {
		this.port = port;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getAvailable() {
		return available;
	}

	public void setAvailable(Long available) {
		this.available = available;
	}

	public Long getProxyType() {
		return proxyType;
	}

	public void setProxyType(Long proxyType) {
		this.proxyType = proxyType;
	}

}
