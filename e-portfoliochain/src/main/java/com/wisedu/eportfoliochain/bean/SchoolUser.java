package com.wisedu.eportfoliochain.bean;

import java.io.Serializable;

public class SchoolUser implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id ;
	private String loginName ;
	private String username ;
	private String password;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
