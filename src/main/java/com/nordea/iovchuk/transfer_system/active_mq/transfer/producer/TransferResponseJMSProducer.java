package com.nordea.iovchuk.transfer_system.active_mq.transfer.producer;

import com.nordea.iovchuk.transfer_system.entity.Human;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferResponseJMSProducer {

    @Value("${active-mq.transfer.response.queue}")
    private String queue;

    private final JmsTemplate jmsTemplate;

    @Autowired
    public TransferResponseJMSProducer(final JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void send(final Human human) {
        try {
            log.info("Sending message to queue: [" + queue + "] ...");
            jmsTemplate.convertAndSend(queue, human);
            log.info("Successfully sent message to queue: [" + queue + "] !");
        } catch (Exception e) {
            log.error("Received exception during sending Message to queue: [" + queue + "]", e);
        }
    }

    public void sendText(final String text) {
        try {
            log.info("Sending message to queue: [" + queue + "] ...");
            jmsTemplate.send(queue, s -> s.createTextMessage(text));
            log.info("Successfully sent message to queue: [" + queue + "] !");
        } catch (Exception e) {
            log.error("Received exception during sending Message to queue: [" + queue + "]", e);
        }
    }
}
