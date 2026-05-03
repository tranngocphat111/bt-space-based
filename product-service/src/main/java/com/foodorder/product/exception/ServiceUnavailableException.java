package com.foodorder.product.exception;

/**
 * Exception thrown when Persistence Service is unavailable or timeout
 */
public class ServiceUnavailableException extends RuntimeException {

    private final int statusCode;

    public ServiceUnavailableException(String message) {
        super(message);
        this.statusCode = 503;
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 503;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
