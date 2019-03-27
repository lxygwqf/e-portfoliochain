package com.wisedu.blockchain.controller;

import com.wisedu.blockchain.service.ChaincodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLDecoder;

@Controller
public class DIDController {
    @Autowired
    ChaincodeService chaincodeService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.url}")
    private String serviceUrl;

    //DIDO生成并上链
    @RequestMapping(value = "/admin/InitDIDO", method = RequestMethod.POST)
    public ResponseEntity<String> InitDIDO(@RequestParam(value = "path") String path) throws IOException {

        String plainText = chaincodeService.readJsonDIDO(path);

        String[] sourceStrArray = plainText.split(";");
        System.out.println("sourceStrArray11:" + sourceStrArray[10]);
        String[] originalStr = new String[13];
        for (int i = 0; i < 13; i++) {
            String[] originalStrArray = sourceStrArray[i].split(":");
            if (i == 10) {
                originalStr[i] = originalStrArray[2].substring(1, originalStrArray[2].length() - 1);
                //System.out.println("originalStrArray" + i + ":" + originalStr[i]);
            } else {
                originalStr[i] = originalStrArray[1].substring(1, originalStrArray[1].length() - 1);
                //System.out.println("originalStrArray" + i + ":" + originalStr[i]);
            }
        }
        String DID = originalStr[0];
        String EnrolmentDay = originalStr[1];
        String SchoolId = originalStr[2];
        String AcademyId = originalStr[3];
        String DepartmentId = originalStr[4];
        String GradeId = originalStr[5];
        String ClassId = originalStr[6];
        String StudentId = originalStr[7];
        String SchoolPK = originalStr[8];
        String StudentPK = originalStr[9];
        String uuid = originalStr[10];
        String Certcategory = originalStr[11];
        String Certname = originalStr[12];


        String response = chaincodeService.invokeChaincode("InitDIDO", new String[]{DID, EnrolmentDay, SchoolId, AcademyId, DepartmentId, GradeId, ClassId, StudentId, SchoolPK, StudentPK, uuid, Certcategory, Certname});
        if (response.equals("Chaincode invoked successfully")) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(response);
        }
    }

    //根据DID查询整个DIDO（测试，成功则删）
    // did : 001
    @RequestMapping(value = "/admin/ReadDIDO", method = RequestMethod.GET)
    public ResponseEntity<String> ReadDIDO(@RequestParam(value = "DID") String[] did) throws Exception {
        String response = chaincodeService.queryChaincode("ReadDIDO", did);
        if (!response.equals("Caught an exception while quering chaincode")) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(response);
        }
    }

    //读取身份链中档案列表以及授权信息,需要输入uuid&did(test successful)
    @RequestMapping(value = "/user/ReadProfileByUUID", method = RequestMethod.GET)
    public ResponseEntity<String> ReadProfileByUUID(@RequestParam(value = "DID") String did,
                                                    @RequestParam(value = "UUID") String uuid) throws Exception {
        String response = chaincodeService.queryChaincode("ReadProfileByUUID", new String[]{did, uuid});
        if (!response.equals("Caught an exception while quering chaincode")) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(response);
        }
    }

    //根据did查询did文档.在档案列表中添加档案(test)
    @RequestMapping(value = "/user/AddProfile", method = RequestMethod.POST)
    public ResponseEntity<String> AddProfile(@RequestParam(value = "DID") String did,
                                             @RequestParam(value = "FilePATH") String path) throws IOException {
        String plaintext = chaincodeService.readJsonDIDO(path);
        String[] sourceStrArray = plaintext.split(";");
        String[] originalStr = new String[3];
        for (int i = 0; i < 3; i++) {
            String[] originalStrArray = sourceStrArray[i].split(":");
            originalStr[i] = originalStrArray[1].substring(1, originalStrArray[1].length() - 1);
        }

        String uuid = originalStr[0];
        String certcategory = originalStr[1];
        String certname = originalStr[2];

        chaincodeService.invokeChaincode("AddProfile", new String[]{did, uuid, certcategory, certname});
        String response = chaincodeService.queryChaincode("ReadDIDO", new String[]{did});
        if (!response.equals("Caught an exception while quering chaincode")) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(response);
        }
    }

    //判断是否授权
    //did : did1
    //uuid : uuid1
    //institutionid : seu
    //根据did&uuid&机构id来确定该机构有没有被授权(test)
    @RequestMapping(value = "/user/IsAuthorizated", method = RequestMethod.GET)
    public ResponseEntity<String> IsAuthorizated(@RequestParam(value = "DID") String DID,
                                                 @RequestParam(value = "UUID") String uuid,
                                                 @RequestParam(value = "InstitutionId") String InstitutionId) throws Exception {
        String response = chaincodeService.queryChaincode("IsAuthorizated", new String[]{URLDecoder.decode(DID,
                "utf-8"), uuid, InstitutionId});
        if (!response.equals("Caught an exception while quering chaincode")) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(response);
        }
    }

    //添加授权
    //did : did1
    //uuid :uuid1
    //institutionid : nju
    @RequestMapping(value = "/user/AddAuthorization",method = RequestMethod.GET)
    public ResponseEntity<String> AddAuthorization(@RequestParam(value = "DID") String DID,
                                                   @RequestParam(value= "UUID") String uuid,
                                                   @RequestParam(value= "InstitutionId") String InstitutionId
                                                   //@RequestParam(value = "Signature") String Signature
    ) throws IOException {
        String plainText = InstitutionId;
        //下面必须使用{plaintext}这种用法 否则报错
        String svcUrl = serviceUrl + "/addStudentSigntoCert?plainText={plainText}";
        //System.out.println(svcUrl);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(svcUrl, String.class, plainText);
        String response = responseEntity.getBody();
        System.out.println(response);

        String Signature = response;

        String DID1 = DID.replace(" ", "+");

        String result = chaincodeService.queryChaincode("AddAuthorization", new String[]{DID1, uuid, InstitutionId, Signature});
        if (!result.equals("Caught an exception while quering chaincode")) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(result);
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(result);

        }
    }

    //根据did&uuid获得指定档案的授权列表
    @RequestMapping(value="/user/ReadAuthorizationListByUUID",method = RequestMethod.GET)
    public ResponseEntity<String> ReadAuthorizationListByUUID(@RequestParam(value = "DID") String DID,
                                                              @RequestParam(value = "UUID") String uuid) throws Exception {
        String response=chaincodeService.queryChaincode("ReadAuthorizationListByUUID",new String[]{DID, uuid});
        if (!response.equals("Caught an exception while quering chaincode")) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(response);
        }
    }
    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }

}
