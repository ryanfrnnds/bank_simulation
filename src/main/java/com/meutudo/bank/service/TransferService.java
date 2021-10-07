package com.meutudo.bank.service;

import com.meutudo.bank.enums.TransferResultEnum;
import com.meutudo.bank.model.Transfer;
import com.meutudo.bank.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferService {

    @Autowired
    AccountService accountService;

    @Autowired
    TransferRepository transferRepository;


    public Transfer generate(Transfer params){
        boolean isNotGreaterThanZero = !(params.getValue().compareTo(Double.valueOf(0)) > 0);
        if(isNotGreaterThanZero){
            params.setResult(TransferResultEnum.VALUE_MUST_BE_GREATER_THAN_ZERO);
            return params;
        }
        boolean isOriginNotFound = !accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit());
        if(isOriginNotFound){
            params.setResult(TransferResultEnum.ORIGIN_NOT_FOUND);
            return params;
        }
        boolean isDestinationNotFound = !accountService.checkFound(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit());
        if(isDestinationNotFound){
            params.setResult(TransferResultEnum.DESTINATION_NOT_FOUND);
            return params;
        }
        if(!hasBalanceToTransfer(params)){
            params.setResult(TransferResultEnum.INSUFFICIENT_FUNDS);
            return params;
        }
        //TODO - Realizar Logica para SUCESSO!!!
        params.setResult(TransferResultEnum.OK);
        return params;
    }

    private boolean hasBalanceToTransfer(Transfer params) {
       Double balance = accountService.getBalance(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit()).get();
       return balance.compareTo(params.getValue()) == 0 || balance.compareTo(params.getValue()) > 0;
    }
}
