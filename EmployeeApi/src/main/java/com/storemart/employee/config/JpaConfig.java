package com.storemart.employee.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@EntityScan("com.storemart.*")
@Configuration
public class JpaConfig {
}
