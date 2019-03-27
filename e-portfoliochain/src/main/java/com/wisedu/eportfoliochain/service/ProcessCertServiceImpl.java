package com.wisedu.eportfoliochain.service;

import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class ProcessCertServiceImpl implements ProcessCertService {

    //从本地路径下的json文件从读取证书
    public String saveJsonCert(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }
        String fileName = file.getOriginalFilename();
        String filePath = "/home/xuli/projects/HW/JSON_FILE/";
        File dest = new File(filePath + fileName);
        try {
            file.transferTo(dest);
            return filePath + fileName;
        } catch (IOException e) {
        }
        return "上传失败！";

    }
}
