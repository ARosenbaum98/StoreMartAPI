package com.storemart.v3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

//@Configuration
//public class JpaConfig {
//    @Bean(name="entityManagerFactory")
//    public LocalSessionFactoryBean sessionFactory(){
//        return new LocalSessionFactoryBean();
//    }
//
//    @Bean(name = "entityManagerFactoryBean")
//    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(){
//        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
//        bean.setPackagesToScan("com.storemart.v3.models");
//        bean.setPersistenceUnitName("EmployeeLogin");
//        return bean;
//    }
//}
