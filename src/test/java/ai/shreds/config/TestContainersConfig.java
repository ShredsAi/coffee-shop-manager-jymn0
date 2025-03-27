package ai.shreds.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.stream.Stream;

@TestConfiguration
public class TestContainersConfig {

    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    public static final RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.8-management-alpine")
            .withExposedPorts(5672, 15672)
            .withUser("guest", "guest");

    static {
        Startables.deepStart(Stream.of(postgres, rabbitmq)).join();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                "spring.datasource.url=" + postgres.getJdbcUrl(),
                "spring.datasource.username=" + postgres.getUsername(),
                "spring.datasource.password=" + postgres.getPassword(),
                "spring.rabbitmq.host=" + rabbitmq.getHost(),
                "spring.rabbitmq.port=" + rabbitmq.getMappedPort(5672),
                "spring.rabbitmq.username=" + rabbitmq.getAdminUsername(),
                "spring.rabbitmq.password=" + rabbitmq.getAdminPassword()
            ).applyTo(context.getEnvironment());
        }
    }
}