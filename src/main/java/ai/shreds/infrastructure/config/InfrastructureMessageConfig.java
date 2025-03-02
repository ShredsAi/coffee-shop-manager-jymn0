package ai.shreds.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.JacksonUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class InfrastructureMessageConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.retries}")
    private Integer retries;

    @Value("${spring.kafka.producer.batch-size}")
    private Integer batchSize;

    @Value("${spring.kafka.producer.linger-ms}")
    private Integer lingerMs;

    @Value("${spring.kafka.producer.buffer-memory}")
    private Integer bufferMemory;

    @Value("${spring.kafka.producer.acks}")
    private String acks;

    @Value("${payment.kafka.order-notification-topic}")
    private String orderNotificationTopic;

    @Value("${payment.kafka.financial-notification-topic}")
    private String financialNotificationTopic;

    @Bean
    public ProducerFactory<String, Object> kafkaProducerFactory() {
        log.info("Configuring Kafka producer with bootstrap servers: {}", bootstrapServers);

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // Reliability configurations
        configProps.put(ProducerConfig.RETRIES_CONFIG, retries);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        configProps.put(ProducerConfig.ACKS_CONFIG, acks);

        // Idempotence and transaction configurations
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        
        // Compression for better performance
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        return new DefaultKafkaProducerFactory<>(configProps, new StringSerializer(), new JsonSerializer<>(objectMapper()));
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(kafkaProducerFactory());
        template.setMessageConverter(new StringJsonMessageConverter());
        return template;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonUtils.enhancedObjectMapper();
    }

    @Bean
    public String orderNotificationTopic() {
        return orderNotificationTopic;
    }

    @Bean
    public String financialNotificationTopic() {
        return financialNotificationTopic;
    }
}
