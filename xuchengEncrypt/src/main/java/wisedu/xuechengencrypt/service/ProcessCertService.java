package wisedu.xuechengencrypt.service;

import java.io.IOException;

public interface ProcessCertService {
    public String readJsonCert(String path) throws IOException;
    public String addSchoolSign(String sign,String plainText);
}
