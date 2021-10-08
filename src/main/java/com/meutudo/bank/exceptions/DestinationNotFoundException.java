package com.meutudo.bank.exceptions;

import com.meutudo.bank.exceptions.base.BusinessException;

public class DestinationNotFoundException extends BusinessException {
    public DestinationNotFoundException() {
        super();
    }

    public DestinationNotFoundException(String message) {
        super(message);
    }

    public DestinationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DestinationNotFoundException(Throwable cause) {
        super(cause);
    }
}
