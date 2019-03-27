package com.wisedu.blockchain.controller;


import com.wisedu.blockchain.service.ChaincodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import com.wisedu.blockchain.controller.ChaincodeController;
import com.wisedu.blockchain.service.ProcessCertService;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

//import org.springframework.security.access.prepost.PreAuthorize;

@Controller
public class CertController {

    @Autowired
    private RestTemplate rest;

    @Value("${service.url}")
    private String serviceUrl;

    @Autowired
    private ProcessCertService processCertService;

    @Autowired
    private ChaincodeService chaincodeService;




    //证书上链
    //从e-portfoliochain传certificate相关参数
    @RequestMapping(value = "/admin/InitCert",method = RequestMethod.GET)
    public ResponseEntity<String> InitCert(@RequestParam( value = "certificate") String certificate) throws IOException {

        String[] certificates = certificate.split(",");
        String IdentityID = certificates[0];
        String SchoolID = certificates[1];
        String StudentID = certificates[2];
        String CertName = certificates[3];
        String CertCategory = certificates[4];
        String path = certificates[5];

        System.out.println(serviceUrl);
        long startTime = System.nanoTime();

        String ss = processCertService.readJsonCert(path);
        String plainText = ss.substring(0,ss.length()-1);
        System.out.println("plainText:"+plainText);
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("plainText",plainText);


//        下面必须使用{plaintext}这种用法 否则报错
        String plainText1 = URLEncoder.encode(plainText,"utf-8");
        String svcUrl = serviceUrl + "/Initcert?plainText={plainText1}";
        ResponseEntity<String> responseEntity = rest.getForEntity(svcUrl, String.class, plainText1);
        String response = responseEntity.getBody();

        String[] params1 = response.split(",");
        String uuid = params1[0];
        System.out.println(uuid);
        String HashValue = params1[1];
        String certContent = params1[2];
        String ChaincodeFunction = "InitCert";
        String[] ChaincodeArgs =new String[]{uuid, IdentityID, SchoolID,StudentID,CertName,CertCategory,HashValue,certContent};
       //
        String responseOfInvoke = chaincodeService.invokeChaincode(ChaincodeFunction, ChaincodeArgs);
        System.out.println("responseOfiNVOKE: " + responseOfInvoke);
        String result = responseOfInvoke + "&" + uuid;
        return new ResponseEntity<String>(result, HttpStatus.OK);
    }

    //查询证书记录所有元素
    //根据学生学校id和学号来查询
    @RequestMapping(value="/user/ShowRecordByStudentId",method = RequestMethod.GET)
    public ResponseEntity<String> ShowRecord(@RequestParam(value = "schoolid") String schoolid,
                                             @RequestParam(value = "studentid")String studentid) throws Exception {

        String result = chaincodeService.queryChaincode("QueryCertByStudent",new String[]{schoolid,studentid});
        System.out.println("查询内容：" + result);
        return new ResponseEntity<String>(result, HttpStatus.OK);

    }
// 根据uuid查询证书
    @RequestMapping(value="/user/ShowRecordByUuid",method = RequestMethod.GET)
    public ResponseEntity<String> ShowRecord(@RequestParam(value = "uuid") String uuid) throws Exception {

        String result = chaincodeService.queryChaincode("QueryCertByUUID",new String[]{uuid});
        System.out.println("查询内容：" + result);
        return new ResponseEntity<String>(result, HttpStatus.OK);

    }

    //通过学生身份证号查询证书记录所有元素
//    IdentityID
    @RequestMapping(value="/user/ShowRecordByIdentityID",method = RequestMethod.GET)
    public ResponseEntity<String> ShowRecordByIdentityID(@RequestParam(value = "IdentityID") String identityid) throws Exception {
        String result = chaincodeService.queryChaincode("QueryCertByIdentityID",new String[]{identityid});
        System.out.println("查询内容：" + result);
        return new ResponseEntity<String>(result, HttpStatus.OK);

    }


//    验证证书，对以下内容加密{原件，学校签名，学生签名，uuid}后的内容传来，验证证书

    @RequestMapping(value = "/admin/verifysign",method = RequestMethod.GET)
    public ResponseEntity<String> VerifySign(@RequestParam(value = "cipherText") String cipherText,
                                             @RequestParam(value = "uuid") String uuid) throws IOException{

//        先转码
//        String CipherText = URLEncoder.encode(cipherText,"utf-8");
        //将加密后的内容传到XuechengEnrypt
        String svcUrl = serviceUrl + "/sm2VerifyCert?CipherText="+ cipherText;
        System.out.println(svcUrl);
        ResponseEntity<String> responseEntity = rest.getForEntity(svcUrl, String.class);
        String  response = responseEntity.getBody();
        System.out.println("response;" + response);
//        分割传回的参数
        String[] params = response.split("&");
//        得到明文
        String plainText = params[0];
//        得到验证学校签名结果
        String back = params[1];
//        得到哈希值
        String hashValue = params[2];
        System.out.println("back:" + back);
        String result = chaincodeService.invokeChaincode("ValidateHash",new String[]{uuid,hashValue});
        System.out.println("Result:" + result);
        if(back.equals("true")&& result.equals("equals")){

            return new ResponseEntity<String>("true" + "&" +plainText, HttpStatus.OK);
        }
        else
            return ResponseEntity.status(300).body("false");

    }

    //证书绑定 就是将证书状态改为绑定状态
    //    uuid = 184a752a-5b89-4792-a910-fab0d8a53132
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/admin/BindCert",method = RequestMethod.POST)
    public ResponseEntity<String> BindCert(@RequestParam(value = "uuid") String uuid) throws IOException {

        long startTime = System.nanoTime();


        long endTime = System.nanoTime(); //获取结束时间
        System.out.println("程序运行时间： " + (endTime - startTime) + "ns");
        String result = chaincodeService.invokeChaincode("BindCert", new String[]{uuid});
        System.out.println("查询内容：" + result);
        String  bind = chaincodeService.queryChaincode("QueryCertByUUID",new String[]{uuid});
        return ResponseEntity.status(200).body("绑定成功" + bind);
    }

    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }
}

