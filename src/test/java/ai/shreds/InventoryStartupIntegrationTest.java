package ai.shreds;

import ai.shreds.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestContainersConfig.Initializer.class)
@ExtendWith(OutputCaptureExtension.class)
@ActiveProfiles("test")
class InventoryStartupIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(InventoryStartupIntegrationTest.class);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", TestContainersConfig.postgres::getJdbcUrl);
        registry.add("spring.datasource.username", TestContainersConfig.postgres::getUsername);
        registry.add("spring.datasource.password", TestContainersConfig.postgres::getPassword);
        registry.add("spring.rabbitmq.host", TestContainersConfig.rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", () -> TestContainersConfig.rabbitmq.getMappedPort(5672));
    }

    @Test
    void shouldStartApplicationAndLogOutput(CapturedOutput output) {
        logger.info("\n=== Starting Application Startup Test ===\n");

        // Log the full startup output for analysis
        logger.info("\n=== Full Application Startup Logs ===\n");
        logger.info(output.toString());
        logger.info("\n=== End of Startup Logs ===\n");

        // Verify Spring Boot started successfully
        assertThat(output).contains("Started InventoryStartupIntegrationTest");
        assertThat(output).contains("Started InventoryApplication");

        // Verify critical infrastructure components
        assertThat(output).contains("HikariPool");
        assertThat(output).contains("Database: jdbc:postgresql");
        assertThat(output).contains("Hibernate");
        assertThat(output).contains("Started RabbitMQ");
        assertThat(output).contains("Tomcat started");

        // Verify no critical errors occurred
        assertThat(output).doesNotContain("ERROR");
        assertThat(output).doesNotContain("FATAL");
        assertThat(output).doesNotContain("Could not autowire");
        assertThat(output).doesNotContain("UnsatisfiedDependencyException");
        assertThat(output).doesNotContain("BeanCreationException");

        // Verify all required beans are created
        assertThat(output).contains("DomainServiceInventory");
        assertThat(output).contains("DomainServiceLowSupplyAlert");
        assertThat(output).contains("ApplicationInventoryService");
        assertThat(output).contains("InfrastructureInventoryRepositoryImpl");

        logger.info("\n=== Application Startup Test Completed Successfully ===\n");
    }
}