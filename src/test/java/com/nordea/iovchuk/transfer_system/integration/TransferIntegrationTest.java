package com.nordea.iovchuk.transfer_system.integration;

import com.example.exercises.transfersystem.transfer_request_response.ActionType;
import com.example.exercises.transfersystem.transfer_request_response.OutcomeType;
import com.example.exercises.transfersystem.transfer_request_response.TransferRequestType;
import com.example.exercises.transfersystem.transfer_request_response.TransferResponseType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nordea.iovchuk.transfer_system.app_runner.AccountImporter;
import com.nordea.iovchuk.transfer_system.entity.AccountEntity;
import com.nordea.iovchuk.transfer_system.entity.CurrencyAmountEntity;
import com.nordea.iovchuk.transfer_system.json_pojo.Accounts;
import com.nordea.iovchuk.transfer_system.repository.AccountRepository;
import com.nordea.iovchuk.transfer_system.repository.CurrencyAmountRepository;
import com.nordea.iovchuk.transfer_system.service.ApplicationArgumentsParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(args = {"example-transfer-system.json"})
public class TransferIntegrationTest {

    static final String CURRENCY = "CUR";
    static final String ACC_NUMBER = "NUMBER";

    @Autowired
    private JmsTemplate jmsTemplate;

    @MockBean
    private AccountImporter accountImporter;

    @Autowired
    private ApplicationArguments args;

    @Autowired
    private ApplicationArgumentsParser argumentsParser;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    CurrencyAmountRepository currencyAmountRepository;

    @Autowired
    AccountRepository accountRepository;

    @Value("${active-mq.transfer.request.queue}")
    private String rqQueue;

    @Value("${active-mq.transfer.response.queue}")
    private String rsQueue;

    @BeforeEach
    public void beforeEach() {
        initDB();
        initImportFile();
    }

    private void initDB() {
        AccountEntity accountEntity = accountEntity();
        CurrencyAmountEntity currencyAmountEntity = accountEntity.getCurrencyAmount().get(0);
        currencyAmountEntity.setAccount(accountEntity);
        accountRepository.save(accountEntity);
    }

    private AccountEntity accountEntity() {
        CurrencyAmountEntity currencyAmountEntity = currencyAmountEntity();
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setNumber(ACC_NUMBER);
        List<CurrencyAmountEntity> currencyAmountEntities = new ArrayList<>();
        currencyAmountEntities.add(currencyAmountEntity);
        accountEntity.setCurrencyAmount(currencyAmountEntities);
        return accountEntity;
    }

    private CurrencyAmountEntity currencyAmountEntity() {
        CurrencyAmountEntity currencyAmountEntity = new CurrencyAmountEntity();
        currencyAmountEntity.setCurrency(CURRENCY);
        currencyAmountEntity.setAmount(BigDecimal.valueOf(100));
        return currencyAmountEntity;
    }

    private void initImportFile() {
        try {
            File accountsFile = getImportFile();
            Accounts accounts = accounts();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(accountsFile, accounts);
        } catch (IOException ignored) {
        }
    }

    private Accounts accounts() {
        Accounts accounts = new Accounts();
        LinkedList<AccountEntity> accountEntities = new LinkedList<>();
        accountEntities.add(accountEntity());
        accounts.setAccountEntities(accountEntities);
        return accounts;
    }

    @Test
    public void whenSendingMessage_thenSuccessful() throws IOException {
        final TransferRequestType requestType = new TransferRequestType();
        requestType.setRequestId("requestId");
        requestType.setTargetAccountNumber(ACC_NUMBER);
        requestType.setAction(ActionType.DEBIT);
        requestType.setCurrency(CURRENCY);
        requestType.setQuantity(BigDecimal.valueOf(75));
        jmsTemplate.convertAndSend(rqQueue, requestType);
        TransferResponseType receivedResponseType = (TransferResponseType) jmsTemplate.receiveAndConvert(rsQueue);

        assertNotNull(receivedResponseType);
        checkResponseType(receivedResponseType, expectedTransferResponseType());
        checkDB();
        checkImportFile();
    }

    void checkResponseType(TransferResponseType responseType, TransferResponseType expectedResponseType) {
        assertEquals(responseType.getRequestId(), expectedResponseType.getRequestId());
        assertEquals(responseType.getTargetAccountNumber(), expectedResponseType.getTargetAccountNumber());
        assertEquals(responseType.getAction(), expectedResponseType.getAction());
        assertEquals(responseType.getCurrency(), expectedResponseType.getCurrency());
        assertEquals(responseType.getQuantity(), expectedResponseType.getQuantity());
        assertEquals(responseType.getOutcome(), OutcomeType.ACCEPT);
    }

    TransferResponseType expectedTransferResponseType() {
        final TransferResponseType responseType = new TransferResponseType();
        responseType.setRequestId("requestId");
        responseType.setTargetAccountNumber(ACC_NUMBER);
        responseType.setAction(ActionType.DEBIT);
        responseType.setCurrency(CURRENCY);
        responseType.setQuantity(BigDecimal.valueOf(75));
        responseType.setOutcome(OutcomeType.ACCEPT);
        return responseType;
    }

    void checkDB() {
        Optional<CurrencyAmountEntity> optCurrencyAmountEntity =
                currencyAmountRepository.findByAccount_NumberAndCurrency(ACC_NUMBER, CURRENCY);
        assertTrue(optCurrencyAmountEntity.isPresent());
        CurrencyAmountEntity currencyAmountEntity = optCurrencyAmountEntity.get();
        assertEquals(BigDecimal.valueOf(25.0).compareTo(currencyAmountEntity.getAmount()), 0);
    }

    void checkImportFile() throws IOException {
        final Accounts accounts = argumentsParser.parseAccountsFromImportFile();
        LinkedList<AccountEntity> accountEntities = accounts.getAccountEntities();
        AccountEntity accountEntity = accountEntities.get(0);
        CurrencyAmountEntity currencyAmountEntity = accountEntity.getCurrencyAmount().get(0);
        assertEquals(BigDecimal.valueOf(25.0).compareTo(currencyAmountEntity.getAmount()), 0);
    }

    @AfterEach
    public void afterEach() {
        clearImportFile();
    }

    void clearImportFile() {
        try {
            File accountsFile = getImportFile();
            objectMapper.writeValue(accountsFile, null);
        } catch (IOException ignored) {
        }
    }

    File getImportFile() {
        try {
            final URL accountsFileUrl = getClass().getClassLoader()
                    .getResource(argumentsParser.getAccountsImportFilePath(args));
            assertNotNull(accountsFileUrl);
            return new File(accountsFileUrl.toURI());
        } catch (URISyntaxException ignored) {
            return null;
        }
    }

}
