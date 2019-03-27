package wisedu.xuechengencrypt.service;

import java.io.IOException;

public interface SmEncryptService {
    public String sm2SignCert(String cert) throws  IOException;
    public boolean sm2VerifySign(String signedCert) throws IOException;
    public String sm3CreateHash(String signedCert) throws IOException;
    public String sm4EncryptCert(String signedCert) throws IOException;
    public  String sm4DecryptCert(String cipherText) throws IOException;
    public  String sm2StudentSignCert(String InstitutionId) throws IOException;
    public boolean sm2VerifyStudentSign(String signedCert, String sign, String StudentPK) throws IOException;
}
