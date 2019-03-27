package com.wisedu.eportfoliochain.bean;

import org.springframework.stereotype.Component;

@Component
public class Certificate {
//    uuid, IdentityID, SchoolID,StudentID,CertName,CertCategory,HashValue,certContent
    private String IdentityID;
    private String SchoolID;
    private String StudentID;
    private String CertName;
    private String CertCategory;
    private String path;

    public void setIdentityID(String identityID) {
        IdentityID = identityID;
    }

    public void setSchoolID(String schoolID) {
        SchoolID = schoolID;
    }

    public void setStudentID(String studentID) {
        StudentID = studentID;
    }

    public void setCertName(String certName) {
        CertName = certName;
    }

    public void setCertCategory(String certCategory) {
        CertCategory = certCategory;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIdentityID() {
        return IdentityID;
    }

    public String getSchoolID() {
        return SchoolID;
    }

    public String getStudentID() {
        return StudentID;
    }

    public String getCertName() {
        return CertName;
    }

    public String getCertCategory() {
        return CertCategory;
    }

    public String getPath() {
        return path;
    }
}