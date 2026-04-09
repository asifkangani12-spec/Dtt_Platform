package ug.daes.onboarding.service.impl;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ug.daes.onboarding.dto.LogModelDTO;

@Service
public class OnbKafkaSender {
    private static final Logger logger = LoggerFactory.getLogger(OnbKafkaSender.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OnbKafkaSender(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${com.dt.kafka.topic}")
    private String topicName;

    @Value("${com.dt.kafka.topic.central}")
    private String centralTopicName;


    public void send(LogModelDTO logmodel) {
        logger.info("Kafka -> Sending LogModelDTO to topic:  {} " , topicName);
       logger.info("Central Topic => {}" , centralTopicName);
        kafkaTemplate.send(topicName, logmodel);
        // To send to central topic:
         kafkaTemplate.send(centralTopicName, logmodel);
    }


    public void sendString(String logmodel) {
        logger.info("Kafka -> Sending String message to topic: {}" , topicName);
        kafkaTemplate.send(topicName, logmodel);
        // To send to central topic:
         kafkaTemplate.send(centralTopicName, logmodel);
    }
}
