package com.storemart.Oauth2authorizationserver.exceptions;

public class BadPermission extends RuntimeException {
    public BadPermission(String message) {
        super(message);
    }
}
