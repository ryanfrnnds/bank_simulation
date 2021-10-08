package com.meutudo.bank.service;

import com.meutudo.bank.dto.TransferDto;
import com.meutudo.bank.enums.TransferResultEnum;
import com.meutudo.bank.model.Account;
import com.meutudo.bank.model.Transfer;
import com.meutudo.bank.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TransferService {

    @Autowired
    AccountService accountService;

    @Autowired
    TransferRepository transferRepository;


    @Transactional
    public Transfer create(TransferDto params) throws RuntimeException {
        Transfer transfer = params.convert();

        if(!validate(transfer)) {
            return transfer;
        }
        generate(params, transfer);

        return transfer;
    }

    private boolean validate(Transfer transfer) {
        boolean isNotGreaterThanZero = !(transfer.getValue().compareTo(Double.valueOf(0)) > 0);
        if(isNotGreaterThanZero){
            transfer.setResult(TransferResultEnum.VALUE_MUST_BE_GREATER_THAN_ZERO);
            return false;
        }
        boolean isOriginNotFound = !accountService.checkFound(transfer.getOrigin().getAgency(), transfer.getOrigin().getNumber(),transfer.getOrigin().getDigit());
        if(isOriginNotFound){
            transfer.setResult(TransferResultEnum.ORIGIN_NOT_FOUND);
            return false;
        }
        boolean isDestinationNotFound = !accountService.checkFound(transfer.getDestination().getAgency(), transfer.getDestination().getNumber(),transfer.getDestination().getDigit());
        if(isDestinationNotFound){
            transfer.setResult(TransferResultEnum.DESTINATION_NOT_FOUND);
            return false;
        }
        if(!hasBalanceToTransfer(transfer)){
            transfer.setResult(TransferResultEnum.INSUFFICIENT_FUNDS);
            transfer.getOrigin().setBalance(accountService.getBalance(transfer.getOrigin().getAgency(), transfer.getOrigin().getNumber(),transfer.getOrigin().getDigit()).get());
            return false;
        }
        transfer.setResult(TransferResultEnum.CREATED);
        return true;
    }

    private boolean hasBalanceToTransfer(Transfer params) {
        Double balance = accountService.getBalance(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit()).get();
        return balance.compareTo(params.getValue()) == 0 || balance.compareTo(params.getValue()) > 0;
    }

    private void generate(TransferDto params, Transfer transfer) {
        Account origin = accountService.get(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit()).get();
        Account destination = accountService.get(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit()).get();
        transfer.setOrigin(origin);
        transfer.setDestination(destination);

        transfer.generate();

        transfer = transferRepository.save(transfer);
        transfer.setResult(TransferResultEnum.CREATED);
    }
}
