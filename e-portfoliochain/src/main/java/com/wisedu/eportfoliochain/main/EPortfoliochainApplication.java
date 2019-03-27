package com.wisedu.eportfoliochain.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@ComponentScan(basePackages = {"com.wisedu.eportfoliochain"})
@MapperScan(basePackages = {"com.wisedu.eportfoliochain.repository"})
@EnableAutoConfiguration//启用自动配置
public class EPortfoliochainApplication {



    public static void main(String[] args) {
        SpringApplication.run(EPortfoliochainApplication.class, args);
    }





}
