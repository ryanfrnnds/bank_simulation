package com.meutudo.bank.service;

import com.meutudo.bank.dto.TransferDto;
import com.meutudo.bank.dto.TransferFutureDto;
import com.meutudo.bank.enums.TransferResultEnum;
import com.meutudo.bank.exceptions.*;
import com.meutudo.bank.model.Account;
import com.meutudo.bank.model.Transfer;
import com.meutudo.bank.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
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

        validate(transfer);

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
            throw new NotFoundException("Transferência não encontrada");
        }

        Transfer transferRevert = optTransfer.get();

        if(Optional.ofNullable(transferRevert.getRevertTransferId()).isPresent()) {
            throw new RevertException("Transferência já foi anulada.");
        }

        Transfer newTransfer = buildNewTransferForRevert(transferRevert);

        validate(newTransfer);

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

        validateFuture(transfer);

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

    private void validate(Transfer transfer) {
        boolean isNotGreaterThanZero = !(transfer.getValue().compareTo(Double.valueOf(0)) > 0);
        if(isNotGreaterThanZero){
            throw new ValueMustbeGreaterThanZeroException("Valor da transferência , quando futura, não deve ser menor que 1(um)");
        }
        boolean isOriginNotFound = !accountService.checkFound(transfer.getOrigin().getAgency(), transfer.getOrigin().getNumber(),transfer.getOrigin().getDigit());
        if(isOriginNotFound){
            throw new OriginNotFoundException(MessageFormat.format("Conta de ORIGEM não encontrada. Agência: {0}, Conta: {1}", transfer.getOrigin().getAgency(), transfer.getOrigin().getNumber().concat("-").concat(transfer.getOrigin().getDigit())));
        }
        boolean isDestinationNotFound = !accountService.checkFound(transfer.getDestination().getAgency(), transfer.getDestination().getNumber(),transfer.getDestination().getDigit());
        if(isDestinationNotFound){
            throw new DestinationNotFoundException(MessageFormat.format("Conta de DESTINO não encontrada. Agência: {0}, Conta: {1}", transfer.getDestination().getAgency(), transfer.getDestination().getNumber().concat("-").concat(transfer.getDestination().getDigit())));
        }
        if(!hasBalanceToTransfer(transfer)){
            double balance = accountService.getBalance(transfer.getOrigin().getAgency(), transfer.getOrigin().getNumber(),transfer.getOrigin().getDigit());
            throw new InsufficientFoundsException(MessageFormat.format("Saldo insuficiente. Saldo: {0}, valor da transferência: {1}", balance, transfer.getValue()));
        }
    }

    private void validateFuture(Transfer transfer) {
        if(transfer.getQuantityCashPurcahses() <= 0) {
            throw new NumberOfCashPurchasesMustBeGreaterThanZeroException("Numero de parcelas deve ser maior que zero.");
        }
        boolean isMustBeLessThanOne = Optional.ofNullable(transfer.getValue()).isEmpty() || transfer.getValue().doubleValue() < 1;
        if(isMustBeLessThanOne){
            throw new FutureValueMustBeLessThanOneException("Valor da transferência , quando futura, não deve ser menor que 1(um)");
        }

        boolean isDateMustBeFromTheNexDay = !transfer.getDate().isAfter(LocalDate.now());
        if(isDateMustBeFromTheNexDay){
            throw new FutureDateMustBeFromTheNextDayException("Data da transferência futura deve ser apartir do próximo dia");
        }

        boolean isOriginNotFound = !accountService.checkFound(transfer.getOrigin().getAgency(), transfer.getOrigin().getNumber(),transfer.getOrigin().getDigit());
        if(isOriginNotFound){
            throw new OriginNotFoundException(MessageFormat.format("Conta de ORIGEM não encontrada. Agência: {0}, Conta: {1}", transfer.getOrigin().getAgency(), transfer.getOrigin().getNumber().concat("-").concat(transfer.getOrigin().getDigit())));
        }
        boolean isDestinationNotFound = !accountService.checkFound(transfer.getDestination().getAgency(), transfer.getDestination().getNumber(),transfer.getDestination().getDigit());
        if(isDestinationNotFound){
            throw new DestinationNotFoundException(MessageFormat.format("Conta de DESTINO não encontrada. Agência: {0}, Conta: {1}", transfer.getDestination().getAgency(), transfer.getDestination().getNumber().concat("-").concat(transfer.getDestination().getDigit())));
        }
    }

    private boolean hasBalanceToTransfer(Transfer params) {
        Double balance = accountService.getBalance(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit());
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
