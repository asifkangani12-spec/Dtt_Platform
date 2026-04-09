package com.dtt.organization.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrgKafkaSender {

    private static final Logger logger = LoggerFactory.getLogger(OrgKafkaSender.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrgKafkaSender(
            @Qualifier("orgKafkaTemplate")
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Object message) {
        try {
            kafkaTemplate.send("dtt-org-log", message);
            logger.info("OrgKafkaSender: message sent");
        } catch (Exception e) {
            logger.error("OrgKafkaSender: send failed", e);
        }
    }
}
