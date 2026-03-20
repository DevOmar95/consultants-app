package com.example.consultants_api.kafka;

import com.example.consultants_api.model.Consultant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
public class ConsultantEventConsumer {

    @KafkaListener(topics = "consultant.created", groupId = "consultants-group")
    public void onCreated(Consultant consultant) {
        log.info("Received created event: {} {}", consultant.getFirstName(), consultant.getLastName());
    }

    @KafkaListener(topics = "consultant.updated", groupId = "consultants-group")
    public void onUpdated(Consultant consultant) {
        log.info("Received updated event for consultant id: {}", consultant.getId());
    }

    @KafkaListener(topics = "consultant.deleted", groupId = "consultants-group")
    public void onDeleted(Long id) {
        log.info("Received deleted event for consultant id: {}", id);
    }
}