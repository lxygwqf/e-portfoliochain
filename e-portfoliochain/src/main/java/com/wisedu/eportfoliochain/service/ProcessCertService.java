package com.wisedu.eportfoliochain.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface ProcessCertService {
    public String saveJsonCert(MultipartFile file) throws IOException;
}
