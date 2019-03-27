package wisedu.xuechengencrypt.service.Impl;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;
import wisedu.xuechengencrypt.encrypt.SM2Utils;
import wisedu.xuechengencrypt.encrypt.SM3Digest;
import wisedu.xuechengencrypt.encrypt.SM4Utils;
import wisedu.xuechengencrypt.encrypt.Util;
import wisedu.xuechengencrypt.service.SmEncryptService;

import java.io.IOException;

@Service
public class SmEncryptServiceImpl implements SmEncryptService {
    //使用SM2对证书内容进行签名，签名追加到证书后面

    public String sm2SignCert(String cert) throws IOException {
//        {"studentId":"220171634","graduateDate":"2016/06/25","degreeLevel":"学士学位","major":"计算机科学与技术","issuingDate":"2016/06/25","studentName":"李铭","name":"毕业证书","discipline":"工科","issuer":"东南大学","createDate":"2016/06/25"}
        //String plainText = "{\"certID\":\"2013061367895\";\"name\":\"zhangsan\";\"publisher\":\"SEU\";\"image:0ff2384rhfdjaso8jhshdwqeouowqdcmweunq38ryvn98\"}";
        String plainText = cert;
        //plainText="{certID:2013061367895}";
        byte[] sourceData = plainText.getBytes();
        System.out.println("加密用的cert=>" + plainText);

        // 国密规范测试私钥
        // 学校私钥
        String prik = "58967e2beb6fffd3c96545eebd3000b39c10087d48faa0d41f9c7bf3720e0ea4";
        String prikS = new String(Base64.encode(Util.hexToByte(prik)));

        // 国密规范测试用户ID
        String userId = "ALICE123@YAHOO.COM";

        //用id和私钥签名fff
        byte[] c = SM2Utils.sign(userId.getBytes(), Base64.decode(prikS.getBytes()), sourceData);

        return Util.getHexString(c);
    }

    //验证签名
    public boolean sm2VerifySign(String signedCert) throws IOException {
        // 国密规范测试用户ID
        String userId = "ALICE123@YAHOO.COM";

//        String[] sourceArray = signedCert.split(";");
//        String pubkS = sourceArray[4].substring(15, sourceArray[4].length() - 1);
//        System.out.println("pubk:" + pubkS);
//        String sign = sourceArray[5].substring(8, sourceArray[5].length() - 3);
//        System.out.println("sign:" + sign);
//        String cert = sourceArray[0] + ";" + sourceArray[1] + ";" + sourceArray[2] + ";" + sourceArray[3] + "}\n";
//        System.out.println("cert:" + cert);


        String[] sourceArray = signedCert.split("&");
        String cert = sourceArray[0] + "}";
        String pubkSJson = sourceArray[1].split(":")[1];
        String pubkS = pubkSJson.substring(1,pubkSJson.length()-1);
        String signJson = sourceArray[2].split(":")[1];
        System.out.println("\"".length());
        String sign = signJson.substring(1,signJson.length()-2);
        System.out.println("pubk:" + pubkS);
        System.out.println("sign:" + sign);
        System.out.println("解密用的cert=》" + cert);
        System.out.println("现长度：" + cert.length());
        byte[] sourceData = cert.getBytes();
        //sourceData="{\"certID\":\"2013061367895\";\"name\":\"zhangsan\";\"publisher\":\"SEU\";\"image:0ff2384rhfdjaso8jhshdwqeouowqdcmweunq38ryvn98\"}".getBytes();
        //用id,公钥和明文验证签名是否通过
        boolean vs = SM2Utils.verifySign(userId.getBytes(), Base64.decode(pubkS.getBytes()), sourceData, Util.hexStringToBytes(sign));
        System.out.println(vs);

        return vs;
    }

    public String sm3CreateHash(String signedCert) throws IOException {

        byte[] md = new byte[32];
        byte[] msg1 = signedCert.getBytes();
        //System.out.println(msg1);
        SM3Digest sm3 = new SM3Digest();
        sm3.update(msg1, 0, msg1.length);
        sm3.doFinal(md, 0);
        String s = new String(Hex.encode(md));
        return s;
    }

    public String sm4EncryptCert(String signedCert) throws IOException {

        //使用sm4中的CBC模式对称加密数字证书
        SM4Utils sm4 = new SM4Utils();
        sm4.setSecretKey("JeF8U9wHFOMfs2Y8");
        sm4.setHexString(false);
        sm4.setIv("UISwD9fW6cFh9SNS");
        String cipherText = sm4.encryptData_CBC(signedCert);
        //System.out.println("密文: " + cipherText);
        return cipherText;
    }

    public String sm4DecryptCert(String cipherText) throws IOException {
        SM4Utils sm4 = new SM4Utils();
        sm4.setSecretKey("JeF8U9wHFOMfs2Y8");
        sm4.setHexString(false);
        sm4.setIv("UISwD9fW6cFh9SNS");
        String signedPlainText = sm4.decryptData_CBC(cipherText);
        System.out.println("解密后:" + signedPlainText);
        return signedPlainText;
    }

    //使用SM2对证书进行学生的签名，返回签名

    public String sm2StudentSignCert(String cert) throws IOException {

        String plainText = cert;

        byte[] sourceData = plainText.getBytes();
        System.out.println("加密用的机构ID=>" + plainText);

        // 国密规范测试私钥
        // 学生私钥
        String prik = "54232d8aaa3209ee123e07c34314e50e29fbb941496f92e219eb62c5bd40d968";
        String prikS = new String(Base64.encode(Util.hexToByte(prik)));

        // 国密规范测试用户ID
        String userId = "ALICE123@YAHOO.COM";

        //用id和私钥签名fff
        byte[] c = SM2Utils.sign(userId.getBytes(), Base64.decode(prikS.getBytes()), sourceData);

        return Util.getHexString(c);
    }

    //验证学生的签名
    public boolean sm2VerifyStudentSign(String signedCert, String sign,String StudentPK) throws IOException {
        // 国密规范测试用户ID
        String userId = "ALICE123@YAHOO.COM";

        //学生公钥：044a77c33fa976ddab1d8e2ad05694f01151ed39892832947fbcb4a89199db72bc5db91b29616009f0b504459ad72f97b078cf35aebd32b6066003dd81db9a3244
        String pubkS = new String(Base64.encode(Util.hexToByte(StudentPK)));
        System.out.println("pubkS:" + pubkS);
        System.out.println("signedCert:" + signedCert);

        byte[] sourceData = signedCert.getBytes();
        String s = sourceData.toString();
        System.out.println(sourceData[0]);
        //用id,公钥和明文验证签名是否通过

        boolean vs = SM2Utils.verifySign(userId.getBytes(), Base64.decode(pubkS.getBytes()), sourceData, Util.hexStringToBytes(sign));
        System.out.println(vs);

        return vs;
    }
}

