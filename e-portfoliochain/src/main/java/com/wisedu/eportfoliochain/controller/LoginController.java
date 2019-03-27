package com.wisedu.eportfoliochain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class LoginController {

	// 映射"/"请求
	@RequestMapping("/")
	public String login(Model model){
		System.out.println("LoginController index方法被调用......");
		// 根据Thymeleaf默认模板，将返回resources/templates/login.html
		return "myindex";
	}
}
