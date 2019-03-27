package com.wisedu.eportfoliochain.bean;

import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class LoginUser implements Serializable {
    public String InstitutionId ;

    public String getInstitutionId() {
        return InstitutionId;
    }

    public void setInstitutionId(String institutionId) {
        InstitutionId = institutionId;
    }
}