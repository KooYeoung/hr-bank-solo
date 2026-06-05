package com.kooyeoung.hrbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class HrBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrBankApplication.class, args);
    }

}
