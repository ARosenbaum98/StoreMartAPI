package com.storemart.Oauth2authorizationserver.repositories;

import com.storemart.Oauth2authorizationserver.models.employee.EmployeeLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepository extends JpaRepository<EmployeeLogin, Long> {

    EmployeeLogin findByUsernameIgnoreCase(String username);



}
