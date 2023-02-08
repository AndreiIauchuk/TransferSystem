package com.nordea.iovchuk.transfer_system.app_runner;

import com.nordea.iovchuk.transfer_system.entity.AccountEntity;
import com.nordea.iovchuk.transfer_system.json_pojo.Accounts;
import com.nordea.iovchuk.transfer_system.repository.AccountRepository;
import com.nordea.iovchuk.transfer_system.service.ApplicationArgumentsParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountImporter implements ApplicationRunner {

    private final AccountRepository accountRepository;
    private final ApplicationArgumentsParser argumentsParser;

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        importAccounts();
    }

    /**
     * Import the account information from a JSON file.
     *
     * @throws IOException IOException
     */
    private void importAccounts() throws IOException {
        log.info("Importing accounts...");
        final Accounts accounts = argumentsParser.parseAccountsFromImportFile();
        final List<AccountEntity> accountEntities = accounts.getAccountEntities();
        accountEntities.removeIf(account -> accountRepository.existsByNumber(account.getNumber()));

        if (accountEntities.isEmpty()) {
            log.info("No new accounts to import!");
            return;
        }

        accountEntities.forEach(account ->
                account.getCurrencyAmount().forEach(
                        currencyAmount -> currencyAmount.setAccount(account)));
        accountRepository.saveAll(accountEntities);
        log.info("{} accounts were successfully imported", accountEntities.size());
    }
}
