package com.storemart.employee.services;

import com.storemart.employee.repositories.EmployeeRepository;
import com.storemart.employee.repositories.LoginRepository;
import com.storemart.exceptions.LoginBadPassword;
import com.storemart.exceptions.LoginException;
import com.storemart.exceptions.LoginUserNotFound;
import com.storemart.jpaentities.EmployeeLogin;
import com.storemart.jpaentities.EmployeeProfile;
import com.storemart.jpaentities.Permissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class EmployeeService {

    private final Permissions PERMISSIONS;

    private EmployeeRepository employeeRepository;
    private LoginRepository loginRepository;

    @Autowired
    public EmployeeService(EmployeeRepository ep, LoginRepository lp){
        HashSet<String> p_seed = new HashSet<>(ep.findAllPermissionLevels());
        PERMISSIONS = new Permissions(p_seed);

        employeeRepository = ep;
        loginRepository = lp;
    }

    public Set<String> allPermissionLevels(){
        return PERMISSIONS.getList();
    }

    public EmployeeProfile validateUser(EmployeeLogin attempt) throws LoginException{
        EmployeeLogin login = loginRepository.findByUsernameIgnoreCase(attempt.getUsername());

        if(login == null){
            throwUserNotFoundError("username", attempt.getUsername());
        }

        if(!login.getPassword().equals(attempt.getPassword())){
            throwBadPasswordException(attempt.getUsername());
        }
        return employeeRepository.getById(login.getId());
    }

    public EmployeeProfile getEmployeeById(Long id) {
        Optional<EmployeeProfile> profile = employeeRepository.findById(id);

        if(!profile.isPresent()){
            throwUserNotFoundError("id", id);
            return null;
        }

        return profile.get();
    }

    public boolean canViewProfile(EmployeeProfile user, EmployeeProfile profile) {

        Permissions userPermissions = new Permissions(user.getEmployeePermissions(), PERMISSIONS);

        // Allow view all profiles
        if(userPermissions.contains("VIEW_ALL_EMPLOYEE_PROFILES")){
            return true;
        }

        // Allow view own profile
        if(user.getUsername().equals(profile.getUsername()) && user.getId().equals(profile.getId())) {
            return userPermissions.contains("VIEW_OWN_PROFILE");
        }

        // Allow view assigned employee profiles
        if(userPermissions.contains("VIEW_ASSIGNED_EMPLOYEE_PROFILE")){
            for(EmployeeProfile supervisee : user.getSupervisees()){
                if(supervisee.equals(profile)) return true;
            }
            return false;
        }

        //TODO Implement store look up
        if(userPermissions.contains("VIEW_ASSIGNED_STORE_EMPLOYEES")){
            //TODO
        }

        return false;
    }

//    public boolean canEditProfile(EmployeeProfile user, EmployeeProfile profile){
//
//        Permissions userPermissions = new Permissions(user.getEmployeePermissions(), PERMISSIONS);
//
//        // Allow edit all profiles
//        if(userPermissions.contains("EDIT_ALL_EMPLOYEE_PROFILES")){
//            return true;
//        }
//    }

    private void throwUserNotFoundError(String field, Object cred){
        String msg = "User with "+field+" '"+ cred +"' not located in database";
        log.debug(msg);
        throw new LoginUserNotFound(msg);
    }

    private void throwBadPasswordException(String username){
        String msg = "Login attempt from user "+username+" not valid (Bad Password)";
        log.debug(msg);
        throw new LoginBadPassword(msg);
    }

}
