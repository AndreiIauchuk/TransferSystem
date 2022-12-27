package com.nordea.iovchuk.transfer_system.app_runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nordea.iovchuk.transfer_system.entity.AccountEntity;
import com.nordea.iovchuk.transfer_system.exception.NoAccountsImportFilePathException;
import com.nordea.iovchuk.transfer_system.json_pojo.Accounts;
import com.nordea.iovchuk.transfer_system.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountImporter implements ApplicationRunner {

    private final AccountRepository accountRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        importAccounts(args);
    }

    /**
     * Import the account information from a JSON file.
     * @param args application arguments
     * @throws IOException
     */
    private void importAccounts(final ApplicationArguments args) throws IOException {
        final Accounts accounts = parseFile(args);
        final List<AccountEntity> accountEntities = accounts.getAccountEntities();
        accountEntities.removeIf(account -> accountRepository.existsByNumber(account.getNumber()));
        accountEntities.forEach(account ->
                account.getCurrencyAmount().forEach(
                        currencyAmount -> currencyAmount.setAccount(account)));
        accountRepository.saveAll(accountEntities);
    }

    private Accounts parseFile(final ApplicationArguments args) throws IOException {
        final File accountsFile = new File(getFilePath(args));
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(accountsFile, Accounts.class);
    }

    private String getFilePath(final ApplicationArguments args) {
        final String[] stringArgs = args.getSourceArgs();
        for (String arg : stringArgs) {
            if (arg.contains(".json")) {
                return arg;
            }
        }
        throw new NoAccountsImportFilePathException();
    }
}
