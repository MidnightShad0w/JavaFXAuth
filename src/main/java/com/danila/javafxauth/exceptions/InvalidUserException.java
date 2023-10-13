package com.danila.javafxauth.exceptions;

public class InvalidUserException extends Exception {

    String title;

    public  InvalidUserException() {
    }

    public  InvalidUserException(String title, String message) {
        super(message);
    }

    public String getTitle() {
        return title;
    }
}
