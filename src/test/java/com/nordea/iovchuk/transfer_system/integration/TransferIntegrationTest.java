package com.nordea.iovchuk.transfer_system.integration;

import com.example.exercises.transfersystem.transfer_request_response.ActionType;
import com.example.exercises.transfersystem.transfer_request_response.TransferRequestType;
import com.nordea.iovchuk.transfer_system.active_mq.transfer.consumer.TransferRequestJMSConsumer;
import com.nordea.iovchuk.transfer_system.active_mq.transfer.producer.TransferResponseJMSProducer;
import com.nordea.iovchuk.transfer_system.app_runner.AccountImporter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.utility.DockerImageName.parse;

@SpringBootTest
@Testcontainers
public class TransferIntegrationTest {

    @Container
    public static final GenericContainer<?> activeMqContainer = new GenericContainer<>(
            parse("rmohr/activemq:5.15.9-alpine"))
            .withExposedPorts(61616);

    @Autowired
    private TransferResponseJMSProducer producer;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private TransferRequestJMSConsumer consumer;

    @MockBean
    private AccountImporter accountImporter;

    @Value("${active-mq.transfer.response.queue}")
    private String queue;

    @Test
    public void whenSendingMessage_thenSuccessful() {
        final TransferRequestType requestType = new TransferRequestType();
        requestType.setRequestId("requestId");
        requestType.setTargetAccountNumber("targetAccountNumber");
        requestType.setAction(ActionType.DEBIT);
        requestType.setCurrency("currency");
        requestType.setQuantity(BigDecimal.valueOf(100));
        producer.send(requestType);
        TransferRequestType receivedRequestType = (TransferRequestType) jmsTemplate.receiveAndConvert(queue);
        assertEquals(requestType, receivedRequestType);
    }

}
