package com.meutudo.bank.helpers;

import com.meutudo.bank.enums.TransferTypeEnum;
import com.meutudo.bank.model.Account;
import com.meutudo.bank.model.Transfer;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransferHelper {

    public static Transfer createDefault(Account origin, Account destination, BigDecimal value) {
        Transfer transfer = new Transfer();

        //---- Update Accounts!
        origin.setBalance(origin.getBalance().subtract(value));
        transfer.setOrigin(origin);

        destination.setBalance(destination.getBalance().add(value));
        transfer.setDestination(destination);
        // ------ FIM
        transfer.setValue(value);
        transfer.setType(TransferTypeEnum.DEFAULT);
        transfer.setDate(LocalDate.now());
        return transfer;
    }

    //No Update Accounts!
    public static Transfer createFuture(Account origin, Account destination, BigDecimal value, LocalDate dateCashPurchases) {
        Transfer transfer = new Transfer();
        transfer.setOrigin(origin);
        transfer.setDestination(destination);
        transfer.setType(TransferTypeEnum.FUTURE);
        transfer.setDate(dateCashPurchases);
        transfer.setValue(value);
        return transfer;
    }
}
