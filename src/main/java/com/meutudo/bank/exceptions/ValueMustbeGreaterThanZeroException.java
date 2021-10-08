package com.meutudo.bank.exceptions;

import com.meutudo.bank.exceptions.base.BusinessException;

public class ValueMustbeGreaterThanZeroException extends BusinessException {
    public ValueMustbeGreaterThanZeroException() {
        super();
    }

    public ValueMustbeGreaterThanZeroException(String message) {
        super(message);
    }

    public ValueMustbeGreaterThanZeroException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValueMustbeGreaterThanZeroException(Throwable cause) {
        super(cause);
    }
}
