package com.storemart.v3.employeeAPI.repositories;

import com.storemart.v3.employeeAPI.models.EmployeeLogin;
import com.storemart.v3.employeeAPI.models.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeProfile, Long> {

    @Query(value = "SELECT id FROM employee_permission", nativeQuery = true)
    Collection<String> findAllPermissionLevels();


    EmployeeProfile findByEmail(String email);

    EmployeeProfile findByUsername(String email);

    EmployeeProfile findByUsernameIgnoreCase(String username);

    @Override
    Optional<EmployeeProfile> findById(Long aLong);
}
