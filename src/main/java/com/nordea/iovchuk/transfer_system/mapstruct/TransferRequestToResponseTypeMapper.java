package com.nordea.iovchuk.transfer_system.mapstruct;

import com.example.exercises.transfersystem.transfer_request_response.TransferRequestType;
import com.example.exercises.transfersystem.transfer_request_response.TransferResponseType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferRequestToResponseTypeMapper {

    @Mapping(target = "outcome", ignore = true)
    TransferResponseType toResponseType(final TransferRequestType requestType);
}
