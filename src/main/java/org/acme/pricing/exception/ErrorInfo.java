package org.acme.pricing.exception;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.net.URI;

/**
 * detail – a human-readable explanation of the error
 * instance – a URI that identifies the specific occurrence of the error
 * Examples:
 * "detail": "Authentication failed due to incorrect username or password",
 * "instance": "/login/username/abc123"
 */
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ErrorInfo {

    private String detail;
    private URI instance;
    @Nullable
    private Object value;

    public ErrorInfo(String detail, String uriPath) {
        this.detail = detail;
        this.instance = URI.create(uriPath);
    }

}

