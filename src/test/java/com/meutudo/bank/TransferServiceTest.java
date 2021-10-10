package com.meutudo.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meutudo.bank.dto.AccountDto;
import com.meutudo.bank.dto.TransferDto;
import com.meutudo.bank.dto.TransferFutureDto;
import com.meutudo.bank.enums.TransferTypeEnum;
import com.meutudo.bank.exceptions.*;
import com.meutudo.bank.helpers.TransferHelper;
import com.meutudo.bank.model.Account;
import com.meutudo.bank.model.Transfer;
import com.meutudo.bank.repository.TransferRepository;
import com.meutudo.bank.service.AccountService;
import com.meutudo.bank.service.TransferService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransferServiceTest {

    @TestConfiguration
    static class TransferServiceTestConfiguration {

        @Bean
        public TransferService transferService() {
            return new TransferService();
        }
    }

    @Autowired
    TransferService transferService;

    @MockBean
    AccountService accountService;

    @MockBean
    TransferRepository transferRepository;


    private Account origin;
    private Account destination;
    private Transfer transfer;

    @Before
    public void setUp() {
        origin = new Account("4421", "01520446", "9", BigDecimal.valueOf(500.50));
        destination = new Account("5817", "82516", "8", BigDecimal.valueOf(1000));
        transfer = new Transfer(origin, destination, LocalDate.now(), BigDecimal.valueOf(100));
    }

    @Test(expected = InsufficientFoundsException.class)
    public void shouldNotAccomplishTransferWithInsufficientBalance() {
        Mockito.when(accountService.checkFound(Mockito.any(String.class),Mockito.any(String.class),Mockito.any(String.class))).thenReturn(true);
        Mockito.when(accountService.getBalance(Mockito.any(String.class),Mockito.any(String.class),Mockito.any(String.class))).thenReturn(BigDecimal.valueOf(0.1));

        transferService.create(convertDto(transfer));

        assertAll(
                () -> verify(accountService, never()).get(Mockito.any(String.class),Mockito.any(String.class),Mockito.any(String.class)),
                () -> verify(transferRepository, never()).save(Mockito.any(Transfer.class))
        );
    }

    @Test(expected = ValueMustbeGreaterThanZeroException.class)
    public void shouldNotAccomplishTransfersWithAValueOfZeroOrLess() {
        transfer.setValue(BigDecimal.ZERO);

        transferService.create(convertDto(transfer));

        assertAll(
                () -> verify(accountService, never()).get(Mockito.any(String.class),Mockito.any(String.class),Mockito.any(String.class)),
                () -> verify(transferRepository, never()).save(Mockito.any(Transfer.class))
        );
    }

    @Test(expected = OriginNotFoundException.class)
    public void shouldNotAccomplishTransferWhenOriginNotFound() {

        Mockito.when(accountService.checkFound(origin.getAgency(), origin.getNumber(),origin.getDigit())).thenReturn(false);

        transferService.create(convertDto(transfer));

        assertAll(
                () -> verify(accountService, never()).get(Mockito.any(String.class),Mockito.any(String.class),Mockito.any(String.class)),
                () -> verify(transferRepository, never()).save(Mockito.any(Transfer.class))
        );
    }

    @Test(expected = DestinationNotFoundException.class)
    public void shouldNotAccomplishTransferWhenDestinationNotFound() {
        Mockito.when(accountService.checkFound(origin.getAgency(), origin.getNumber(),origin.getDigit())).thenReturn(false);

        Mockito.when(accountService.checkFound(origin.getAgency(), origin.getNumber(),origin.getDigit())).thenReturn(true);
        Mockito.when(accountService.checkFound(destination.getAgency(), destination.getNumber(),destination.getDigit())).thenReturn(false);

        transferService.create(convertDto(transfer));

        assertAll(
                () -> verify(accountService, never()).get(Mockito.any(String.class),Mockito.any(String.class),Mockito.any(String.class)),
                () -> verify(transferRepository, never()).save(Mockito.any(Transfer.class))
        );
    }

    @Test
    public void shouldAccomplishTransfer() {
        BigDecimal initBalanceOrigin = origin.getBalance();
        BigDecimal initBalanceDistination = destination.getBalance();
        BigDecimal valueTransfer = transfer.getValue();

        Mockito.when(accountService.checkFound(Mockito.any(String.class),Mockito.any(String.class),Mockito.any(String.class))).thenReturn(true);

        Mockito.when(accountService.getBalance(origin.getAgency(),origin.getNumber(),origin.getDigit())).thenReturn(origin.getBalance());

        Mockito.when(accountService.get(origin.getAgency(), origin.getNumber(),origin.getDigit())).thenReturn(Optional.of(origin));
        Mockito.when(accountService.get(destination.getAgency(), destination.getNumber(),destination.getDigit())).thenReturn(Optional.of(destination));

        Mockito.when(transferRepository.save(Mockito.any(Transfer.class))).thenReturn(transfer);

        Transfer result = transferService.create(convertDto(transfer));

        assertAll(
                () -> verify(transferRepository, times(1)).save(Mockito.any(Transfer.class)),
                () ->  Assertions.assertEquals(initBalanceOrigin.subtract(transfer.getValue()), result.getOrigin().getBalance() ),
                () -> Assertions.assertEquals(initBalanceDistination.add(transfer.getValue()), result.getDestination().getBalance()),
                () -> Assertions.assertEquals(valueTransfer, result.getValue()),
                () -> Assertions.assertEquals(TransferTypeEnum.DEFAULT, result.getType()),
                () -> Assertions.assertEquals(LocalDate.now(), result.getDate())
        );
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotRevertTransferWhenNotFound() {
        Long id = 1L;
        Mockito.when(transferRepository.findById(id)).thenReturn(Optional.empty());
        transferService.revert(id);

        verify(transferRepository, never()).save(Mockito.any(Transfer.class));
    }

    @Test(expected = RevertException.class)
    public void shouldNotRevertTransferWhenTransferIsRevert() throws JsonProcessingException {
        transfer.setRevertTransferId(1L);

        Mockito.when(transferRepository.findById(transfer.getId())).thenReturn(Optional.of(transfer));

        transferService.revert(transfer.getId());

        verify(transferRepository, never()).save(transfer);
    }

    @Test(expected = InsufficientFoundsException.class)
    public void shouldNotRevertTransferWhenNotBalance() {

        Mockito.when(transferRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(transfer));
        Mockito.when(accountService.checkFound(Mockito.any(String.class),Mockito.any(String.class),Mockito.any(String.class))).thenReturn(true);
        Mockito.when(accountService.getBalance(origin.getAgency(),origin.getNumber(),origin.getDigit())).thenReturn(BigDecimal.ZERO);

        transferService.revert(Mockito.anyLong());

        verify(transferRepository, never()).save(Mockito.any(Transfer.class));
    }

    @Test
    public void shouldRevertTransfer() {
        transfer.setId(1L);
        Mockito.when(transferRepository.findById(transfer.getId())).thenReturn(Optional.of(transfer));
        Mockito.when(accountService.checkFound(Mockito.any(String.class),Mockito.any(String.class),Mockito.any(String.class))).thenReturn(true);
        Mockito.when(accountService.getBalance(origin.getAgency(),origin.getNumber(),origin.getDigit())).thenReturn(transfer.getOrigin().getBalance());

        Mockito.when(transferRepository.findById(transfer.getId())).thenReturn(Optional.of(transfer));

        Mockito.when(transferRepository.save(transfer.revert())).thenReturn(transfer.revert());

        Transfer result = transferService.revert(transfer.getId());

        assertAll(
                () -> verify(transferRepository, times(1)).save(Mockito.any(Transfer.class)),
                () -> Assertions.assertEquals(TransferTypeEnum.REVERT,result.getType()),
                () -> Assertions.assertNotNull(result.getRevertTransferId())
        );
    }


    @Test(expected = NumberOfCashPurchasesMustBeGreaterThanZeroException.class)
    public void shouldNotAccomplishFutureTransferWhenQuantityCahsPurchasesIsLessThanOrEqualToZero() {
        int quantityCashPurchases = 0;
        BigDecimal value = BigDecimal.valueOf(100);
        LocalDate date = LocalDate.now();
        TransferFutureDto params = buildFuture(value, date, quantityCashPurchases);

        transferService.future(params);
        verify(transferRepository, never()).save(Mockito.any(Transfer.class));
    }

    @Test(expected = FutureValueMustBeLessThanOneException.class)
    public void shouldNotAccomplishFutureTransferWhenValueLessThanToOne() {
        int quantityCashPurchases = 2;
        BigDecimal value = BigDecimal.valueOf(0);
        LocalDate date = LocalDate.now();
        TransferFutureDto params = buildFuture(value, date, quantityCashPurchases);

        transferService.future(params);
        verify(transferRepository, never()).save(Mockito.any(Transfer.class));
    }

    @Test(expected = OriginNotFoundException.class)
    public void shouldNotAccomplishFutureTransferWhenOriginNotFound() {
        int quantityCashPurchases = 2;
        BigDecimal value = BigDecimal.valueOf(10);
        LocalDate date = LocalDate.now().plusDays(1);
        TransferFutureDto params = buildFuture(value, date, quantityCashPurchases);

        Mockito.when(accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(false);

        transferService.future(params);
        verify(transferRepository, never()).save(Mockito.any(Transfer.class));
    }

    @Test(expected = DestinationNotFoundException.class)
    public void shouldNotAccomplishFutureTransferWhenDestinyNotFound() {
        int quantityCashPurchases = 2;
        BigDecimal value = BigDecimal.valueOf(10);
        LocalDate date = LocalDate.now().plusDays(1);
        TransferFutureDto params = buildFuture(value, date, quantityCashPurchases);

        Mockito.when(accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(true);
        Mockito.when(accountService.checkFound(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getOrigin().getDigit())).thenReturn(false);

        transferService.future(params);
        verify(transferRepository, never()).save(Mockito.any(Transfer.class));
    }

    @Test(expected = FutureDateMustBeFromTheNextDayException.class)
    public void shouldNotAccomplishFutureTransferWhenNotForTheNextDay() {
        int quantityCashPurchases = 11;
        BigDecimal value = BigDecimal.valueOf(101);
        LocalDate date = LocalDate.now();
        TransferFutureDto params = buildFuture(value, date, quantityCashPurchases);

        Mockito.when(accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(true);
        Mockito.when(accountService.checkFound(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit())).thenReturn(true);

        transferService.future(params);

        verify(transferRepository, never()).save(Mockito.any(Transfer.class));
    }

    @Test
    public void shouldAccomplishFutureTransferWhenTheResultOfTheDivisionDoesNotGeneratePeriodicTenth() {
        List<Transfer> transfers = new ArrayList<Transfer>();
        int quantityCashPurchases = 4;
        BigDecimal value = BigDecimal.valueOf(12);
        LocalDate date = LocalDate.now().plusDays(1);
        TransferFutureDto params = buildFuture(value, date, quantityCashPurchases);
        BigDecimal totalValueCashPurchases = BigDecimal.ZERO;
        BigDecimal truncatedValue = value.divide(BigDecimal.valueOf(quantityCashPurchases)).setScale(2, RoundingMode.DOWN);

        LocalDate dateCashPurchases = params.getDate();
        for (int cashPurchases = 0; cashPurchases < params.getQuantityCachePurchase(); cashPurchases++) {
            boolean isLastCashPurchases = cashPurchases == (params.getQuantityCachePurchase() - 1);
            boolean isFirstCashPurchases = cashPurchases == 0;

            BigDecimal valueCashPurchases =  isLastCashPurchases ?  params.getValue().subtract(totalValueCashPurchases) : truncatedValue;
            dateCashPurchases = isFirstCashPurchases ? dateCashPurchases : dateCashPurchases.plusMonths(1);

            Transfer transferFuture = TransferHelper.createFuture(origin, destination, valueCashPurchases, dateCashPurchases);

            Mockito.when(transferRepository.save(transferFuture)).thenReturn(transferFuture);

            transfers.add(transferFuture);
            totalValueCashPurchases = totalValueCashPurchases.add(valueCashPurchases);
        }

        Mockito.when(accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(true);
        Mockito.when(accountService.checkFound(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit())).thenReturn(true);

        Mockito.when(accountService.get(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(Optional.of(origin));
        Mockito.when(accountService.get(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit())).thenReturn(Optional.of(destination));

        List<Transfer> results = transferService.future(params);

        verify(transferRepository, times(quantityCashPurchases)).save(Mockito.any(Transfer.class));
        Assertions.assertNull(results.stream()
                .filter(transfer -> transfer.getValue().compareTo(truncatedValue) != 0)
                .findAny()
                .orElse(null));
        Assertions.assertNull(results.stream()
                .filter(transfer -> transfer.getType() != TransferTypeEnum.FUTURE)
                .findAny()
                .orElse(null));
    }

    @Test
    public void shouldAccomplishFutureTransferWhenTheResultOfTheDivisionGeneratesAPeriodicDecimalPlacingTheDifferenceInTheLastCashPurchases() {
        List<Transfer> transfers = new ArrayList<Transfer>();
        int quantityCashPurchases = 3;
        BigDecimal value = BigDecimal.valueOf(10.00);
        BigDecimal valueLastCashPurchases = BigDecimal.ZERO;
        LocalDate date = LocalDate.now().plusDays(1);
        TransferFutureDto params = buildFuture(value, date, quantityCashPurchases);
        BigDecimal totalValueCashPurchases = BigDecimal.ZERO;
        BigDecimal truncatedValue = value.divide(BigDecimal.valueOf(quantityCashPurchases),2, RoundingMode.DOWN);

        LocalDate dateCashPurchases = params.getDate();
        for (int cashPurchases = 0; cashPurchases < params.getQuantityCachePurchase(); cashPurchases++) {
            boolean isLastCashPurchases = cashPurchases == (params.getQuantityCachePurchase() - 1);
            boolean isFirstCashPurchases = cashPurchases == 0;

            BigDecimal valueCashPurchases =  isLastCashPurchases ?  params.getValue().subtract(totalValueCashPurchases) : truncatedValue;
            valueLastCashPurchases = valueCashPurchases;
            dateCashPurchases = isFirstCashPurchases ? dateCashPurchases : dateCashPurchases.plusMonths(1);

            Transfer transferFuture = TransferHelper.createFuture(origin, destination, valueCashPurchases, dateCashPurchases);

            Mockito.when(transferRepository.save(transferFuture)).thenReturn(transferFuture);

            transfers.add(transferFuture);
            totalValueCashPurchases = totalValueCashPurchases.add(valueCashPurchases);
        }

        Mockito.when(accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(true);
        Mockito.when(accountService.checkFound(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit())).thenReturn(true);

        Mockito.when(accountService.get(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(Optional.of(origin));
        Mockito.when(accountService.get(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit())).thenReturn(Optional.of(destination));

        List<Transfer> results = transferService.future(params);
        Transfer lastElement = results.stream().skip(results.size() - 1).findFirst().get();

        verify(transferRepository, times(quantityCashPurchases)).save(Mockito.any(Transfer.class));
        Assertions.assertEquals(valueLastCashPurchases, lastElement.getValue());
        Assertions.assertNull(results.stream()
                .filter(transfer -> transfer.getType() != TransferTypeEnum.FUTURE)
                .findAny()
                .orElse(null));
    }

    private Transfer build(BigDecimal value, LocalDate date, BigDecimal balanceOrigin, BigDecimal balanceDestination) {
        Account origin = new Account("4421", "01520446", "9", balanceOrigin);
        Account destination = new Account("5817", "82516", "8", balanceDestination);
        return new Transfer(origin,destination, date,value);
    }

    private TransferFutureDto buildFuture(BigDecimal value, LocalDate date, int quantityCashPurchase) {

        AccountDto origin = new AccountDto("4421", "01520446", "9");
        AccountDto destination = new AccountDto("5817", "82516", "8");
        return new TransferFutureDto(value,origin,destination, false, date,quantityCashPurchase);
    }

    private TransferDto convertDto(Transfer transfer) {
        TransferDto dto = new TransferDto();
        dto.setOrigin(new AccountDto(transfer.getOrigin().getAgency(),transfer.getOrigin().getNumber(),transfer.getOrigin().getDigit()));
        dto.setDestination(new AccountDto(transfer.getDestination().getAgency(),transfer.getDestination().getNumber(),transfer.getDestination().getDigit()));
        dto.setValue(transfer.getValue());
        return dto;
    }
}

