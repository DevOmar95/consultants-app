package com.example.consultants_api;

import com.example.consultants_api.kafka.ConsultantEventProducer;
import com.example.consultants_api.model.Consultant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsultantEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private ConsultantEventProducer producer;

    @Test
    void sendCreatedEvent_sendsToCorrectTopic() {
        Consultant c = new Consultant(1L, "John", "Doe", "john@test.com", "123", "Java", "ACME");

        producer.sendCreatedEvent(c);

        verify(kafkaTemplate).send("consultant.created", c);
    }

    @Test
    void sendUpdatedEvent_sendsToCorrectTopic() {
        Consultant c = new Consultant(1L, "John", "Doe", "john@test.com", "123", "Java", "ACME");

        producer.sendUpdatedEvent(c);

        verify(kafkaTemplate).send("consultant.updated", c);
    }

    @Test
    void sendDeletedEvent_sendsIdToCorrectTopic() {
        producer.sendDeletedEvent(42L);

        verify(kafkaTemplate).send("consultant.deleted", 42L);
    }
}
