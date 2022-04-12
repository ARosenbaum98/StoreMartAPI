package com.storemart.v3.employeeAPI.repositories;

import com.storemart.v3.employeeAPI.models.EmployeeLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepository extends JpaRepository<EmployeeLogin, Long> {

    EmployeeLogin findByUsernameIgnoreCase(String username);



}
