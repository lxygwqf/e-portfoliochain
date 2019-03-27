package com.wisedu.eportfoliochain.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class GenerateDIDOServiceImpl implements GenerateDIDOService {

    //从本地路径下的json文件从读取证书
    public int GenerateDIDO(String DID) throws IOException {
        String path = "/home/xuli/projects/HW/did10.json";
        File file = new File(path);

        FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
        BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
        StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
        String s = "";
        while ((s = bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
            sb.append(s + "\n");//将读取的字符串添加换行符后累加存放在缓存中
            System.out.println(s);
        }
        bReader.close();
        String str = sb.toString();
        System.out.println("str:"+str);
        String params = str.split(",")[0].split(":")[1];
        String newDID = params.replace(params.substring(1,params.length()),DID);
        String did = "{\"did\":" + newDID +"\"";
        String newDIDO = str.replace(str.split(",")[0],did);
        System.out.println("newDIDO:" + newDIDO);

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(newDIDO);
            out.close();
        } catch (IOException e) {
        }

        return 1;
    }
}
