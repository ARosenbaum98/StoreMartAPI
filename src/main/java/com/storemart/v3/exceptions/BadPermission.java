package com.storemart.v3.exceptions;

public class BadPermission extends RuntimeException {
    public BadPermission(String message) {
        super(message);
    }
}
