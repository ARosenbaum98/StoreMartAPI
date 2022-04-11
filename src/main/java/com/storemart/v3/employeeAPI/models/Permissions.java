package com.storemart.v3.employeeAPI.models;

import com.storemart.v3.exceptions.BadPermission;
import com.storemart.v3.exceptions.InitializePermissionException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Getter
@ToString
@EqualsAndHashCode
@Slf4j
public class Permissions {

    private final Set<String> list;
    private final Set<String> parentList;

    public Permissions(Set<String> list, Permissions parentList){

        //Validate list
        for(String permission : list ){
            if(!parentList.contains(permission)){
                throw new InitializePermissionException("Permission '"+permission+"' not recognized as part of parent list Parent = ["+parentList.toString()+"]");
            }
        }

        this.list = list;
        this.parentList = parentList.getList();
    }

    public Permissions(Set<String> list){
        this.list = list;
        this.parentList = list;
    }

    public boolean contains(String s){
        if(!this.parentList.contains(s)){
            throwBadPermissionException(s);
        }
        return this.list.contains(s);
    }

    private void throwBadPermissionException(String s) throws BadPermission {
        String msg = "Permission "+s+" not recognized. (List of valid permissions: "+ list.toString()+")";
        log.error(msg);
        throw new BadPermission(msg);
    }
}


