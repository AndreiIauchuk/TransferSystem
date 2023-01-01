package com.nordea.iovchuk.transfer_system.service;

import com.example.exercises.transfersystem.transfer_request_response.ActionType;
import com.example.exercises.transfersystem.transfer_request_response.TransferRequestType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nordea.iovchuk.transfer_system.entity.AccountEntity;
import com.nordea.iovchuk.transfer_system.entity.CurrencyAmountEntity;
import com.nordea.iovchuk.transfer_system.exception.TransferException;
import com.nordea.iovchuk.transfer_system.json_pojo.Accounts;
import com.nordea.iovchuk.transfer_system.repository.CurrencyAmountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private final CurrencyAmountRepository currencyAmountRepository;
    private final ApplicationArguments args;
    private final ApplicationArgumentsParser argumentsParser;
    private final ObjectMapper objectMapper;

    /**
     * Transfer operation.
     *
     * @param requestType TransferRequestType
     */
    @Transactional(rollbackFor = TransferException.class)
    public void transfer(final TransferRequestType requestType) throws TransferException {
        final String accountNumber = requestType.getTargetAccountNumber();
        log.info("Account number [ {} ] : Transfer operation started...", accountNumber);
        updateDB(requestType);
        updateImportFile(requestType);
        log.info("Account number [ {} ] : Transfer operation successfully ended!", accountNumber);
    }

    /**
     *
     */
    public void updateDB(final TransferRequestType requestType) throws TransferException {
        final String accountNumber = requestType.getTargetAccountNumber();
        final ActionType actionType = requestType.getAction();
        final BigDecimal quantity = requestType.getQuantity();
        final CurrencyAmountEntity currencyAmount = findCurrencyAmountInDB(requestType);
        setAmountBasedOnActionType(actionType, quantity, currencyAmount, accountNumber);
        currencyAmountRepository.save(currencyAmount);
    }

    private CurrencyAmountEntity findCurrencyAmountInDB(final TransferRequestType requestType) throws TransferException {
        final String accountNumber = requestType.getTargetAccountNumber();
        final String currency = requestType.getCurrency();
        final Optional<CurrencyAmountEntity> optCurrencyAmount =
                currencyAmountRepository.findByAccount_NumberAndCurrency(accountNumber, currency);

        if (optCurrencyAmount.isEmpty()) {
            throw new TransferException(
                    "Account number [ " + accountNumber + " ] : Account with number [ " + accountNumber + " ]" +
                            " OR presented currency [ " + currency + " ] with such account number is not exist in DB!"
            );
        }
        return optCurrencyAmount.get();
    }

    /**
     *
     */
    public void updateImportFile(final TransferRequestType requestType) throws TransferException {
        final ActionType actionType = requestType.getAction();
        final BigDecimal quantity = requestType.getQuantity();
        final String currency = requestType.getCurrency();
        final Accounts accounts = findAccountsInImportFile();
        final CurrencyAmountEntity currencyAmount = findCurrencyAmountInAccounts(accounts, requestType);
        final String accountNumber = requestType.getTargetAccountNumber();
        if (currencyAmount == null) {
            log.error(
                    "Account number [ {} ] : Presented currency [ {} ] with account number [ {} ]" +
                            " is not exist in import file!",
                    accountNumber, currency, accountNumber);
            return;
        }

        setAmountBasedOnActionType(actionType, quantity, currencyAmount, accountNumber);

        try {
            final File accountsFile = new File(argumentsParser.getAccountsImportFilePath(args));
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(accountsFile, accounts);
        } catch (IOException e) {
            throw new TransferException(e.getMessage());
        }
    }

    private void setAmountBasedOnActionType(ActionType actionType, BigDecimal quantity,
                                            CurrencyAmountEntity currencyAmount, String accountNumber)
            throws TransferException {
        final BigDecimal currentAmount = currencyAmount.getAmount();
        if (actionType.equals(ActionType.DEBIT)) {
            if (currentAmount.compareTo(quantity) < 0) {
                throw new TransferException(
                        "Account number [ " + accountNumber + " ] : Not enough funds for transfer!");
            }
            currencyAmount.setAmount(currentAmount.subtract(quantity));
        } else { //CREDIT
            currencyAmount.setAmount(currentAmount.add(quantity));
        }
    }

    private CurrencyAmountEntity findCurrencyAmountInAccounts(
            final Accounts accounts,
            final TransferRequestType requestType) {
        final String accountNumber = requestType.getTargetAccountNumber();
        final String currency = requestType.getCurrency();
        final List<AccountEntity> accountEntities = accounts.getAccountEntities();
        final AccountEntity account = accountEntities.stream()
                .filter(accountEntity -> accountEntity.getNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);

        if (account == null) {
            log.error(
                    "Account number [ {} ] : Account with number [ {} ] is not exist in import file!",
                    accountNumber, accountNumber);
            return null;
        }

        final List<CurrencyAmountEntity> currencyAmounts = account.getCurrencyAmount();
        return currencyAmounts.stream()
                .filter(currencyAmountEntity -> currencyAmountEntity.getCurrency().equals(currency))
                .findFirst()
                .orElse(null);
    }

    private Accounts findAccountsInImportFile() throws TransferException {
        try {
            return argumentsParser.parseAccountsFromImportFile(args);
        } catch (Exception e) {
            throw new TransferException(e.getMessage());
        }
    }
}
