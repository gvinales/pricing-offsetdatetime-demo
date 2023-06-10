package org.acme.pricing.exception;

import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.net.URI;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;


/**
 * Aims to standardize the response body info.
 * <p>
 * title – a brief, human-readable message about the error
 * type – a URI identifier that categorizes the error
 * status – the HTTP response code (code)
 * <p>
 * JSON response example:
 * ```json
 * {
 * "title": "Incorrect username or password",
 * "type": "/errors/incorrect-user-pass",
 * "status": 401,
 * }
 * ```
 */
@Data
@Builder
@AllArgsConstructor
public class ProblemInfo {

    public static final URI TYPES_CONSTRAINS_VIOLATIONS = URI.create("/errors/constrains");
    private static final URI TYPES_BLANK = URI.create("about:blank");
    private static final URI TYPES_500 = URI.create("/errors/general");
    @Nullable
    private String title;
    private URI type;
    private int status;
    private String statusPhrase;

    @Nullable
    private List<ErrorInfo> errors;


    protected ProblemInfo() {
        this.type = TYPES_BLANK;
        this.status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        this.title = "Oops! We've tripped on a banana peel! We're on it";
    }


    protected ProblemInfo(int rawStatusCode) {
        var st = Response.Status.fromStatusCode(rawStatusCode);
        this.status = rawStatusCode;
        this.statusPhrase = st.getReasonPhrase();
        if (rawStatusCode == 500)
            this.type = TYPES_500;
        else
            this.type = TYPES_BLANK;
    }

    protected ProblemInfo(ProblemInfo other) {
        this.type = other.type;
        this.title = other.title;
        this.status = other.status;
        this.statusPhrase = other.statusPhrase;
        this.errors = other.errors != null ? new LinkedList<>(other.errors) : null;
    }

    protected ProblemInfo(URI type, @Nullable String title, Response.Status status, @Nullable List<ErrorInfo> errors) {
        this.type = type;
        this.title = title;
        this.status = status.getStatusCode();
        this.statusPhrase = status.getReasonPhrase();
        this.errors = errors;
    }

    public static ProblemInfo forException(NotAuthorizedException ex) {
        return ProblemInfo.forStatusAndTitle(Response.Status.UNAUTHORIZED, "Unauthorized request");
    }

    public static ProblemInfo forException(ConstraintViolationException ex) {
        final List<ErrorInfo> errors = ex.getConstraintViolations().stream()
                .map(v -> ErrorInfo.builder().detail(v.getMessage()).instance(URI.create(v.getPropertyPath().toString())).value(v.getInvalidValue())
                        .build()).toList();
        return new ProblemInfo(ProblemInfo.TYPES_CONSTRAINS_VIOLATIONS,
                "Constrains violation", BAD_REQUEST, errors);
    }

    public static ProblemInfo forException(PlatformHttpException ex) {
        return ProblemInfo.forStatusAndTitle(ex.getHttpStatus(), ex.getMessage());
    }

    public static ProblemInfo forStatus(int status) {
        return new ProblemInfo(status);
    }

    public static ProblemInfo forStatusAndTitle(@NotNull(message = "HttpStatus code is required") Response.Status status,
                                                @NotNull(message = "Problem title is required") String title) {
        ProblemInfo problem = forStatus(status.getStatusCode());
        problem.setTitle(title);
        return problem;
    }


    public static ProblemInfo forException(DateTimeParseException ex) {
        return ProblemInfo.builder().status(BAD_REQUEST.getStatusCode()).title("Problems parsing a date")
                .errors(List.of(new ErrorInfo(ex.getMessage(), "date")))
                .build();
    }
}

