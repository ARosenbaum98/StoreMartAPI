package com.storemart.exceptions;

public class UserLookupFailed extends RuntimeException{
    UserLookupFailed(String msg){
        super(msg);
    }
}
