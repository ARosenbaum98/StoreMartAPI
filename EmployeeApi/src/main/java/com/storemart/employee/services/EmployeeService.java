package com.storemart.employee.services;

import com.storemart.employee.repositories.EmployeeRepository;
import com.storemart.employee.repositories.LoginRepository;
import com.storemart.exceptions.LoginBadPassword;
import com.storemart.exceptions.LoginException;
import com.storemart.exceptions.LoginUserNotFound;
import com.storemart.exceptions.UserLookupByIdFailed;
import com.storemart.jpaentities.EmployeeLogin;
import com.storemart.jpaentities.EmployeeProfile;
import com.storemart.jpaentities.Permissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
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
        HashSet<String> allPermissions = new HashSet<>(ep.findAllPermissionLevels());
        PERMISSIONS = new Permissions(allPermissions);

        employeeRepository = ep;
        loginRepository = lp;
    }

//    public Set<String> allPermissionLevels(){
//        return PERMISSIONS.getList();
//    }

    public EmployeeProfile validateUser(EmployeeLogin attempt) throws LoginException{
        EmployeeLogin login = loginRepository.findByUsernameIgnoreCase(attempt.getUsername());

        if(login == null){
            throwUserNotFoundError("username", attempt.getUsername());
            return null;
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
            return this.isSupervisor(user, profile);
        }

        // Allow view assigned store look up
        if(userPermissions.contains("VIEW_ASSIGNED_STORE_EMPLOYEES")){
            //TODO Implement store look up
        }

        return false;
    }

    public boolean canEditProfile(EmployeeProfile requester, EmployeeProfile profileToEdit){

        Permissions requesterPermissions = new Permissions(requester.getEmployeePermissions(), PERMISSIONS);

        // Allow edit all profiles
        if(requesterPermissions.contains("EDIT_ALL_EMPLOYEE_PROFILES")){
            return true;
        }

        // Allow edit own profile
        if(requester.getUsername().equals(profileToEdit.getUsername()) && requester.getId().equals(profileToEdit.getId())){
            return requesterPermissions.contains("EDIT_OWN_PROFILE");
        }

        // Allow edit assigned employee profiles
        if(requesterPermissions.contains("EDIT_ASSIGNED_EMPLOYEE_PROFILES")){
            return this.isSupervisor(requester, profileToEdit);
        }

        // Allow edit store
        if(requesterPermissions.contains("EDIT_ASSIGNED_STORE_EMPLOYEE_PROFILES")){
            //TODO: Store look up system.
        }

        return false;
    }

    private boolean isSupervisor(EmployeeProfile supervisor, EmployeeProfile profile) {
        for(EmployeeProfile supervisee : supervisor.getSupervisees()){
            if(supervisee.equals(profile)) return true;
        }
        return false;
    }

    private void throwUserNotFoundError(String field, Object cred) throws LoginUserNotFound{
        String msg = "User with "+field+" '"+ cred +"' not located in database";
        log.debug(msg);
        throw new LoginUserNotFound(msg);
    }

    private void throwBadPasswordException(String username) throws LoginBadPassword{
        String msg = "Login attempt from user "+username+" not valid (Bad Password)";
        log.debug(msg);
        throw new LoginBadPassword(msg);
    }

    public EmployeeProfile updateEmployee(EmployeeProfile newDetails) throws UserLookupByIdFailed {
        Optional<EmployeeProfile> profileOptional = employeeRepository.findById(newDetails.getId());
        EmployeeProfile profile = null;
        if(profileOptional.isPresent()){
            profile = profileOptional.get();
            copyNonNullProperties(newDetails, profile);
            employeeRepository.save(profile);
        }else{
            String msg = "User with id '"+ newDetails.getId() +"' not found in database";
            log.debug(msg);
            throw new UserLookupByIdFailed(msg);
        }
        return profile;
    }


    // Lovingly copied from https://stackoverflow.com/a/27862208/16800644
    private static void copyNonNullProperties(Object from, Object to) {
        BeanUtils.copyProperties(from, to, getNullPropertyNames(from));
    }

    private static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
