package com.nordea.iovchuk.transfer_system.active_mq.transfer.producer;

import com.example.exercises.transfersystem.transfer_request_response.TransferRequestType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransferResponseJMSProducer {

    @Value("${active-mq.transfer.request.queue}")
    private String rqQueue;

    private final JmsTemplate jmsTemplate;

    @Autowired
    public TransferResponseJMSProducer(final JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void send(final TransferRequestType requestType) {
        try {
            log.info("Sending message to queue: [ {} ] ...", rqQueue);
            jmsTemplate.convertAndSend(rqQueue, requestType);
            log.info("Successfully sent message to queue: [ {} ] !", rqQueue);
        } catch (final Exception e) {
            log.error("Received exception during sending Message to queue: [ {} ]", rqQueue, e);
        }
    }
}
