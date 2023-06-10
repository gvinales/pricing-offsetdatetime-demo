package org.acme.pricing.exception;

import jakarta.ws.rs.core.Response;

public class PlatformHttpException extends RuntimeException {
    private final Response.Status httpStatus;

    public PlatformHttpException(String message, Response.Status httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public Response.Status getHttpStatus() {
        return httpStatus;
    }

}