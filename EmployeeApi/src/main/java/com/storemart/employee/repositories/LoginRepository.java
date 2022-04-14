package com.storemart.employee.repositories;

import com.storemart.jpaentities.EmployeeLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepository extends JpaRepository<EmployeeLogin, Long> {

    EmployeeLogin findByUsernameIgnoreCase(String username);



}
