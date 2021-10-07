package com.meutudo.bank;

import com.meutudo.bank.enums.TransferResultEnum;
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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(SpringRunner.class)
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

    @Before
    public void setUp(){

        // Mockito.when(transferRepository.save(transfer)).thenReturn(transfer);
    }

    @Test
    //nao Deve Realizar Transferencia Com Saldo Insuficiente
    public void ShouldNotAccomplishTransferWithInsufficientBalance() {
        Transfer params = build(Double.valueOf(600.50), LocalDateTime.now());

        Mockito.when(accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(true);
        Mockito.when(accountService.checkFound(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit())).thenReturn(true);
        Mockito.when(accountService.getBalance(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(Optional.of(Double.valueOf(500)));

        Transfer resultado = transferService.generate(params);
        Assertions.assertEquals(resultado.getResult().getCode(), TransferResultEnum.INSUFFICIENT_FUNDS.getCode());
    }

    @Test
    //nao Deve Realizar Transferencia Com Valor Zero Ou Menor
    public void ShouldNotAccomplishTransfersWithAValueOfZeroOrLess() {
        Transfer params = build(Double.valueOf(0), LocalDateTime.now());

        Mockito.when(accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(true);
        Mockito.when(accountService.checkFound(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit())).thenReturn(true);

        Mockito.when(accountService.getBalance(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(Optional.of(Double.valueOf(500.50)));

        Transfer resultado = transferService.generate(params);
        Assertions.assertEquals(resultado.getResult().getCode(), TransferResultEnum.VALUE_MUST_BE_GREATER_THAN_ZERO.getCode());
    }

    @Test
    //nao Deve Realizar Transferencia Quando Origem Nao Encontrada
    public void ShouldNotAccomplishTransferWhenOriginNotFound() {
        Transfer params = build(Double.valueOf(500), LocalDateTime.now());

        Mockito.when(accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(false);

        Transfer resultado = transferService.generate(params);
        Assertions.assertEquals(resultado.getResult().getCode(), TransferResultEnum.ORIGIN_NOT_FOUND.getCode());
    }

    @Test
    //nao Deve Realizar Transferencia Quando Destino Nao Encontrada
    public void ShouldNotAccomplishTransferWhenDestinationNotFound() {
        Transfer params = build(Double.valueOf(500), LocalDateTime.now());

        Mockito.when(accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(true);
        Mockito.when(accountService.checkFound(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit())).thenReturn(false);

        Transfer resultado = transferService.generate(params);
        Assertions.assertEquals(resultado.getResult().getCode(), TransferResultEnum.ORIGIN_NOT_FOUND.getCode());
    }

    private Transfer build(Double value, LocalDateTime date) {
        Account origin = new Account("4421", "01520446", "9", 589.23);
        Account destination = new Account("5817", "82516", "8", 1008.87);
        return new Transfer(origin,destination, date,value);
    }
}
