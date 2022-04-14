package com.storemart.exceptions;

public class LoginUserNotFound extends LoginException {
    public LoginUserNotFound(String message) {
        super(message);
    }
}
