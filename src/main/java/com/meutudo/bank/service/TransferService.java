package com.meutudo.bank.service;

import com.meutudo.bank.dto.TransferDto;
import com.meutudo.bank.dto.TransferFutureDto;
import com.meutudo.bank.exceptions.NotFoundException;
import com.meutudo.bank.helpers.TransferHelper;
import com.meutudo.bank.helpers.TransferValidateHelper;
import com.meutudo.bank.model.Account;
import com.meutudo.bank.model.Transfer;
import com.meutudo.bank.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class TransferService {

    @Autowired
    AccountService accountService;

    @Autowired
    TransferRepository transferRepository;


    @Transactional
    public Transfer create(TransferDto params) throws RuntimeException {
        TransferValidateHelper.validateDefault(params, accountService);
        Account origin = accountService.get(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit()).get();
        Account destination = accountService.get(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit()).get();
        Transfer transfer = TransferHelper.createDefault(origin, destination, params.getValue());

        transfer = transferRepository.save(transfer);
        return transfer;
    }

    @Transactional
    public Transfer revert(Long id) {
        Optional<Transfer> optTransfer = transferRepository.findById(id);
        if(optTransfer.isEmpty()) {
            throw new NotFoundException("Transferência não encontrada");
        }
        Transfer insertTransferForRevert = optTransfer.get().revert();
        TransferValidateHelper.validateRevert(insertTransferForRevert.getOrigin(), insertTransferForRevert.getDestination(), id, insertTransferForRevert.getValue(), accountService,transferRepository);
        return transferRepository.save(insertTransferForRevert);
    }

    @Transactional
    public List<Transfer> future(TransferFutureDto params) {
        TransferValidateHelper.validateFuture(params, accountService);
        List transfers = new ArrayList<Transfer>();
        Account origin = accountService.get(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit()).get();
        Account destination = accountService.get(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit()).get();

        BigDecimal totalValueCashPurchases = BigDecimal.ZERO;

        BigDecimal truncatedValue = params.getValue().divide(BigDecimal.valueOf(params.getQuantityCachePurchase()),2, RoundingMode.DOWN);
        LocalDate dateCashPurchases = params.getDate();

        for (int cashPurchases = 0; cashPurchases < params.getQuantityCachePurchase(); cashPurchases++) {
            boolean isLastCashPurchases = cashPurchases == (params.getQuantityCachePurchase() - 1);
            boolean isFirstCashPurchases = cashPurchases == 0;

            BigDecimal valueCashPurchases =  isLastCashPurchases ?  params.getValue().subtract(totalValueCashPurchases) : truncatedValue;
            dateCashPurchases = isFirstCashPurchases ? dateCashPurchases : dateCashPurchases.plusMonths(1);

            Transfer transferFuture = TransferHelper.createFuture(origin, destination, valueCashPurchases, dateCashPurchases);
            transferFuture = transferRepository.save(transferFuture);
            transfers.add(transferFuture);

            totalValueCashPurchases = totalValueCashPurchases.add(valueCashPurchases);

        }
        return transfers;
    }



}
