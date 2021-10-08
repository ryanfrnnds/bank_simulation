package com.meutudo.bank.exceptions;

import com.meutudo.bank.exceptions.base.BusinessException;

public class OriginNotFoundException extends BusinessException {
    public OriginNotFoundException() {
        super();
    }

    public OriginNotFoundException(String message) {
        super(message);
    }

    public OriginNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OriginNotFoundException(Throwable cause) {
        super(cause);
    }
}
