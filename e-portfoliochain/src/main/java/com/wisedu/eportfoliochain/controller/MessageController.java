package com.wisedu.eportfoliochain.controller;

import com.tencent.xinge.XingeApp;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class MessageController {

    //没有测试过
    //机构发送请求授权消息
    @ResponseBody
    @RequestMapping(value = "/messagepushing", method = RequestMethod.GET)
    public String messagepushing(@RequestParam("InstitutionId") String InstitutionId,
                                 @RequestParam("CertName") String CertName) throws IOException {

        System.out.println("进入messagepushing");
        //没有传签名，需要修改
        String str = InstitutionId + "请求查看您的" + CertName + "证书！";
        //accessId,token需要安卓端输入
        JSONObject jsonObject = XingeApp.pushAllAndroid(2100319549, "371f22710436541bf7a574cd79e205dc", "授权申请", str);
        System.out.println(jsonObject);
        if (jsonObject.getInt("ret_code") != 0){
            return "error";
        } else {
            return "success";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/InstitutionIdpushing")
    public String authorize() throws IOException{
        System.out.println("推送机构和证书名");

        return "东南大学,南京大学2017届毕业证书";

    }

}