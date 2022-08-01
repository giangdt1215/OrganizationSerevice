package com.optimagrowth.organizationservice.events.config;

import com.optimagrowth.organizationservice.events.model.OrganizationChangeModel;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public Map<String, Object> producerConfigs(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 240000);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 60000);
        return props;
    }

    @Bean
    public ProducerFactory<String, OrganizationChangeModel> orgChangeModelProducerFactory(){
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, OrganizationChangeModel> orgChangeModelKafkaTemplate(){
        return new KafkaTemplate<>(orgChangeModelProducerFactory());
    }
}
