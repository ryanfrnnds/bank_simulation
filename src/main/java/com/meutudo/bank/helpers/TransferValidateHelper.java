package com.meutudo.bank.helpers;

import com.meutudo.bank.enums.TransferTypeEnum;
import com.meutudo.bank.model.Account;
import com.meutudo.bank.model.Transfer;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransferHelper {
    public static Transfer createDefault(Account origin, Account destination, double value) {
        Transfer transfer = create(origin,destination,value);
        transfer.setType(TransferTypeEnum.DEFAULT);
        return transfer;
    }

    public static Transfer createFuture(Account origin, Account destination, BigDecimal value, LocalDate dateCashPurchases) {
        Transfer transfer = create(origin,destination,value.doubleValue());
        transfer.setType(TransferTypeEnum.FUTURE);
        transfer.setDate(dateCashPurchases);
        return transfer;
    }

    private static Transfer create(Account origin, Account destination, double value) {
        Transfer transfer = new Transfer();
        transfer.setOrigin(origin);
        transfer.setDestination(destination);
        transfer.setValue(value);
        transfer.setRevert(false);
        return transfer;
    }
}
