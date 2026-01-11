package com.example.booking_api.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String entity, Long id) {
        super(String.format("%s with ID %d not found", entity, id));
    }


    public NotFoundException(String entity, String field, Object value) {
        super(String.format("%s with %s %s not found", entity, field, value));
    }

    public NotFoundException(String message) {
        super(message);
    }
}
