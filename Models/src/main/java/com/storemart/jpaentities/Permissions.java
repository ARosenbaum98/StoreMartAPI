package com.storemart.jpaentities;

import com.storemart.exceptions.BadPermission;
import com.storemart.exceptions.InitializePermissionException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
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
        String msg = "Permission "+s+" not recognized.";
        log.debug(msg+"(List of valid permissions: "+ list.toString()+")");
        throw new BadPermission(msg);
    }
}


