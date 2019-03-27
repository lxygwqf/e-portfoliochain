package com.wisedu.eportfoliochain.controller;

import com.sun.org.apache.xpath.internal.SourceTree;
import com.tencent.xinge.XingeApp;
import com.wisedu.eportfoliochain.bean.Certificate;
import com.wisedu.eportfoliochain.bean.LoginUser;
import com.wisedu.eportfoliochain.bean.StudentUser;
import com.wisedu.eportfoliochain.service.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


// Controller注解用于指示该类是一个控制器
@org.springframework.stereotype.Controller
public class ServiceController {

	@Value("${service.url}")
	private String serviceUrl;

	@Value("${service.idurl}")
	private String serviceidUrl;

	@Value("${service.encrypturl}")
	private String encrypturl;

	@Autowired
	private RestTemplate rest;

	@Autowired
	private Certificate certificate;

	@Resource
	private SchoolUserService schooluserService;

	@Resource
	private StudentUserService studentuserService;

	@Autowired
	private SmEncryptService smEncryptService;

	@Autowired
	private ProcessCertService processCertService;

	@Resource
	private GenerateDIDOService generateDIDOService;

	@Autowired
	private LoginUser loginUser;

	//这个变量用于装cookies信息
	private static Cookie cookie;

	@PostMapping("index")
	public String index(HttpServletResponse response,
			@RequestParam("loginName") String loginName,
						@RequestParam("password") String password) {
		System.out.println("ServiceController login方法被调用......");
		System.out.println("ServiceController 登录名:" + loginName + " 密码:" + password);

		//写入cookie
		cookie = new Cookie("login",loginName);
		response.addCookie(cookie);

		loginUser.setInstitutionId(loginName);

		if (password.equals(schooluserService.selectPasswordByUsername(loginName).getPassword())) {

			return "mycenter";
		} else {

			return "login";
		}
	}

	@RequestMapping(value = "/initCert")
	@ResponseBody
	public ModelAndView add(@RequestParam("IdentityID") String IdentityID,
							@RequestParam("SchoolID") String SchoolID,
							@RequestParam("StudentID") String StudentID,
							@RequestParam("CertName") String CertName,
							@RequestParam("CertCategory") String CertCategory,
							@RequestParam("file") MultipartFile file) throws IOException {

		System.out.println("进入add");
		String certificate = "";
		System.out.println("原本为：" + file);
		String path = processCertService.saveJsonCert(file);
		System.out.println("文本路径为：" + path);
		certificate = IdentityID + "," + SchoolID + "," + StudentID + "," + CertName + "," + CertCategory + "," + path;
		String svcUrl = serviceUrl + "/admin/InitCert?certificate=" + certificate;
		ResponseEntity<String> responseEntity = rest.getForEntity(svcUrl, String.class);
		String response = responseEntity.getBody();
		System.out.println(response);
		String result = "上链失败！";
		if(response.split("&")[0].equals("Chaincode invoked successfully")){
			result = "上链成功！";
		}

		String uuid = response.split("&")[1];
		ModelAndView mv = new ModelAndView();
		mv.setViewName("resuleOfInit");
		mv.addObject("result" ,result );
		mv.addObject("uuid",uuid);
		return mv;
	}

	@ResponseBody
	@RequestMapping(value = "/showRecordsByIdentityID")
	public String search(@RequestParam(value = "IdentityID") String IdentityID) throws IOException {
		System.out.println("进入次查询");
		String svcUrl = serviceUrl + "/user/ShowRecordByIdentityID?IdentityID=" + IdentityID;
		ResponseEntity<String> responseEntity = rest.getForEntity(svcUrl, String.class);
		String response1 = responseEntity.getBody();
		System.out.println("response1" + response1);
		String[] params = response1.split(",");
		String certcontent = params[3].split(":")[1];
		System.out.println(certcontent);

		String decryptCert = smEncryptService.sm4DecryptCert(certcontent.substring(1, certcontent.length() - 1));
		String result = decryptCert.split("&")[0] + "}";

		ModelAndView mv = new ModelAndView();
		mv.setViewName("searchResult");
		String str = "您有新的证书待下载，请注意查收";
		//accessId,token需要安卓端输入
		JSONObject jsonObject = XingeApp.pushAllAndroid(2100319549, "371f22710436541bf7a574cd79e205dc", "证书下载", str);
		System.out.println(jsonObject);
		if (jsonObject.getInt("ret_code") != 0) {
			return "erro";
		} else
			return result;

	}

	@RequestMapping(value = "showRecordsByStudentId")
	public ModelAndView searchByStudentId(@RequestParam(value = "schoolid") String schoolid,
										  @RequestParam(value = "studentid") String studentid) throws IOException {
		System.out.println("进入查询2");
		System.out.println(schoolid + "," + studentid);
		String svcUrl = serviceUrl + "/user/ShowRecordByStudentId?schoolid=" + schoolid + "&studentid=" + studentid;
		ResponseEntity<String> responseEntity = rest.getForEntity(svcUrl, String.class);
		String response2 = responseEntity.getBody();
		response2 = response2.substring(2,response2.length()-1);
		System.out.println("response2: " + response2);
		ModelAndView mv = new ModelAndView();
		mv.setViewName("searchResult");
		String[] result = response2.split(",");

		HashMap<String,String> map = new LinkedHashMap<>();
		for(int i=0;i<result.length;i++){
			map.put(result[i].split(":")[0],result[i].split(":")[1]);
		}

		String certcontentHash = map.get("\"certcontent\"");
		System.out.println("加密的证书： " + certcontentHash);
		String decryptCert = smEncryptService.sm4DecryptCert(certcontentHash.substring(1, certcontentHash.length() - 1));
		String certcontent = decryptCert.split("&")[0] + "}";
		mv.addObject("IdentiryID", map.get("\"IdentityID\""));
		mv.addObject("binddate", map.get("\"binddate\""));
		mv.addObject("certcategory", map.get("\"certcategory\""));
		mv.addObject("certcontent", certcontent);
		mv.addObject("certname", map.get("\"certname\""));
		mv.addObject("certstate", map.get("\"certstate\""));
		mv.addObject("hashvalue", map.get("\"hashvalue\""));
		mv.addObject("revokereason", map.get("\"revokereason\""));
		mv.addObject("schoolid", map.get("\"schoolid\""));
		mv.addObject("studentid", map.get("\"studentid\""));
		mv.addObject("uuid",map.get("\"uuid\""));
		String validation = result[11].split(":")[1];
		mv.addObject("validation", map.get("\"validation\""));
		return mv;
	}

	//跨校查询，界面啥的都没做,数据库user表变了
	@ResponseBody
	@RequestMapping(value = "showRecordsByUUID")
	public ModelAndView showRecordsByUUID(@CookieValue("login") String login,
										  @RequestParam(value = "UUID") String UUID,
										  @RequestParam(value = "studentid") String studentid) throws IOException {
		System.out.println("当前登录学校为:" + login);

		StudentUser studentUser = studentuserService.selectDIDByStudentID(studentid, "NJU");
		String DID = studentUser.getDid();

		System.out.println("DID为：" + DID);
		System.out.println("进入是否查询");
		String DID2 = URLEncoder.encode(DID, "utf-8");
		String svcUrl = serviceidUrl + "/user/IsAuthorizated?DID=" + DID2 + "&UUID=" + UUID + "&InstitutionId=" + login;
		ResponseEntity<String> responseEntity = rest.getForEntity(svcUrl, String.class);

		String response = responseEntity.getBody();
		System.out.println("是否授权:" + response);

		ModelAndView mv = new ModelAndView();

		if (response.equals("yes")) {
			svcUrl = serviceUrl + "/user/ShowRecordByUuid?uuid=" + UUID;
			responseEntity = rest.getForEntity(svcUrl, String.class);
			response = responseEntity.getBody();
			System.out.println("证书内容:" + response);
			mv.setViewName("thirdSearchResultRt");
			String[] result = response.split(",");
			String certcontentHash = result[3].split(":")[1];
			String decryptCert = smEncryptService.sm4DecryptCert(certcontentHash.substring(1, certcontentHash.length() - 1));
			String certcontent = decryptCert.split("&")[0] + "}";
			mv.addObject("IdentiryID", result[0].split(":")[1]);
			mv.addObject("binddate", result[1].split(":")[1]);
			mv.addObject("certcategory", result[2].split(":")[1]);
			mv.addObject("certcontent", certcontent);
			mv.addObject("certname", result[4].split(":")[1]);
			mv.addObject("certstate", result[5].split(":")[1]);
			mv.addObject("hashvalue", result[6].split(":")[1]);
			mv.addObject("revokereason", result[7].split(":")[1]);
			mv.addObject("schoolid", result[8].split(":")[1]);
			mv.addObject("studentid", result[9].split(":")[1]);
			mv.addObject("uuid", result[10].split(":")[1]);
			String validation = result[11].split(":")[1];
			mv.addObject("validation", validation.substring(0, validation.length() - 1));
		} else {
			mv.setViewName("thirdSearchResultWR");
			mv.addObject("result", "您没有授权！");
		}
		return mv;
	}


	@RequestMapping(value = "authorize")
	public ModelAndView authorize(@RequestParam(value = "UUID") String UUID,
								  @RequestParam(value = "DID") String DID,
								  @RequestParam(value = "InstitutionId") String InstitutionId) throws IOException {
		System.out.println("进入授权操作");
		//String UUID = "0f2d8375-3060-46f2-ad85-d71743a2d6a1";
		//String InstitutionId = "qhu";
		//String urlOfIdChannel = "http://172.20.6.207:8008";
		String svcUrl = serviceidUrl + "/user/AddAuthorization?UUID=" + UUID + "&DID=" + DID + "&InstitutionId=" + InstitutionId;
		System.out.println(svcUrl);
		ResponseEntity<String> responseEntity = rest.getForEntity(svcUrl, String.class);
		String response = responseEntity.getBody();
		System.out.println(response);
		ModelAndView mv = new ModelAndView();
		mv.setViewName("authorizeResult");

		if (response.equals("添加授权成功")) {
			mv.addObject("result", "添加授权成功");
		} else
			mv.addObject("result", "添加授权失败");
		return mv;
	}



	//    cipherText:5wcg9rHv+1Dkoyyv3bJmKAR2Qvwa4/R9+xunGz1fekD7u4vcYCt2SWnlBrLM9ZAHOg8fVMYW1E+IlNx5oqol3K5rj/RlKf/AelMgtoOjeSxEyrpREF8I445AsE3AUOOdvx1pJS2Fulw+QkrItjLlpWyYjTrPAbvIsPc4z8ET82G5LJ47VMv17I3Ayh/zo439fm7CSZO7WAnCkVVQhpOo75FUp3ZnXBk1N+N/HIS3ytehGfaxT/cfgCuI9YWIyxetEoxF78aUavGYHhtu1sxcUCDY2G1QvAeAtcPL5+uS5Y+lToXz6O9VC3qAS3qO3L6O3Dj8menjdi30wqPAC28y1x4cKtDc3zP3KLRGjs/gJRskXvzS2PhgD0nxYtumTrfFwIQ3KvIeKybHNBhbXw+z6oZXKwd5TiMwyoAi+k+M0u/GhS1igAQPXsxJHeF2zjkq8kyeaooVjr8W/52aUXBlm+6vasrbiqXdAM60ecejrAESlXv7HqMsimGqElbYzAZ1
//      hashValue =161a3f760a1c349c0030d7fabf352e711ae214bc7173d3b2abc99774bad43c72
//    uuid = 184a752a-5b89-4792-a910-fab0d8a53132
	@ResponseBody
	@RequestMapping(value = "validate")
	public ModelAndView validate(@RequestParam(value = "cipherText") String cipherText,
								 @RequestParam(value = "studentSign") String studentSign,
								 @RequestParam(value = "uuid") String uuid) throws IOException {
		System.out.println("进入验证");
		String plainText = "";
		ModelAndView mv = new ModelAndView();
		mv.setViewName("resultOfValidate");
		System.out.println("cipherText:" + cipherText);
		String cc = cipherText.replace(" ","+");
//		cipherText 是客户端对{证书原件 ， 用学校公钥对证书原件签名 }再加密后的内容
		String CipherText = URLEncoder.encode(cc, "utf-8");
		String svcUrl = serviceUrl + "/admin/verifysign?cipherText=" + CipherText + "&uuid=" + uuid;
		System.out.println("svcUrl:" + svcUrl);
		ResponseEntity<String> responseEntity = rest.getForEntity(svcUrl, String.class);
		String responseOfProfile = responseEntity.getBody();
		System.out.println("responseOfProfile" + responseOfProfile);
		if (responseOfProfile.equals("false")) {
			mv.addObject("step1", "验证学校签名和证书哈希值结果为" + responseOfProfile.split("&")[0]);
		} else {
			//			plainText 是明文
			mv.addObject("step1", "验证学校签名和证书哈希值结果为" + responseOfProfile.split("&")[0]);
			plainText = responseOfProfile.split("&")[1];
			System.out.println(plainText);
//			String plainText2 = plainText.replace(",", ";");
//
//		身份链逻辑
			String DID = "+PdSGNWibxXVQHURFozPMEAYi2Gxo2J7Z4PAagxT8FE=";
			String plainTextToId = smEncryptService.sm4EncryptCert(plainText + "&" + studentSign + "&" + DID);
			System.out.println("plainTextToId;" + plainTextToId);
			System.out.println(plainText + "&" + studentSign + "&" + DID);
//		转码
			String plainTextToIdEncode = URLEncoder.encode(plainTextToId, "utf-8");
//		要传给身份链springboot的是 {证书原件 ， 学生签名}再加密的内容 cipherTextOfStudent
			String svcUrlOfId = serviceidUrl + "/user/VerifyStudentSign?CipherText=" + plainTextToIdEncode;
			ResponseEntity<String> responseEntityOfIdChannel = rest.getForEntity(svcUrlOfId, String.class);
			String responseOfIdChannel = responseEntityOfIdChannel.getBody();
			System.out.println("responseOfIdChannel:" + responseOfIdChannel);

			if (responseOfIdChannel.equals("true"))
				mv.addObject("step2", "验证学生签名结果为" + responseOfIdChannel);
//		mv.addObject("result2",responseOfProfile);
		}

		return mv;

	}


//	身份链
//返回json数据
	@ResponseBody
	@RequestMapping("/studentindex")
	public String studentindex(@RequestParam("mobile") String mobile,
							   @RequestParam("password") String password) throws IOException{
		System.out.println("studentIndexController login方法被调用......");
		System.out.println("studentIndexController 登录名:"+mobile + " 密码:" + password);

		StudentUser studentUser=studentuserService.selectuserStudentByStudentMobile(mobile);
		if ( studentUser!= null){
			if (password.equals(studentUser.getPassword())){
				// 重定向到到main请求
				//System.out.println("经过判断");
				if (studentUser.getIdCard()!= null){
					System.out.println("yes,"+studentUser.getUsername()+","+studentUser.getIdCard());
					return "yes,"+studentUser.getUsername()+","+studentUser.getIdCard();
				} else {
					return "success";
				}
			} else {
				System.out.println("error");
				return "error";
			}
		} else {
			System.out.println("notexist");
			return "notexist";
		}

	}


	//DIDO上链
	@ResponseBody
	@RequestMapping(value = "InitDIDO")
	public String InitDIDO(@RequestParam(value = "idCard") String idCard) throws IOException {
		//生成DID
		String svcUrl = encrypturl + "/GenerateDID?InstitutionId=" + "NJU";
		System.out.println(svcUrl);
		ResponseEntity<String> responseEntity = rest.getForEntity(svcUrl, String.class);
		String response = responseEntity.getBody();
		System.out.println("DID为：" + response);


		String DID = response;
		String InstitutionId = "NJU";
		System.out.println("进入DIDO上链操作");
		//记得did1.json里面内容得先写好，尤其是DID
//		调用马璇服务器，需要写马璇服务器地址，但是更新的文件在本地。所以目前这个功能我这里无法实现。除非下载
		String path = "/home/xuli/projects/HW/did10.json";
		int a  = generateDIDOService.GenerateDIDO(DID);


		//DID存入数据库
		if (studentuserService.updateDID(DID, InstitutionId, idCard) != 0) {
			svcUrl = serviceidUrl + "/admin/InitDIDO?path=" + path;
			System.out.println(svcUrl);
			responseEntity = rest.getForEntity(svcUrl, String.class);
			response = responseEntity.getBody();
			System.out.println(response);
			if (response.equals("添加成功")) {
				return "success";
			}
		}
		return "error";

	}

	@Bean
	public RestTemplate rest() {
		return new RestTemplate();
	}
}
