package com.storemart.employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("my.package.base.*")
public class EmployeeApiApplication {

    public static void main(String[] args) {

        SpringApplication.run(EmployeeApiApplication.class, args);
    }

}
