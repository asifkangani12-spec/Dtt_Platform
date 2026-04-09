package com.dtt.common.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared Kafka producer properties helper.
 * Each module injects this to build its own uniquely-named ProducerFactory
 * rather than all modules defining identical @Bean producerFactory() methods
 * which caused ConflictingBeanDefinitionException.
 */
@Component
public class CommonKafkaProperties {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    public Map<String, Object> baseProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return props;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

}
