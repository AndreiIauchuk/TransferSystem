package com.nordea.iovchuk.transfer_system.controller;

import com.nordea.iovchuk.transfer_system.active_mq.transfer.producer.TransferResponseJMSProducer;
import com.nordea.iovchuk.transfer_system.entity.Human;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//@Profile("dev")
@RestController
public class TestController {

    private final TransferResponseJMSProducer jmsProducer;

    public TestController(TransferResponseJMSProducer jmsProducer) {
        this.jmsProducer = jmsProducer;
    }

    @GetMapping("/credit")
    public void credit() {
        final Human human = new Human("Andrew", "Iovchuk");
        jmsProducer.send(human);
    }
}
