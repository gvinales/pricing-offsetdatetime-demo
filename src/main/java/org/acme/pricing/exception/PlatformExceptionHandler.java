package org.acme.pricing.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotAuthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.format.DateTimeParseException;


/**
 * Exception handling artifact for Spring. It intercepts any exception and returns a custom Problem response with
 * user-friendly messages.
 * <p>
 * It logs the exceptions message for know exceptions.
 * It logs the stacktrace for unhandled exceptions.
 */
@Slf4j
@RestControllerAdvice
public class PlatformExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ProblemInfo> handle(NotAuthorizedException ex) {
        log.debug("Handling NotAuthorizedException", ex);
        return buildProblemResponse(ProblemInfo.forException(ex));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemInfo> handle(ConstraintViolationException ex) {
        log.debug("Handling ConstraintViolationException", ex);
        return buildProblemResponse(ProblemInfo.forException(ex));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemInfo> handle(PlatformHttpException ex) {
        log.debug("Handling PlatformHttpException exception", ex);
        return buildProblemResponse(ProblemInfo.forException(ex));
    }

    @ExceptionHandler
    private ResponseEntity<ProblemInfo> handle(DateTimeParseException ex) {
        log.debug("Handling PlatformHttpException exception", ex);
        return buildProblemResponse(ProblemInfo.forException(ex));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemInfo> catchAllOthers(Throwable th) {
        // search for nested exception:
        Throwable cause = th;
        while (cause != null) {
            if (cause instanceof ConstraintViolationException cvex) {
                log.debug("Catch all exceptions found nested ConstraintViolationException root cause", th);
                return handle(cvex);
            }
            // search for more interesting nested exceptions

            cause = cause.getCause();
        }
        // DateTimeParseException
        cause = th;
        while (cause != null) {
            if (cause instanceof DateTimeParseException inner) {
                log.debug("Catch all exceptions found nested DateTimeParseException root cause", th);
                return handle(inner);
            }
            // search for more interesting nested exceptions

            cause = cause.getCause();
        }
        // search for other Spring Framework known exceptions...

        // catch all others....
        log.debug("Found unhandled exception", th);
        return buildProblemResponse(ProblemInfo.builder().type(URI.create("about:blank"))
                .status(500).title("Oops, We've tripped on a banana peel! We're on it").build());
    }

    private ResponseEntity<ProblemInfo> buildProblemResponse(ProblemInfo problem) {
        return ResponseEntity.status(problem.getStatus()).body(problem);
    }


}

