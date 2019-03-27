package wisedu.xuechengencrypt.bean;

import org.springframework.stereotype.Component;

@Component
public class Certificate {
    //证书名称
    public  String name;
    //证书创建时间
    public  String createDate;
    //证书内容

    //学生姓名
    public String studentName;
    //学号
    public String studentId;
    //专业
    public String major;
    //毕业时间
    public  String graduateDate;
    //学科类别
    public  String discipline;
    //学位类别
    public  String degreeLevel;
    //颁发机构
    public  String issuer;
    //颁发时间
    public  String issuingDate;

    public String getName() {
        return name;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getMajor() {
        return major;
    }

    public String getGraduateDate() {
        return graduateDate;
    }

    public String getDiscipline() {
        return discipline;
    }

    public String getDegreeLevel() {
        return degreeLevel;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getIssuingDate() {
        return issuingDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setGraduateDate(String graduateDate) {
        this.graduateDate = graduateDate;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public void setDegreeLevel(String degreeLevel) {
        this.degreeLevel = degreeLevel;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setIssuingDate(String issuingDate) {
        this.issuingDate = issuingDate;
    }
}