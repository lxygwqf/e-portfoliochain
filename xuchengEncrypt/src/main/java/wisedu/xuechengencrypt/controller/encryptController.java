package wisedu.xuechengencrypt.controller;

import com.alibaba.fastjson.JSON;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wisedu.xuechengencrypt.bean.Certificate;
import wisedu.xuechengencrypt.encrypt.Util;
import wisedu.xuechengencrypt.service.ProcessCertService;
import wisedu.xuechengencrypt.service.SmEncryptService;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;
import java.util.UUID;

@RestController(value = "encrypt")
public class encryptController {


    @Autowired
    private SmEncryptService smEncryptService;

    @Autowired
    private ProcessCertService processCertService;

    @Autowired
    private Certificate certificate;

    @Value("#{T(System).currentTimeMillis()}")
    public Long result ;



    @RequestMapping(value = "/test")
    public void test(){
        System.out.println(result);
    }
//初始化待上链证书
    @RequestMapping(value = "/Initcert",method = RequestMethod.GET)
    public ResponseEntity<String> InitCert(@RequestParam(value = "plainText") String plainText) throws IOException {

        long startTime=System.nanoTime();
        String plaintext = URLDecoder.decode(plainText,"utf-8");
        //从目录下读取json格式的数字证书，更换,为;
//        String plainText = ProcessCertService.readJsonCert(path);

        //对证书追加学校公钥和学校签名
        // 国密规范测试公钥
        System.out.println("plainText:" + plaintext);
        String sign = smEncryptService.sm2SignCert(plaintext);
        System.out.println("sign:" + sign);
        String signedCert = processCertService.addSchoolSign(sign, plaintext);

        //使用sm3生成该证书内容的哈希值
        String hash = smEncryptService.sm3CreateHash(signedCert);

        //使用sm4中的CBC模式对称加密数字证书
        String cipherText = smEncryptService.sm4EncryptCert(signedCert);
        System.out.println("cipherText: " + cipherText);

        //生成uuid
        UUID uuid  =  UUID.randomUUID();

        String response = uuid+ "," + hash +"," + cipherText;

        long endTime=System.nanoTime(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime-startTime)+"ns");
        return new ResponseEntity<String>(response,HttpStatus.OK);
    }




    @RequestMapping(value="/sm2VerifyCert", method = RequestMethod.GET)
    public  ResponseEntity<String> VerifySign(@RequestParam(value = "CipherText") String CipherText) throws IOException{

//        解密后的证书 先对CipherText解码
//        只能用于javasdkdemo调用可成功 本项目独立运行失败 除非去掉下面的第一第二行
        String cipherText = URLDecoder.decode(CipherText,"utf-8");
        System.out.println("CiperText:" + cipherText);
//        返回SM4解密后的证书内容
        String decryptCert = smEncryptService.sm4DecryptCert(cipherText);
        System.out.println("解密后的内容;" + decryptCert);

//        String[] params = decryptCert.split(";");
//        String plainText = params[0]+"," +params[1]+"," + params[2]+ "," + params[3]+"}";
        String[] params = decryptCert.split("&");
        String plainText = params[0] + "}";
        System.out.println(plainText);

        //使用sm3生成该证书内容的哈希值  cipherText:{证书原件 学校签名}
        String hash = smEncryptService.sm3CreateHash(decryptCert);

        String back = plainText + "&" + smEncryptService.sm2VerifySign(decryptCert) +"&"+  hash;
        System.out.println("back:" + back);

        return new ResponseEntity<String>(back,HttpStatus.OK);

//        if(smEncryptService.sm2VerifySign(decryptCert)){
//            System.out.println(true);
//            return ResponseEntity.status(200).body("true");
//
//        }
//        else
//            return ResponseEntity.status(300).body("false");
    }

    //SM4解密
    @RequestMapping(value="/sm4DecryptCert", method = RequestMethod.GET)
    public  ResponseEntity<String> sm4DecryptCert(@RequestParam(value = "CipherText") String CipherText) throws IOException{


        String cipherText = URLDecoder.decode(CipherText,"utf-8");
        //System.out.println("CiperText:" + cipherText);
//        返回SM4解密后的证书内容
        String decryptCert = smEncryptService.sm4DecryptCert(cipherText);
        System.out.println("解密后的证书内容;" + decryptCert);

        return ResponseEntity.status(200).body(decryptCert);
    }


    //为证书添加学生签名
    @RequestMapping(value="/addStudentSigntoCert", method = RequestMethod.GET)
    public ResponseEntity<String> addStudentSigntoCert(@RequestParam(value = "plainText") String plainText) throws IOException {

        //对机构进行签名
        String sign = smEncryptService.sm2StudentSignCert(plainText);

        String response = sign;

        return new ResponseEntity<String>(response,HttpStatus.OK);
    }

    //验证学生的签名
    @RequestMapping(value="/sm2VerifyStudentSignCert", method = RequestMethod.POST)
    public  ResponseEntity<String> sm2VerifyStudentSignCert(@RequestParam(value = "SignCert") String SignCert,
                                                            @RequestParam(value = "Sign") String Sign,
                                                            @RequestParam(value = "StudentPK") String StudentPK) throws IOException{

        //System.out.println(smEncryptService.sm2VerifyStudentSign(SignCert, Sign, StudentPK));
        if(smEncryptService.sm2VerifyStudentSign(SignCert, Sign, StudentPK)){
            System.out.println(true);
            return ResponseEntity.status(200).body("true");
        }
        else
            return ResponseEntity.status(300).body("false");
        //return ResponseEntity.status(200).body("true");
    }

    //生成机构的DID
    @RequestMapping(value="/GenerateDID", method = RequestMethod.GET)
    public ResponseEntity<String> GenerateDID(@RequestParam(value = "InstitutionId") String InstitutionId) throws IOException {
        String str = InstitutionId;
        if (InstitutionId.length() <= 6){
            for ( int i = 6 - InstitutionId.length(); i > 0; i--){
                str += "0";
            }
        }
        Random rand = new Random();
        int a = rand.nextInt(10000);
        String s = str + String.valueOf(a);
        System.out.println("s:" + s);
        //System.out.println(str);
        //注意公钥可能需要输入
        String pubkS = "044a77c33fa976ddab1d8e2ad05694f01151ed39892832947fbcb4a89199db72bc5db91b29616009f0b504459ad72f97b078cf35aebd32b6066003dd81db9a3244";
        String hash = smEncryptService.sm3CreateHash(s + pubkS);

        String DID = new String(Base64.encode(Util.hexToByte(hash)));

        System.out.println("EOS57cvmveSeGpfcLC9guzoRsF4q95CWJKCLZzME5tnKYvWf5Bja1");
        System.out.println(DID);
        //System.out.println(orginal);

        String response = DID;
        return new ResponseEntity<String>(response,HttpStatus.OK);
    }


}
