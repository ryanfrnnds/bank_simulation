package com.meutudo.bank.exceptions;

import com.meutudo.bank.exceptions.base.BusinessException;

public class NumberOfCashPurchasesMustBeGreaterThanZeroException extends BusinessException {
    public NumberOfCashPurchasesMustBeGreaterThanZeroException() {
        super();
    }

    public NumberOfCashPurchasesMustBeGreaterThanZeroException(String message) {
        super(message);
    }

    public NumberOfCashPurchasesMustBeGreaterThanZeroException(String message, Throwable cause) {
        super(message, cause);
    }

    public NumberOfCashPurchasesMustBeGreaterThanZeroException(Throwable cause) {
        super(cause);
    }
}
