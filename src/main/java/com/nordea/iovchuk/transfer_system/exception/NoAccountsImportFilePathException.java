package com.nordea.iovchuk.transfer_system.exception;

/**
 * Exception which will be thrown if no accounts import file path is presented in app args.
 */
public class NoAccountsImportFilePathException extends RuntimeException {

    public NoAccountsImportFilePathException() {
        super("Accounts import file path is not found!");
    }
}
