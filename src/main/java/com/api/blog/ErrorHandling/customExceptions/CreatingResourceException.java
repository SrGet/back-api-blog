package com.api.blog.ErrorHandling.customExceptions;

public class CreatingResourceException extends RuntimeException{

    private final Object resource;


    public CreatingResourceException(String message, Object resource) {
        super(message);
        this.resource = resource;
    }

    public Object getResource() {
        return resource;
    }
}
