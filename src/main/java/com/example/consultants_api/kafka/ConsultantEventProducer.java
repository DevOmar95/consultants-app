package com.example.consultants_api.kafka;

import com.example.consultants_api.model.Consultant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultantEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCreatedEvent(Consultant consultant) {
        log.info("Sending created event for consultant: {} {}", consultant.getFirstName(), consultant.getLastName());
        kafkaTemplate.send("consultant.created", consultant);
    }

    public void sendUpdatedEvent(Consultant consultant) {
        log.info("Sending updated event for consultant id: {}", consultant.getId());
        kafkaTemplate.send("consultant.updated", consultant);
    }

    public void sendDeletedEvent(Long id) {
        log.info("Sending deleted event for consultant id: {}", id);
        kafkaTemplate.send("consultant.deleted", id);
    }
}