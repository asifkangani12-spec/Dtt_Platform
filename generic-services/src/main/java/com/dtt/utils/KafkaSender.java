package com.dtt.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaSender {

    private static final Logger logger = LoggerFactory.getLogger(KafkaSender.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaSender(
            @Qualifier("genericKafkaTemplate")
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Object message) {
        try {
            kafkaTemplate.send("dtt-log", message);
            logger.info("KafkaSender: message sent successfully");
        } catch (Exception e) {
            logger.error("KafkaSender: failed to send message", e);
        }
    }
}
