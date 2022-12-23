package com.nordea.iovchuk.transfer_system.active_mq_transfer;

import com.nordea.iovchuk.transfer_system.active_mq.transfer.consumer.TransferRequestJMSConsumer;
import com.nordea.iovchuk.transfer_system.active_mq.transfer.producer.TransferResponseJMSProducer;
import com.nordea.iovchuk.transfer_system.config.ActiveMQTestConfig;
import com.nordea.iovchuk.transfer_system.entity.Human;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.utility.DockerImageName.parse;

@SpringBootTest(
        classes = {
                ActiveMQTestConfig.class,
                TransferResponseJMSProducer.class,
                TransferRequestJMSConsumer.class})
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

    @Value("${active-mq.transfer.response.queue}")
    private String queue;

    @Test
    public void whenSendingMessage_thenSuccessful() {
        final Human human = new Human("name", "surname");
        producer.send(human);
        Human receivedHuman = (Human) jmsTemplate.receiveAndConvert(queue);
        assertEquals(human, receivedHuman);
    }

}
