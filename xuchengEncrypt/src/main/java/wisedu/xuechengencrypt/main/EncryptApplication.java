package wisedu.xuechengencrypt.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import wisedu.xuechengencrypt.bean.Certificate;


@SpringBootApplication
@ComponentScan(basePackages = {"wisedu.xuechengencrypt"})
public class EncryptApplication {

    public static void main(String[] args){

        Certificate certificate = new Certificate();
        certificate.setCreateDate("2016/06/25");
        certificate.setDegreeLevel("学士学位");
        certificate.setDiscipline("工科");
        certificate.setGraduateDate("2016/06/25");
        certificate.setIssuer("东南大学");
        certificate.setIssuingDate("2016/06/25");
        certificate.setMajor("计算机科学与技术");
        certificate.setName("毕业证书");
        certificate.setStudentId("220171634");
        certificate.setStudentName("李铭");
        String json = JSON.toJSON(certificate).toString();
        System.out.println(json);
        Certificate certificate1 = (Certificate) JSON.parseObject(json,Certificate.class);
        System.out.println("sdf" + certificate1.getDegreeLevel());
//        JSONObject jsonObject = JSONObject.fromObject(json);

        SpringApplication.run(EncryptApplication.class,args);
    }
}
