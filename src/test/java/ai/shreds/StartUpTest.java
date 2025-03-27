package ai.shreds;

import ai.shreds.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.Initializer.class)
class StartUpTest {

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
    void contextLoads() {
        // This test will fail if the application context cannot start
    }
}