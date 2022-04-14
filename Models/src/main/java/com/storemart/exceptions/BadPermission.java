package com.storemart.exceptions;

public class BadPermission extends RuntimeException {
    public BadPermission(String message) {
        super(message);
    }
}
