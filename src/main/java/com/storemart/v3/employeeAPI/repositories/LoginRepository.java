package com.storemart.v3.employeeAPI.repositories;

import com.storemart.v3.employeeAPI.models.EmployeeLogin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<EmployeeLogin, Long> {

    EmployeeLogin findByUsernameIgnoreCase(String username);



}
