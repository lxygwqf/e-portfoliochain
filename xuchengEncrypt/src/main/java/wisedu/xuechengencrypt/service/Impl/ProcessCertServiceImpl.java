package wisedu.xuechengencrypt.service.Impl;

import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Service;
import wisedu.xuechengencrypt.encrypt.Util;
import wisedu.xuechengencrypt.service.ProcessCertService;

import java.io.*;

@Service
public class ProcessCertServiceImpl implements ProcessCertService {

    //从本地路径下的json文件从读取证书
    public String readJsonCert(String path) throws IOException {
        File file = new File(path);
        StringBuilder stringb = new StringBuilder();

        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                //用逗号会出问题,这里把json文件的，替换掉
//                if (((char) tempchar) == ',')
//                    tempchar=';';
                stringb.append((char) tempchar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(reader != null){
            reader.close();
        }
        String plainText=stringb.toString();
        return plainText;
    }

    public String addSchoolSign(String sign,String plainText){

        //学校公钥
        String pubk = "04ec7e40b8dfa4b14383f703ec5403b71db0ab505b9fc41f0df45a9910a307dfbd5b3c5afdd4b90d79fa0ab70d53fd88422df77e09b254a53e72b4857f74ab1da4";
        String pubkS = new String(Base64.encode(Util.hexToByte(pubk)));
//        String signedCert=plainText.replace("}",";\"SchoolpubkS\":\""+pubkS+"\";\"sign\":\""+sign+"\"}");
        String signedCert=plainText.replace("}","&\"SchoolpubkS\":\""+pubkS+"\"&\"sign\":\""+sign+"\"}");
        System.out.println("signedCert:" + signedCert);

        return signedCert;

    }
}
