package com.dtt.utils;

import com.dtt.common.kafka.CommonKafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

/**
 * Kafka configuration for the generic-services module.
 * Bean names are prefixed with "generic" to avoid ConflictingBeanDefinitionException
 * with identically-named beans in other modules.
 */
@Configuration("genericKafkaConfig")
public class KafkaConfig {

    private CommonKafkaProperties kafkaProperties;

    public KafkaConfig(CommonKafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean("genericProducerFactory")
    public ProducerFactory<String, Object> genericProducerFactory() {
        Map<String, Object> config = kafkaProperties.baseProducerConfigs();
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean("genericKafkaTemplate")
    public KafkaTemplate<String, Object> genericKafkaTemplate() {
        return new KafkaTemplate<>(genericProducerFactory());
    }
}
