package ai.shreds;

import ai.shreds.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.Initializer.class)
@ExtendWith(OutputCaptureExtension.class)
class InventoryApplicationStartupTest {

    private static final Logger logger = LoggerFactory.getLogger(InventoryApplicationStartupTest.class);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", TestContainersConfig.postgres::getJdbcUrl);
        registry.add("spring.datasource.username", TestContainersConfig.postgres::getUsername);
        registry.add("spring.datasource.password", TestContainersConfig.postgres::getPassword);
        registry.add("spring.rabbitmq.host", TestContainersConfig.rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", () -> TestContainersConfig.rabbitmq.getMappedPort(5672));
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("logging.level.root", () -> "INFO");
        registry.add("logging.level.ai.shreds", () -> "DEBUG");
    }

    @Test
    void shouldStartApplicationSuccessfully(CapturedOutput output) {
        logger.info("\n=== Application Startup Test Logs ===\n");

        // Verify core Spring Boot startup
        assertThat(output).contains("Started InventoryApplication");
        
        // Verify database initialization
        assertThat(output).contains("HikariPool");
        assertThat(output).contains("Database: jdbc:postgresql");
        assertThat(output).contains("Schema-validation: tables/sequences/triggers/constraints");
        
        // Verify RabbitMQ connection
        assertThat(output).contains("Successfully declared queue");
        assertThat(output).contains("Started RabbitMQ");
        
        // Verify no startup errors
        assertThat(output).doesNotContain("ERROR");
        assertThat(output).doesNotContain("FATAL");
        assertThat(output).doesNotContain("Could not autowire");
        assertThat(output).doesNotContain("UnsatisfiedDependencyException");
        
        // Print full startup logs for analysis
        logger.info("\n=== Full Application Startup Logs ===\n");
        logger.info(output.toString());
        logger.info("\n=== End of Startup Logs ===\n");
        
        // Additional verification of critical components
        assertThat(output).contains("Tomcat started");
        assertThat(output).contains("JPA initialized");
    }
}