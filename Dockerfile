# Use a specific version for better reproducibility
FROM openjdk:11-jre-slim

# Add non-root user
RUN groupadd -r payment && useradd -r -g payment payment

# Create necessary directories
RUN mkdir /app /app/logs && \
    chown -R payment:payment /app

# Set working directory
WORKDIR /app

# Copy the application JAR
COPY --chown=payment:payment target/payment-service-1.0.0-SNAPSHOT.jar ./app.jar

# Expose application port
EXPOSE 8080

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:+UseG1GC \
               -XX:+HeapDumpOnOutOfMemoryError \
               -XX:HeapDumpPath=/app/logs/heap-dump.hprof \
               -XX:+ExitOnOutOfMemoryError \
               -Xms256m \
               -Xmx512m \
               -XX:MaxMetaspaceSize=128m \
               -XX:MaxDirectMemorySize=256m \
               -Duser.timezone=UTC \
               -Dfile.encoding=UTF-8" \
    SERVER_PORT=8080 \
    SPRING_OUTPUT_ANSI_ENABLED=ALWAYS

# Switch to non-root user
USER payment

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Resource limits (these are soft limits, hard limits should be set in container runtime)
ENV MEMORY_LIMIT="512m" \
    CPU_LIMIT="1.0"

# Create volume for logs
VOLUME ["/app/logs"]

# Entrypoint with proper shell form to allow variable substitution
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
