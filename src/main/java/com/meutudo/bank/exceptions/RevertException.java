package com.meutudo.bank.exceptions;

import com.meutudo.bank.exceptions.base.BusinessException;

public class RevertException extends BusinessException {
    public RevertException() {
        super();
    }

    public RevertException(String message) {
        super(message);
    }

    public RevertException(String message, Throwable cause) {
        super(message, cause);
    }

    public RevertException(Throwable cause) {
        super(cause);
    }
}
