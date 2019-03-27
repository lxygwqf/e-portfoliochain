package com.wisedu.eportfoliochain.controller;

import com.wisedu.eportfoliochain.service.StudentUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class RegisterController {
    @Resource
    private StudentUserService studentuserService;

    @ResponseBody
    @RequestMapping("/studentregister")
    public String studentregister(@RequestParam("mobile") String mobile,
                                  @RequestParam("password") String password){
        System.out.println("studentregister方法被调用......");
        System.out.println("studentregister 登录名:"+mobile + " 密码:" + password);

        if (studentuserService.insertStudentGetKey(mobile, password) != 0){
            return "success";
        } else {
            return "error";
        }
    }

    @ResponseBody
    @RequestMapping("/studentidentification")
    public String studentidentification(@RequestParam("mobile") String mobile,
                                        @RequestParam("username") String username,
                                        @RequestParam("idCard") String idCard) {
        System.out.println("studentregister方法被调用......");
        System.out.println("studentregister 登录名:" + mobile + "实名:" + username + " 身份证号:" + idCard);

        if (studentuserService.updateStudentIDCardGetKey(username, idCard, mobile) != 0) {
            return "success";
        } else {
            return "error";
        }
    }

    @ResponseBody
    @RequestMapping("/studentupdatepassword")
    public String studentupdatepassword(@RequestParam("mobile") String mobile,
                                        @RequestParam("password") String password) {
        System.out.println("studentregister方法被调用......");
        System.out.println("studentregister 登录名:" + mobile + " 重置后的密码:" + password);

        if (studentuserService.updateStudentPasswordGetKey(password, mobile) != 0) {
            return "success";
        } else {
            return "error";
        }
    }
}