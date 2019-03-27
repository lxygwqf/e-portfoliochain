package com.wisedu.blockchain.service.Impl;

import org.springframework.stereotype.Service;
import com.wisedu.blockchain.service.ProcessCertService;

import java.io.*;

@Service
public class ProcessCertServiceImpl implements ProcessCertService {

    //从本地路径下的json文件从读取证书
    public String readJsonCert(String path) throws IOException {
        System.out.println("开始读文件");
        File file = new File(path);
        StringBuilder stringb = new StringBuilder();

        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                //用逗号会出问题,这里把json文件的，替换掉
                if (((char) tempchar) == ',')
                    tempchar=';';
                stringb.append((char) tempchar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(reader != null){
            reader.close();
        }
        String plainText=stringb.toString();
        System.out.println(plainText);
        return plainText;
    }
}
