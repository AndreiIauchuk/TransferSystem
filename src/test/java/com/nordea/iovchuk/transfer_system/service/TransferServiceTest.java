package com.nordea.iovchuk.transfer_system.service;

import com.example.exercises.transfersystem.transfer_request_response.ActionType;
import com.example.exercises.transfersystem.transfer_request_response.TransferRequestType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nordea.iovchuk.transfer_system.entity.AccountEntity;
import com.nordea.iovchuk.transfer_system.entity.CurrencyAmountEntity;
import com.nordea.iovchuk.transfer_system.exception.TransferException;
import com.nordea.iovchuk.transfer_system.json_pojo.Accounts;
import com.nordea.iovchuk.transfer_system.repository.CurrencyAmountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    static final String ACC_NUMBER = "ACC_NUMBER";
    static final String CURRENCY = "CUR";

    @Mock
    CurrencyAmountRepository currencyAmountRepository;

    @Mock
    ApplicationArguments args;

    @Mock
    ApplicationArgumentsParser argumentsParser;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    TransferService service;

    @Nested
    class UpdateDBTest {

        @BeforeEach
        public void beforeEach() {
            CurrencyAmountEntity currencyAmountEntity = new CurrencyAmountEntity();
            currencyAmountEntity.setAmount(BigDecimal.valueOf(100));
            currencyAmountEntity.setCurrency(CURRENCY);
            Mockito.when(currencyAmountRepository.findByAccount_NumberAndCurrency(any(), any()))
                    .thenReturn(Optional.of(currencyAmountEntity));
            lenient().when(currencyAmountRepository.save(any())).thenReturn(new CurrencyAmountEntity());
        }

        @Test
        public void whenDebit_thenSuccessful() throws TransferException {
            service.updateDB(debitTransferRequestType());
        }

        @Test
        public void whenCredit_thenSuccessful() throws TransferException {
            service.updateDB(creditTransferRequestType());
        }

        @Test
        public void whenActionIsDebitAndAmountIsNotEnough_thenThrowTransferException() {
            TransferRequestType requestType = debitTransferRequestType();
            requestType.setQuantity(BigDecimal.valueOf(200));
            Exception exception = assertThrows(
                    TransferException.class,
                    () -> service.updateDB(requestType)
            );
            assertTrue(exception.getMessage().contains(
                    "Account number [ " + ACC_NUMBER + " ] : Not enough funds for transfer!"));
        }
    }

    @Nested
    class UpdateImportFileTest {

        @BeforeEach
        public void beforeEach() throws IOException {
            Mockito.when(argumentsParser.parseAccountsFromImportFile(args)).thenReturn(accounts());
            lenient().when(argumentsParser.getAccountsImportFilePath(args)).thenReturn("");
            lenient().when(objectMapper.writerWithDefaultPrettyPrinter())
                    .thenReturn(new ObjectMapper().writerWithDefaultPrettyPrinter());
        }

        private Accounts accounts() {
            Accounts accounts = new Accounts();
            LinkedList<AccountEntity> accountEntities = new LinkedList<>();
            AccountEntity accountEntity = new AccountEntity();
            accountEntity.setNumber(ACC_NUMBER);
            CurrencyAmountEntity currencyAmountEntity = new CurrencyAmountEntity();
            currencyAmountEntity.setAmount(BigDecimal.valueOf(100));
            currencyAmountEntity.setCurrency(CURRENCY);
            accountEntity.setCurrencyAmount(List.of(currencyAmountEntity));
            accountEntities.add(accountEntity);
            accounts.setAccountEntities(accountEntities);
            return accounts;
        }

        @Test
        public void whenDebit_thenSuccessful() {
            assertThrows(
                    TransferException.class,
                    () -> service.updateImportFile(debitTransferRequestType()));
        }

        @Test
        public void whenCredit_thenSuccessful() {
            assertThrows(
                    TransferException.class,
                    () -> service.updateImportFile(creditTransferRequestType()));
        }

        @Test
        public void whenActionIsDebitAndAmountIsNotEnough_thenThrowTransferException() {
            TransferRequestType requestType = debitTransferRequestType();
            requestType.setQuantity(BigDecimal.valueOf(200));
            Exception exception = assertThrows(
                    TransferException.class,
                    () -> service.updateImportFile(requestType)
            );
            assertTrue(exception.getMessage().contains(
                    "Account number [ " + ACC_NUMBER + " ] : Not enough funds for transfer!"));
        }
    }

    private TransferRequestType debitTransferRequestType() {
        TransferRequestType requestType = transferRequestType();
        requestType.setCurrency(CURRENCY);
        requestType.setAction(ActionType.DEBIT);
        return requestType;
    }

    private TransferRequestType creditTransferRequestType() {
        TransferRequestType requestType = transferRequestType();
        requestType.setCurrency(CURRENCY);
        requestType.setAction(ActionType.CREDIT);
        return requestType;
    }

    private TransferRequestType transferRequestType() {
        TransferRequestType requestType = new TransferRequestType();
        requestType.setQuantity(BigDecimal.valueOf(10));
        requestType.setTargetAccountNumber(ACC_NUMBER);
        return requestType;
    }
}
