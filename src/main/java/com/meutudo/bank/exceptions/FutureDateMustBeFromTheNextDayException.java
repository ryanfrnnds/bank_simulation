package com.meutudo.bank.exceptions;

import com.meutudo.bank.exceptions.base.BusinessException;

public class FutureDateMustBeFromTheNextDayException extends BusinessException {
    public FutureDateMustBeFromTheNextDayException() {
        super();
    }

    public FutureDateMustBeFromTheNextDayException(String message) {
        super(message);
    }

    public FutureDateMustBeFromTheNextDayException(String message, Throwable cause) {
        super(message, cause);
    }

    public FutureDateMustBeFromTheNextDayException(Throwable cause) {
        super(cause);
    }
}
