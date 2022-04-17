package com.storemart.employee.controllers;

import com.storemart.employee.services.EmployeeService;
import com.storemart.exceptions.LoginBadPassword;
import com.storemart.exceptions.LoginUserNotFound;
import com.storemart.exceptions.UserLookupFailed;
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
    public ResponseEntity getEmployee(@PathVariable(name = "id") Long lookupId,
                                      @RequestHeader String username,
                                      @RequestHeader String password,
                                      @RequestHeader Long employeeId){

        EmployeeLogin login = new EmployeeLogin(employeeId, username, password);

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

    @PatchMapping(value="/edit/{id}")
    public ResponseEntity editEmployee(@PathVariable(name="id") Long editId,
                                       @RequestHeader String username,
                                       @RequestHeader String password,
                                       @RequestHeader Long employeeId,
                                       @RequestBody EmployeeProfile profileToUpdate){

        EmployeeLogin login = new EmployeeLogin(employeeId, username, password);
        profileToUpdate.setId(editId);

        try {
            EmployeeProfile user = employeeService.validateUser(login);

            if(employeeService.canEditProfile(user, profileToUpdate)){
                EmployeeProfile newProfile = employeeService.updateEmployee(profileToUpdate);
                return ResponseEntity.ok().body(newProfile);
            }
            String msg = "User '"+login.getUsername()+"' lacks permission to edit employee profile with id '"+String.valueOf(editId)+"'";
            log.debug(msg);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
        }
        catch(LoginUserNotFound | LoginBadPassword | UserLookupFailed e){
            log.debug(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch(Exception e){
            log.error(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();
        }
    }
}
