package ai.shreds.shared.dtos;

import java.time.Instant;
import java.util.Objects;

public class SharedErrorResponseDTO {

    private String error;
    private String details;
    private String stackTrace;
    private Instant timestamp;
    private String errorCode;

    private SharedErrorResponseDTO(Builder builder) {
        setError(builder.error);
        setDetails(builder.details);
        setStackTrace(builder.stackTrace);
        setTimestamp(builder.timestamp);
        setErrorCode(builder.errorCode);
    }

    public SharedErrorResponseDTO() {
        this.timestamp = Instant.now();
    }

    public SharedErrorResponseDTO(String error) {
        this();
        setError(error);
    }

    public static SharedErrorResponseDTO fromException(Exception e) {
        return new Builder()
                .error(e.getMessage())
                .details(e.getClass().getSimpleName())
                .stackTrace(getStackTraceAsString(e))
                .timestamp(Instant.now())
                .errorCode("ERR_" + e.getClass().getSimpleName().toUpperCase())
                .build();
    }

    public static SharedErrorResponseDTO fromBusinessError(String message, String code) {
        return new Builder()
                .error(message)
                .errorCode(code)
                .timestamp(Instant.now())
                .build();
    }

    private static String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        if (error == null || error.trim().isEmpty()) {
            throw new IllegalArgumentException("Error message cannot be null or empty");
        }
        this.error = error.trim();
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = (timestamp == null) ? Instant.now() : timestamp;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedErrorResponseDTO that = (SharedErrorResponseDTO) o;
        return Objects.equals(error, that.error) &&
                Objects.equals(details, that.details) &&
                Objects.equals(errorCode, that.errorCode) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error, details, errorCode, timestamp);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SharedErrorResponseDTO{");
        sb.append("error='").append(error).append("'");
        sb.append(", details='").append(details).append("'");
        sb.append(", errorCode='").append(errorCode).append("'");
        sb.append(", timestamp=").append(timestamp);
        sb.append("}");
        return sb.toString();
    }

    public static class Builder {
        private String error;
        private String details;
        private String stackTrace;
        private Instant timestamp;
        private String errorCode;

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder details(String details) {
            this.details = details;
            return this;
        }

        public Builder stackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public SharedErrorResponseDTO build() {
            return new SharedErrorResponseDTO(this);
        }
    }
}
