package com.meutudo.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meutudo.bank.dto.AccountDto;
import com.meutudo.bank.dto.TransferDto;
import com.meutudo.bank.enums.TransferResultEnum;
import com.meutudo.bank.model.Account;
import com.meutudo.bank.model.Transfer;
import com.meutudo.bank.repository.TransferRepository;
import com.meutudo.bank.service.AccountService;
import com.meutudo.bank.service.TransferService;
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

import java.time.LocalDateTime;
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

    @Test
    //nao Deve Realizar Transferencia Com Saldo Insuficiente
    public void shouldNotAccomplishTransferWithInsufficientBalance() {
        Transfer params = build(Double.valueOf(600.50), LocalDateTime.now());

        Mockito.when(accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(true);
        Mockito.when(accountService.checkFound(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit())).thenReturn(true);
        Mockito.when(accountService.getBalance(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(Optional.of(Double.valueOf(500)));

        Transfer resultado = transferService.create(convertDto(params));

        assertAll(
                () -> verify(accountService, never()).get(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit()),
                () -> verify(accountService, never()).get(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit()),
                () -> verify(transferRepository, never()).save(params),
                () -> Assertions.assertEquals(resultado.getResult().getCode(), TransferResultEnum.INSUFFICIENT_FUNDS.getCode())
        );

    }

    @Test
    //nao Deve Realizar Transferencia Com Valor Zero Ou Menor
    public void shouldNotAccomplishTransfersWithAValueOfZeroOrLess() {
        Transfer params = build(Double.valueOf(0), LocalDateTime.now());

        Mockito.when(accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(true);
        Mockito.when(accountService.checkFound(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit())).thenReturn(true);

        Mockito.when(accountService.getBalance(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(Optional.of(Double.valueOf(500.50)));

        Transfer resultado = transferService.create(convertDto(params));

        assertAll(
                () -> verify(accountService, never()).get(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit()),
                () -> verify(accountService, never()).get(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit()),
                () -> verify(transferRepository, never()).save(params),
                () -> Assertions.assertEquals(resultado.getResult().getCode(), TransferResultEnum.VALUE_MUST_BE_GREATER_THAN_ZERO.getCode())
        );
    }

    @Test
    //nao Deve Realizar Transferencia Quando Origem Nao Encontrada
    public void shouldNotAccomplishTransferWhenOriginNotFound() {
        Transfer params = build(Double.valueOf(500), LocalDateTime.now());

        Mockito.when(accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(false);

        Transfer resultado = transferService.create(convertDto(params));

        assertAll(
                () -> verify(accountService, never()).get(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit()),
                () -> verify(accountService, never()).get(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit()),
                () -> verify(transferRepository, never()).save(params),
                () -> Assertions.assertEquals(resultado.getResult().getCode(), TransferResultEnum.ORIGIN_NOT_FOUND.getCode())
        );
    }

    @Test
    //nao Deve Realizar Transferencia Quando Destino Nao Encontrado
    public void shouldNotAccomplishTransferWhenDestinationNotFound() {
        Transfer params = build(Double.valueOf(500), LocalDateTime.now());

        Mockito.when(accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(true);
        Mockito.when(accountService.checkFound(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit())).thenReturn(false);

        Transfer resultado = transferService.create(convertDto(params));

        assertAll(
                () -> verify(accountService, never()).get(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit()),
                () -> verify(accountService, never()).get(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit()),
                () -> verify(transferRepository, never()).save(params),
                () -> Assertions.assertEquals(resultado.getResult().getCode(), TransferResultEnum.ORIGIN_NOT_FOUND.getCode())
        );
    }

    @Test
    //Deve Realizar Transferencia entre duas contas
    public void shouldAccomplishTransfer() throws JsonProcessingException {
        Double value = Double.valueOf(89.23);
        Transfer params = build(value, LocalDateTime.now());
        Double initialValueOrigin = params.getOrigin().getBalance();
        Double initialValueDestination = params.getDestination().getBalance();

        Mockito.when(accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(true);
        Mockito.when(accountService.checkFound(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit())).thenReturn(true);
        Mockito.when(accountService.getBalance(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(Optional.of(Double.valueOf(589.23)));
        Mockito.when(accountService.get(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(Optional.of(params.getOrigin()));
        Mockito.when(accountService.get(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit())).thenReturn(Optional.of(params.getDestination()));
        Mockito.when(transferRepository.save(Mockito.any(Transfer.class))).thenReturn(params);

        Transfer resultado = transferService.create(convertDto(params));

        assertAll(
                () -> verify(transferRepository, times(1)).save(params),
                () ->  Assertions.assertEquals(resultado.getOrigin().getBalance(), initialValueOrigin - value),
                () -> Assertions.assertEquals(resultado.getDestination().getBalance(), initialValueDestination + value),
                () -> Assertions.assertEquals(resultado.getResult().getCode(), TransferResultEnum.CREATED.getCode()),
                () -> Assertions.assertEquals(resultado.getValue(), value),
                () -> Assertions.assertEquals(resultado.getDate(), params.getDate())
        );
    }

    //TODO - Transferencia em Geral
    //--- CENÁRIOS ---
    // Não Deve Realizar Transferencia Com Saldo Insuficiente         - OK
    // Nao Deve Realizar Transferencia Com Valor Zero Ou Menor        - OK
    // Nao Deve Realizar Transferencia Quando Origem Nao Encontrada   - OK
    // Nao Deve Realizar Transferencia Quando Origem Nao Encontrada   - OK
    // Nao Deve Realizar Transferencia Quando Destino Nao Encontrado  - OK
    // Deve Realizar Transferencia entre duas contas                  - OK
    //TODO - Reverter transferencias
    //--- CENÁRIOS ---
    // deve reverter uma transferência - FALTANDO
    //TODO - Programar uma transferência futura parcelada (Pensar em cenários)
    //--- OBSERVAÇÕES ---
        //Atentar-se as condições de divisões que podem gerar DIZMA PERIODICA! Nesse caso utilizar o truncate na dizma e acrescer 1 centavo na ultima parcela.
        // Deve gerar quantidade de transferencia igual a quantidade de parcelas
        // Irá ser considerada qualquer transferencia de DATA FUTURA como sendo uma transferencia futura.
    //--- CENÁRIOS ---
    // deve programar uma transferência futura! - FALTANDO

    private Transfer build(Double value, LocalDateTime date) {
        Account origin = new Account("4421", "01520446", "9", 589.23);
        Account destination = new Account("5817", "82516", "8", 1008.87);
        return new Transfer(origin,destination, date,value);
    }

    private TransferDto convertDto(Transfer transfer) {
        TransferDto dto = new TransferDto();
        dto.setOrigin(new AccountDto(transfer.getOrigin().getAgency(),transfer.getOrigin().getNumber(),transfer.getOrigin().getDigit()));
        dto.setDestination(new AccountDto(transfer.getDestination().getAgency(),transfer.getDestination().getNumber(),transfer.getDestination().getDigit()));
        dto.setValue(transfer.getValue());
        dto.setDate(transfer.getDate());
        return dto;
    }
}
