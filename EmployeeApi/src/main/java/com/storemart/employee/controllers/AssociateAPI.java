package com.storemart.employee.controllers;

import com.storemart.employee.services.EmployeeService;
import com.storemart.exceptions.BadPermission;
import com.storemart.exceptions.LoginBadPassword;
import com.storemart.exceptions.LoginUserNotFound;
import com.storemart.exceptions.UserLookupFailed;
import com.storemart.jpaentities.EmployeeLogin;
import com.storemart.jpaentities.EmployeeProfile;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PatchMapping(value="/edit/{id}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editEmployee(@PathVariable(name="id") Long editId,
                                       @RequestHeader String username,
                                       @RequestHeader String password,
                                       @RequestHeader(name="employeeId") Long loginId,
                                       @RequestBody EmployeeProfile profileToUpdate){

        EmployeeLogin login = new EmployeeLogin(loginId, username, password);
        profileToUpdate.setId(editId);

        try {
            EmployeeProfile user = employeeService.validateUser(login);

            if(employeeService.getEmployeeById(editId) == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee with id '"+editId+"' not found");
            }
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

    @PostMapping(value = "/add-permission/{id}/", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addPermission(@PathVariable(name="id") Long editId,
                                        @RequestHeader String username,
                                        @RequestHeader String password,
                                        @RequestHeader(name="employeeId") Long loginId,
                                        @RequestBody String[] permissionsToAdd){

        EmployeeLogin login = new EmployeeLogin(loginId, username, password);

        try {
            EmployeeProfile profileToUpdate = employeeService.getEmployeeById(editId);
            EmployeeProfile user = employeeService.validateUser(login);

            if(profileToUpdate == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee with id '"+editId+"' not found");
            }

            if(employeeService.canEditProfile(user, profileToUpdate)){
                if(employeeService.areValidPermissions(permissionsToAdd)) {
                    profileToUpdate.addEmployeePermissions(permissionsToAdd);
                    EmployeeProfile newProfile = employeeService.updateEmployee(profileToUpdate);
                    return ResponseEntity.ok().body(newProfile);
                }else{
                    // This will never run - error will be thrown if condition is not met
                    // If-else block needed to prevent memory leak
                    return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("This should never be seen");
                }
            }
            else{
                String msg = "User '"+login.getUsername()+"' lacks permission to edit employee profile with id '"+String.valueOf(editId)+"'";
                log.debug(msg);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
            }


        }
        catch(LoginUserNotFound | LoginBadPassword | UserLookupFailed | BadPermission e){
            log.debug(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        }catch(Exception e){
            log.error(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/remove-permission/{id}/", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity removePermission(@PathVariable(name="id") Long editId,
                                        @RequestHeader String username,
                                        @RequestHeader String password,
                                        @RequestHeader(name="employeeId") Long loginId,
                                        @RequestBody String[] permissionsToRemove){

        EmployeeLogin login = new EmployeeLogin(loginId, username, password);

        try {
            EmployeeProfile profileToUpdate = employeeService.getEmployeeById(editId);
            EmployeeProfile user = employeeService.validateUser(login);

            if(profileToUpdate == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee with id '"+editId+"' not found");
            }

            if(employeeService.canEditProfile(user, profileToUpdate)){
                if(employeeService.areValidPermissions(permissionsToRemove)) {

                    profileToUpdate.removeEmployeePermissions(permissionsToRemove);

                    EmployeeProfile newProfile = employeeService.updateEmployee(profileToUpdate);
                    return ResponseEntity.ok().body(newProfile);

                }else{
                    // This will never run - error will be thrown if condition is not met
                    // If-else block needed to prevent memory leak
                    return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("This should never be seen");
                }
            }
            else{
                String msg = "User '"+login.getUsername()+"' lacks permission to edit employee profile with id '"+String.valueOf(editId)+"'";
                log.debug(msg);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
            }


        }
        catch(LoginUserNotFound | LoginBadPassword | UserLookupFailed | BadPermission e){
            log.debug(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        }catch(Exception e){
            log.error(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/add-employee/", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addEmployee(@PathVariable(name="id") Long editId,
                                      @RequestHeader String username,
                                      @RequestHeader String password,
                                      @RequestHeader(name="employeeId") Long loginId,
                                      @RequestBody EmployeeProfile profileToAdd){

        EmployeeLogin login = new EmployeeLogin(loginId, username, password);

        try {
            EmployeeProfile userLoggedIn = employeeService.validateUser(login);

            if(employeeService.canAddUser(userLoggedIn)){
                employeeService.addUser(profileToAdd);
                return ResponseEntity.ok().body(employeeService.getEmployeeByUsername(profileToAdd.getUsername()).toString());
            }

            String msg = "User '"+login.getUsername()+"' lacks permission to create employee profiles";
            log.debug(msg);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);

        }
        catch(LoginUserNotFound | LoginBadPassword | UserLookupFailed | BadPermission e){
            log.debug(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        }catch(Exception e){
            log.error(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();
        }
    }

}
