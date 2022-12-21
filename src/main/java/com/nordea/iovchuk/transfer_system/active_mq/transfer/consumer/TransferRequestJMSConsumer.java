package com.nordea.iovchuk.transfer_system.active_mq.transfer.consumer;

import com.nordea.iovchuk.transfer_system.entity.Human;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferRequestJMSConsumer{

    @Value("${active-mq.transfer.request.queue}")
    private String queue;

    @Value("${active-mq.transfer.response.queue}")
    private String responseQueue;

    @JmsListener(destination = "${active-mq.transfer.response.queue}")
    @SendTo("[topic for answers]")
    public void onMessage(final Human human) {
        log.info("Getting message from queue: " + responseQueue + " ...");
        log.info("Message = " + human);
    }
}
