package com.foodorder.product.exception;

/**
 * Exception thrown when a product is not found
 */
public class NotFoundException extends RuntimeException {

    private final int statusCode;

    public NotFoundException(String message) {
        super(message);
        this.statusCode = 404;
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 404;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
