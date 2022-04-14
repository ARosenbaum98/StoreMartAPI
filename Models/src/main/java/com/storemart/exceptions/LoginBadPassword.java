package com.storemart.exceptions;

public class LoginBadPassword extends LoginException {
    public LoginBadPassword(String message) {
        super(message);
    }
}
