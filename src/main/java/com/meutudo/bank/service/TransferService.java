package com.meutudo.bank.service;

import com.meutudo.bank.dto.TransferDto;
import com.meutudo.bank.dto.TransferFutureDto;
import com.meutudo.bank.enums.TransferResultEnum;
import com.meutudo.bank.model.Account;
import com.meutudo.bank.model.Transfer;
import com.meutudo.bank.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;


@Service
public class TransferService {

    @Autowired
    AccountService accountService;

    @Autowired
    TransferRepository transferRepository;


    @Transactional
    public Transfer create(TransferDto params) throws RuntimeException {
        Transfer transfer = params.convert();
        transfer.setRevert(false);

        if(!validate(transfer)) {
            return transfer;
        }

        Account origin = accountService.get(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit()).get();
        Account destination = accountService.get(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit()).get();

        buildTransfer(transfer,origin,destination);

        transfer = transferRepository.save(transfer);
        transfer.setResult(TransferResultEnum.CREATED);
        return transfer;
    }

    @Transactional
    public Transfer revert(Long id) {
        Optional<Transfer> optTransfer = transferRepository.findById(id);
        if(optTransfer.isEmpty()) {
            Transfer empty = new Transfer();
            empty.setId(id);
            empty.setResult(TransferResultEnum.NOT_FOUND);
            return empty;
        }

        Transfer transferRevert = optTransfer.get();

        if(Optional.ofNullable(transferRevert.getRevertTransferId()).isPresent()) {
            Transfer isRevertTransfer = new Transfer();
            isRevertTransfer.setId(id);
            isRevertTransfer.setRevertTransferId(transferRevert.getRevertTransferId());
            isRevertTransfer.setResult(TransferResultEnum.IS_REVERT);
            return isRevertTransfer;
        }

        Transfer newTransfer = buildNewTransferForRevert(transferRevert);

        if(!validate(newTransfer)) return newTransfer;

        newTransfer = transferRepository.save(newTransfer);

        transferRevert.setRevert(true);
        transferRevert.setRevertTransferId(newTransfer.getId());
        transferRevert.setResult(TransferResultEnum.CREATED);
        transferRepository.save(transferRevert);

        return transferRevert;
    }

    @Transactional
    public Transfer future(TransferFutureDto params) {
        Transfer transfer = params.convert();

        if(!validateFuture(transfer)) return transfer;

        double totalValueCashPurchases = 0;

        Double truncatedValue = BigDecimal.valueOf(params.getValue()/params.getQuantityCachePurchase())
                .setScale(2, RoundingMode.DOWN)
                .doubleValue();
        LocalDate dateCashPurchases = params.getDate();

        Account origin = accountService.get(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit()).get();
        Account destination = accountService.get(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit()).get();



        for (int cashPurchases = 0; cashPurchases < transfer.getQuantityCashPurcahses(); cashPurchases++) {
            Transfer transferFuture = params.convert();
            boolean isLastCashPurchases = cashPurchases == (params.getQuantityCachePurchase() - 1);
            boolean isFirstCashPurchases = cashPurchases == 0;
            double valueCashPurchases = truncatedValue;
            dateCashPurchases = isFirstCashPurchases ? dateCashPurchases : dateCashPurchases.plusMonths(1);

            if(isLastCashPurchases) {
                valueCashPurchases = params.getValue() - totalValueCashPurchases;
                transfer.setValueLastCashPurchases(valueCashPurchases);
            }
            totalValueCashPurchases += valueCashPurchases;

            transferFuture.setValue(valueCashPurchases);
            transferFuture.setDate(dateCashPurchases);
            buildTransfer(transferFuture, origin, destination);

            transferRepository.save(transferFuture);
        }
        transfer.setResult(TransferResultEnum.CREATED);
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

    private boolean validateFuture(Transfer transfer) {
        if(transfer.getQuantityCashPurcahses() <= 0) {
            transfer.setResult(TransferResultEnum.NUMBER_OF_CASH_PURCHASES_MUST_BE_GREATER_THAN_ZERO);
            return false;
        }
        boolean isMustBeLessThanOne = Optional.ofNullable(transfer.getValue()).isEmpty() || transfer.getValue().doubleValue() < 1;
        if(isMustBeLessThanOne){
            transfer.setResult(TransferResultEnum.VALUE_MUST_BE_LESS_THAN_ONE);
            return false;
        }

        boolean isDateMustBeFromTheNexDay = !transfer.getDate().isAfter(LocalDate.now());
        if(isDateMustBeFromTheNexDay){
            transfer.setResult(TransferResultEnum.FUTURE_TRANSFER_DATE_MUST_BE_FROM_THE_NEX_DAY);
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
        transfer.setResult(TransferResultEnum.CREATED);
        return true;
    }

    private boolean hasBalanceToTransfer(Transfer params) {
        Double balance = accountService.getBalance(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit()).get();
        return balance.compareTo(params.getValue()) == 0 || balance.compareTo(params.getValue()) > 0;
    }

    private void buildTransfer(Transfer transfer, Account origin, Account destination) {
        transfer.setOrigin(origin);
        transfer.setDestination(destination);
        transfer.getOrigin().setBalance(transfer.getOrigin().getBalance() - transfer.getValue());
        transfer.getDestination().setBalance(transfer.getDestination().getBalance() + transfer.getValue());
    }

    private Transfer buildNewTransferForRevert(Transfer revert) {
        Transfer newTransferForRevert = new Transfer();
        newTransferForRevert.setDate(LocalDate.now());
        newTransferForRevert.setValue(revert.getValue());
        buildTransfer(newTransferForRevert, revert.getDestination(), revert.getOrigin());
        return newTransferForRevert;
    }
}
