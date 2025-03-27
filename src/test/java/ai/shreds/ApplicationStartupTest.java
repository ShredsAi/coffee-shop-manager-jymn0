package ai.shreds;

import ai.shreds.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class ApplicationStartupTest {

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", TestContainersConfig.postgres::getJdbcUrl);
        registry.add("spring.datasource.username", TestContainersConfig.postgres::getUsername);
        registry.add("spring.datasource.password", TestContainersConfig.postgres::getPassword);
        registry.add("spring.rabbitmq.host", TestContainersConfig.rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", () -> TestContainersConfig.rabbitmq.getMappedPort(5672));
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Test
    void shouldCaptureStartupLogs(CapturedOutput output) {
        // Verify that the application started successfully
        assertThat(output).contains("Started InventoryApplication");
        
        // Verify that critical components are initialized
        assertThat(output).contains("HikariPool");
        assertThat(output).contains("Started RabbitMQ");
        assertThat(output).contains("Hibernate");
        
        // Verify no error messages in the output
        assertThat(output).doesNotContain("ERROR");
        assertThat(output).doesNotContain("FATAL");
        
        // Print the full startup logs for analysis
        System.out.println("\n=== Application Startup Logs ===\n");
        System.out.println(output);
        System.out.println("\n=== End of Startup Logs ===\n");
    }
}