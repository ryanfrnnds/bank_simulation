package com.meutudo.bank.exceptions;

import com.meutudo.bank.exceptions.base.BusinessException;

public class InsufficientFoundsException extends BusinessException {

    public InsufficientFoundsException() {
        super();
    }

    public InsufficientFoundsException(String message) {
        super(message);
    }

    public InsufficientFoundsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientFoundsException(Throwable cause) {
        super(cause);
    }
}
