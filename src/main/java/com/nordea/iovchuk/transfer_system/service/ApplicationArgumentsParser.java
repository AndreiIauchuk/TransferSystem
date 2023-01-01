package com.nordea.iovchuk.transfer_system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nordea.iovchuk.transfer_system.exception.NoAccountsImportFilePathException;
import com.nordea.iovchuk.transfer_system.json_pojo.Accounts;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ApplicationArgumentsParser {

    private final ObjectMapper objectMapper;

    public Accounts parseAccountsFromImportFile(final ApplicationArguments args) throws IOException {
        final File accountsFile = new File(getAccountsImportFilePath(args));
        return objectMapper.readValue(accountsFile, Accounts.class);
    }

    public String getAccountsImportFilePath(final ApplicationArguments args) {
        final String[] stringArgs = args.getSourceArgs();
        for (String arg : stringArgs) {
            if (arg.contains(".json")) {
                return arg;
            }
        }
        throw new NoAccountsImportFilePathException();
    }
}
