package com.dtt.organization.util;

import com.dtt.common.kafka.CommonKafkaProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration for the organization module.
 * Bean names are prefixed "org" to prevent ConflictingBeanDefinitionException.
 */
@Configuration("orgKafkaConfig")
public class OrgKafkaConfig {

    private final CommonKafkaProperties kafkaProperties;

    public OrgKafkaConfig(CommonKafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Value("${com.dt.kafka.topic.central:dtt-org-central}")
    private String topic;

    @Value("${com.dt.kafka.topic.ra:dtt-org-ra}")
    private String topicRA;

    @Bean("orgProducerFactory")
    public ProducerFactory<String, Object> orgProducerFactory() {
        Map<String, Object> config = new HashMap<>(kafkaProperties.baseProducerConfigs());
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean("orgKafkaTemplate")
    public KafkaTemplate<String, Object> orgKafkaTemplate() {
        return new KafkaTemplate<>(orgProducerFactory());
    }
}
