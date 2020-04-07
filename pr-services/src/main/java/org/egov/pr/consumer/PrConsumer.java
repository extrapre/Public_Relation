package org.egov.pr.consumer;

import java.util.HashMap;

import org.egov.pr.service.notification.PrNotificationService;
import org.egov.pr.web.models.PrRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class PrConsumer {

    private PrNotificationService notificationService;

    @Autowired
    public PrConsumer(PrNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = {"${persister.update.pr.topic}","${persister.save.pr.topic}","${persister.update.pr.workflow.topic}"})
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        ObjectMapper mapper = new ObjectMapper();
        PrRequest PrRequest = new PrRequest();
        try {
            log.info("Consuming record: " + record);
            PrRequest = mapper.convertValue(record, PrRequest.class);
        } catch (final Exception e) {
            log.error("Error while listening to value: " + record + " on topic: " + topic + ": " + e);
        }
      
        notificationService.process(PrRequest);
    }



}
