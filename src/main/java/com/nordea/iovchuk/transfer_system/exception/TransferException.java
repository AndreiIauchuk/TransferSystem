package com.nordea.iovchuk.transfer_system.exception;

/**
 * Exception which will be thrown if some went wrong in transfer operation.
 */
public class TransferException extends Exception {

    public TransferException(final String message) {
        super(message);
    }
}
