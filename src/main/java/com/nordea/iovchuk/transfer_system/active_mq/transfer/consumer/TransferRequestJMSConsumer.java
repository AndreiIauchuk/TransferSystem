package com.nordea.iovchuk.transfer_system.active_mq.transfer.consumer;

import com.example.exercises.transfersystem.transfer_request_response.OutcomeType;
import com.example.exercises.transfersystem.transfer_request_response.TransferRequestType;
import com.example.exercises.transfersystem.transfer_request_response.TransferResponseType;
import com.nordea.iovchuk.transfer_system.mapstruct.TransferRequestToResponseTypeMapper;
import com.nordea.iovchuk.transfer_system.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferRequestJMSConsumer {

    private final TransferService transferService;
    private final TransferRequestToResponseTypeMapper mapper;

    @Value("${active-mq.transfer.request.queue}")
    private String rqQueue;

    @Value("${active-mq.transfer.response.queue}")
    private String rsQueue;

    @JmsListener(destination = "${active-mq.transfer.request.queue}")
    @SendTo("${active-mq.transfer.response.queue}")
    public TransferResponseType onMessage(final TransferRequestType requestType) {
        log.info("Getting message from queue: [ {} ] ...", rqQueue);
        try {
            transferService.transfer(requestType);
            log.info("Sent ACCEPT response message to queue [ {} ]", rsQueue);
            return acceptedResponse(requestType);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            log.info("Sent REJECT response message to queue [ {} ]", rsQueue);
            return rejectedResponse(requestType);
        }
    }

    private TransferResponseType acceptedResponse(final TransferRequestType requestType) {
        final TransferResponseType responseType = mapper.toResponseType(requestType);
        responseType.setOutcome(OutcomeType.ACCEPT);
        return responseType;
    }

    private TransferResponseType rejectedResponse(final TransferRequestType requestType) {
        final TransferResponseType responseType = mapper.toResponseType(requestType);
        responseType.setOutcome(OutcomeType.REJECT);
        return responseType;
    }
}
