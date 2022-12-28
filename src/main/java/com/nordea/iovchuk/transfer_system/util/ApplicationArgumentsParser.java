package com.nordea.iovchuk.transfer_system.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nordea.iovchuk.transfer_system.exception.NoAccountsImportFilePathException;
import com.nordea.iovchuk.transfer_system.json_pojo.Accounts;
import lombok.experimental.UtilityClass;
import org.springframework.boot.ApplicationArguments;

import java.io.File;
import java.io.IOException;

@UtilityClass
public class ApplicationArgumentsParser {

    public Accounts parseAccountsFromImportFile(final ApplicationArguments args) throws IOException {
        final File accountsFile = new File(getAccountsImportFilePath(args));
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(accountsFile, Accounts.class);
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
