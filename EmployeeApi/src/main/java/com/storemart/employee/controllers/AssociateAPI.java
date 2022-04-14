package com.storemart.employee.controllers;

import com.storemart.employee.services.EmployeeService;
import com.storemart.exceptions.LoginBadPassword;
import com.storemart.exceptions.LoginUserNotFound;
import com.storemart.jpaentities.EmployeeLogin;
import com.storemart.jpaentities.EmployeeProfile;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@SuppressWarnings("ALL")
@RequestMapping("associates")
@RestController
@Slf4j
public class AssociateAPI {

    @Autowired
    @Setter
    private EmployeeService employeeService;

//    @GetMapping(value = "/permissions_list")
//    public ResponseEntity<HashSet<String>> getPermissionsList(){
//        return ResponseEntity.ok().body(employeeService.allPermissionLevels());
//    }

    @GetMapping(value="/profile/{id}")
    public ResponseEntity getEmployee(@PathVariable(name = "id") Long lookupId, @RequestBody EmployeeLogin login){

        try {
            EmployeeProfile user = employeeService.validateUser(login);
            EmployeeProfile profile = employeeService.getEmployeeById(lookupId);

            if(employeeService.canViewProfile(user, profile)){
                return ResponseEntity.ok().body(profile);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User '"+login.getUsername()+"' lacks permission to view employee profile with id '"+String.valueOf(lookupId)+"'");
        }
        catch(LoginUserNotFound | LoginBadPassword e){
            log.debug(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch(Exception e){
            log.error(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();

        }
    }

    @GetMapping(value="/edit/{id}")
    public ResponseEntity editEmployee(@PathVariable(name="id") Long lookupId, @RequestBody EmployeeLogin login){
        return null;
    }
}
