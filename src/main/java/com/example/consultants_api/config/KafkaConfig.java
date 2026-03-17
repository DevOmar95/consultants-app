package com.example.consultants_api.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic consultantCreatedTopic() {
        return new NewTopic("consultant.created", 1, (short) 1);
    }

    @Bean
    public NewTopic consultantUpdatedTopic() {
        return new NewTopic("consultant.updated", 1, (short) 1);
    }

    @Bean
    public NewTopic consultantDeletedTopic() {
        return new NewTopic("consultant.deleted", 1, (short) 1);
    }
}