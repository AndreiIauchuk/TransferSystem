package com.nordea.iovchuk.transfer_system.controller;

import com.example.exercises.transfersystem.transfer_request_response.ActionType;
import com.example.exercises.transfersystem.transfer_request_response.TransferRequestType;
import com.nordea.iovchuk.transfer_system.active_mq.transfer.producer.TransferResponseJMSProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

//@Profile("dev")
@RestController
@RequiredArgsConstructor
public class TestController {

    private final TransferResponseJMSProducer jmsProducer;

    /**
     * Credit test endpoint.
     */
    @GetMapping("/credit")
    public void credit() {
        final TransferRequestType requestType = new TransferRequestType();
        requestType.setRequestId("requestId");
        requestType.setTargetAccountNumber("targetAccountNumber");
        requestType.setAction(ActionType.DEBIT);
        requestType.setCurrency("currency");
        requestType.setQuantity(BigDecimal.valueOf(100));
        jmsProducer.send(requestType);
    }

    /**
     * Debit test endpoint.
     */
    @GetMapping
    public void debit() {}

}
