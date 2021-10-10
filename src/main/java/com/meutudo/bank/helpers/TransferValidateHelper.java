package com.meutudo.bank.helpers;

import com.meutudo.bank.dto.TransferDto;
import com.meutudo.bank.dto.TransferFutureDto;
import com.meutudo.bank.exceptions.*;
import com.meutudo.bank.model.Account;
import com.meutudo.bank.repository.TransferRepository;
import com.meutudo.bank.service.AccountService;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Optional;

public class TransferValidateHelper {
    public static void validateDefault(TransferDto transfer, AccountService accountService) {
        boolean isNotGreaterThanZero = !(transfer.getValue().compareTo(BigDecimal.ZERO) > 0);
        if(isNotGreaterThanZero){
            throw new ValueMustbeGreaterThanZeroException("Valor da transferência deve ser maior que zero");
        }
        boolean isOriginNotFound = !accountService.checkFound(transfer.getOrigin().getAgency(), transfer.getOrigin().getNumber(),transfer.getOrigin().getDigit());
        if(isOriginNotFound){
            throw new OriginNotFoundException(MessageFormat.format("Conta de ORIGEM não encontrada. Agência: {0}, Conta: {1}", transfer.getOrigin().getAgency(), transfer.getOrigin().getNumber().concat("-").concat(transfer.getOrigin().getDigit())));
        }
        boolean isDestinationNotFound = !accountService.checkFound(transfer.getDestination().getAgency(), transfer.getDestination().getNumber(),transfer.getDestination().getDigit());
        if(isDestinationNotFound){
            throw new DestinationNotFoundException(MessageFormat.format("Conta de DESTINO não encontrada. Agência: {0}, Conta: {1}", transfer.getDestination().getAgency(), transfer.getDestination().getNumber().concat("-").concat(transfer.getDestination().getDigit())));
        }
        if(!hasBalanceToTransfer(transfer.getOrigin().getAgency(), transfer.getOrigin().getNumber(), transfer.getOrigin().getDigit(), transfer.getValue(), accountService)){
            BigDecimal balance = accountService.getBalance(transfer.getOrigin().getAgency(), transfer.getOrigin().getNumber(),transfer.getOrigin().getDigit());
            throw new InsufficientFoundsException(MessageFormat.format("Saldo insuficiente. Saldo: {0}, valor da transferência: {1}", balance, transfer.getValue()));
        }
    }

    public static void validateFuture(TransferFutureDto transfer, AccountService accountService) {
        if(transfer.getQuantityCachePurchase() <= 0) {
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

    public static void validateRevert(Account origin, Account destination, Long idTransferRevert, BigDecimal value , AccountService accountService, TransferRepository transferRepository) {
        if(transferRepository.findByRevertTransferId(idTransferRevert).isPresent()) {
            throw new RevertException("Transferência já foi anulada.");
        }
        boolean isOriginNotFound = !accountService.checkFound(origin.getAgency(), origin.getNumber(),origin.getDigit());
        if(isOriginNotFound){
            throw new OriginNotFoundException(MessageFormat.format("Conta de ORIGEM não encontrada. Agência: {0}, Conta: {1}", origin.getAgency(), origin.getNumber().concat("-").concat(origin.getDigit())));
        }
        boolean isDestinationNotFound = !accountService.checkFound(destination.getAgency(), destination.getNumber(),destination.getDigit());
        if(isDestinationNotFound){
            throw new DestinationNotFoundException(MessageFormat.format("Conta de DESTINO não encontrada. Agência: {0}, Conta: {1}", destination.getAgency(), destination.getNumber().concat("-").concat(destination.getDigit())));
        }
        if(!hasBalanceToTransfer(origin.getAgency(), origin.getNumber(), origin.getDigit(), value, accountService)){
            BigDecimal balance = accountService.getBalance(origin.getAgency(), origin.getNumber(),origin.getDigit());
            throw new InsufficientFoundsException(MessageFormat.format("Saldo insuficiente. Saldo: {0}, valor da transferência: {1}", balance, value));
        }
    }


    private static boolean hasBalanceToTransfer(String agency, String number, String digit, BigDecimal value , AccountService accountService) {
        BigDecimal balance = accountService.getBalance(agency, number,digit);
        return balance.compareTo(value) == 0 || balance.compareTo(value) > 0;
    }
}
