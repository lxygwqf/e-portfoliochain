package com.wisedu.eportfoliochain.bean;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;

import java.io.Serializable;

public class StudentUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id ;
    private String username ;
    private String password;
    private String mobile;
    private String idCard;
    private String studentid;
    private String did;
    private String institutionid;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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
    public String getMobile() { return mobile; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getDid() { return did; }
    public void setDid(String did) { this.did = did; }
    public String getInstitutionid() { return institutionid; }
    public void setInstitutionid(String institutionid) { this.institutionid = institutionid; }
    public String getStudentid() { return studentid; }
    public void setStudentid(String studentid) { this.studentid = studentid; }
}