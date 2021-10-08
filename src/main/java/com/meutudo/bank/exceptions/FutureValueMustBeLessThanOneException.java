package com.meutudo.bank.exceptions;

import com.meutudo.bank.exceptions.base.BusinessException;

public class FutureValueMustBeLessThanOneException extends BusinessException {
    public FutureValueMustBeLessThanOneException() {
        super();
    }

    public FutureValueMustBeLessThanOneException(String message) {
        super(message);
    }

    public FutureValueMustBeLessThanOneException(String message, Throwable cause) {
        super(message, cause);
    }

    public FutureValueMustBeLessThanOneException(Throwable cause) {
        super(cause);
    }
}
