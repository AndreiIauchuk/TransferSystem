package com.nordea.iovchuk.transfer_system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nordea.iovchuk.transfer_system.exception.NoAccountsImportFilePathException;
import com.nordea.iovchuk.transfer_system.json_pojo.Accounts;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
@RequiredArgsConstructor
public class ApplicationArgumentsParser {

    private final ObjectMapper objectMapper;
    private final ApplicationArguments args;

    public Accounts parseAccountsFromImportFile() throws IOException {
        URL accountsFileUrl = getClass().getClassLoader().getResource(getAccountsImportFilePath(args));
        return objectMapper.readValue(accountsFileUrl, Accounts.class);
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
