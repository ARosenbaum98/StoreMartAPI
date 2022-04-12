package com.storemart.Oauth2authorizationserver.repositories;

import com.storemart.Oauth2authorizationserver.models.employee.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeProfile, Long> {

    @Query(value = "SELECT id FROM employee_permission", nativeQuery = true)
    Collection<String> findAllPermissionLevels();


    EmployeeProfile findByEmail(String email);

    EmployeeProfile findByUsername(String email);

    EmployeeProfile findByUsernameIgnoreCase(String username);

    @Override
    Optional<EmployeeProfile> findById(Long aLong);
}
