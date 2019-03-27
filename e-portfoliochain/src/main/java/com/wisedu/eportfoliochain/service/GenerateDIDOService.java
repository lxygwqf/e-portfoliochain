package com.wisedu.eportfoliochain.service;

import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface GenerateDIDOService {
    public int GenerateDIDO(String DID) throws IOException;
}