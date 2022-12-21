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

   private final JmsTemplate jmsTemplate;

    @Value("${active-mq.transfer.response.queue}")
    private String queue;

    @Autowired
    public TransferResponseJMSProducer(final JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void send(final Human human) {
        try {
            log.info("Sending message to queue: " + queue + " ...");
            jmsTemplate.convertAndSend(queue, human);
        } catch (Exception e) {
            log.error("Received exception during sending Message to queue: " + queue, e);
        }
    }
}
