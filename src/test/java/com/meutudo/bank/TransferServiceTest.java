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
        Transfer params = build(Double.valueOf(600.50), LocalDateTime.now(), false);

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
        Transfer params = build(Double.valueOf(0), LocalDateTime.now(), false);

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
        Transfer params = build(Double.valueOf(500), LocalDateTime.now(), false);

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
        Transfer params = build(Double.valueOf(500), LocalDateTime.now(), false);

        Mockito.when(accountService.checkFound(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit())).thenReturn(true);
        Mockito.when(accountService.checkFound(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit())).thenReturn(false);

        Transfer resultado = transferService.create(convertDto(params));

        verify(accountService, never()).get(params.getOrigin().getAgency(), params.getOrigin().getNumber(),params.getOrigin().getDigit());
        verify(accountService, never()).get(params.getDestination().getAgency(), params.getDestination().getNumber(),params.getDestination().getDigit());
        verify(transferRepository, never()).save(params);
        Assertions.assertEquals(resultado.getResult().getCode(), TransferResultEnum.DESTINATION_NOT_FOUND.getCode());
    }

    @Test
    //Deve Realizar Transferencia entre duas contas
    public void shouldAccomplishTransfer() {
        Double value = Double.valueOf(89.23);
        Transfer params = build(value, LocalDateTime.now(), false);
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

    @Test
    //Não Deve Reverter uma tranferência realizada quando não encontrada
    public void shouldNotRevertTransferWhenNotFound() {
        Long id = 1L;
        Transfer params = new Transfer();
        params.setId(id);

        Transfer transfer = build(Double.valueOf(89.23), LocalDateTime.now(), true);
        transfer.setId(id);

        Mockito.when(transferRepository.findById(id)).thenReturn(Optional.empty());

        Transfer resultado = transferService.revert(id);

        assertAll(
                () -> verify(transferRepository, never()).save(transfer),
                () -> Assertions.assertEquals(resultado.getId(), id),
                () -> Assertions.assertEquals(resultado.getResult().getCode(), TransferResultEnum.NOT_FOUND.getCode())
        );
    }

    @Test
    //Não Deve Reverter uma tranferência realizada quando ja tiver sido revertida
    public void shouldNotRevertTransferWhenTransferIsRevert() throws JsonProcessingException {
        Long id = 1L;
        Transfer params = new Transfer();
        params.setId(id);

        Transfer transfer = build(Double.valueOf(89.23), LocalDateTime.now(), true);
        transfer.setId(id);
        transfer.setRevertTransferId(1L);

        Mockito.when(transferRepository.findById(id)).thenReturn(Optional.of(transfer));

        Transfer resultado = transferService.revert(id);

        assertAll(
                () -> verify(transferRepository, never()).save(transfer),
                () -> Assertions.assertEquals(resultado.getId(), id),
                () -> Assertions.assertNotNull(resultado.getRevertTransferId())
        );
    }

    @Test
    //Não Deve Reverter uma tranferência realizada quando não houver SALDO
    public void shouldNotRevertTransferWhenNotBalance() {
        Long idUpdateTransfer = 1L;
        Double value = Double.valueOf(2000.58);
        Transfer params = new Transfer();
        params.setId(idUpdateTransfer);

        Transfer transferRevert = build(value, LocalDateTime.now(), true);
        transferRevert.setId(idUpdateTransfer);

        Transfer newTransferForRevert = buildNewTransferForRevert(transferRevert, value);

        Mockito.when(accountService.checkFound(newTransferForRevert.getOrigin().getAgency(), newTransferForRevert.getOrigin().getNumber(),newTransferForRevert.getOrigin().getDigit())).thenReturn(true);
        Mockito.when(accountService.checkFound(newTransferForRevert.getDestination().getAgency(), newTransferForRevert.getDestination().getNumber(),newTransferForRevert.getDestination().getDigit())).thenReturn(true);

        Mockito.when(accountService.getBalance(newTransferForRevert.getOrigin().getAgency(), newTransferForRevert.getOrigin().getNumber(),newTransferForRevert.getOrigin().getDigit())).thenReturn(Optional.of(Double.valueOf(0)));

        Mockito.when(accountService.get(newTransferForRevert.getOrigin().getAgency(), newTransferForRevert.getOrigin().getNumber(),newTransferForRevert.getOrigin().getDigit())).thenReturn(Optional.of(newTransferForRevert.getOrigin()));
        Mockito.when(accountService.get(newTransferForRevert.getDestination().getAgency(), newTransferForRevert.getDestination().getNumber(),newTransferForRevert.getDestination().getDigit())).thenReturn(Optional.of(newTransferForRevert.getDestination()));

        Mockito.when(transferRepository.findById(idUpdateTransfer)).thenReturn(Optional.of(transferRevert));
        Mockito.when(transferRepository.save(newTransferForRevert)).thenReturn(newTransferForRevert);
        Mockito.when(transferRepository.save(transferRevert)).thenReturn(transferRevert);

        Transfer revertTransferResult = transferService.revert(idUpdateTransfer);

        assertAll(
                () -> verify(transferRepository, never()).save(Mockito.any(Transfer.class)),
                () -> Assertions.assertEquals(revertTransferResult.getResult().getCode(), TransferResultEnum.INSUFFICIENT_FUNDS.getCode())
        );
    }

    @Test
    //Deve Reverter uma tranferência realizada
    public void shouldRevertTransfer() throws JsonProcessingException {
        Long idUpdateTransfer = 1L;
        Double value = Double.valueOf(89.23);
        Transfer params = new Transfer();
        params.setId(idUpdateTransfer);

        Transfer transferRevert = build(value, LocalDateTime.now(), true);
        transferRevert.setId(idUpdateTransfer);

        Transfer newTransferForRevert = buildNewTransferForRevert(transferRevert, value);

        Mockito.when(accountService.checkFound(newTransferForRevert.getOrigin().getAgency(), newTransferForRevert.getOrigin().getNumber(),newTransferForRevert.getOrigin().getDigit())).thenReturn(true);
        Mockito.when(accountService.checkFound(newTransferForRevert.getDestination().getAgency(), newTransferForRevert.getDestination().getNumber(),newTransferForRevert.getDestination().getDigit())).thenReturn(true);
        Mockito.when(accountService.getBalance(newTransferForRevert.getOrigin().getAgency(), newTransferForRevert.getOrigin().getNumber(),newTransferForRevert.getOrigin().getDigit())).thenReturn(Optional.of(value));
        Mockito.when(accountService.get(newTransferForRevert.getOrigin().getAgency(), newTransferForRevert.getOrigin().getNumber(),newTransferForRevert.getOrigin().getDigit())).thenReturn(Optional.of(newTransferForRevert.getOrigin()));
        Mockito.when(accountService.get(newTransferForRevert.getDestination().getAgency(), newTransferForRevert.getDestination().getNumber(),newTransferForRevert.getDestination().getDigit())).thenReturn(Optional.of(newTransferForRevert.getDestination()));

        Mockito.when(transferRepository.findById(idUpdateTransfer)).thenReturn(Optional.of(transferRevert));
        Mockito.when(transferRepository.save(newTransferForRevert)).thenReturn(newTransferForRevert);
        Mockito.when(transferRepository.save(transferRevert)).thenReturn(transferRevert);

        Transfer revertTransferResult = transferService.revert(idUpdateTransfer);

        assertAll(
                () -> verify(transferRepository, times(2)).save(Mockito.any()),
                () -> Assertions.assertEquals(revertTransferResult.isRevert(), true),
                () -> Assertions.assertNotNull(revertTransferResult.getRevertTransferId()),
                () -> Assertions.assertEquals(revertTransferResult.getResult().getCode(), TransferResultEnum.CREATED.getCode())
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
    // Não Deve Reverter uma tranferência realizada quando não encontrada           - OK
    // Não Deve Reverter uma tranferência realizada quando ja tiver sido revertida  - OK
    // Não Deve Reverter uma tranferência realizada quando não houver SALDO         - OK
    // Deve Reverter uma tranferência realizada - OK
    //TODO - Programar uma transferência futura parcelada (Pensar em cenários)
    //--- OBSERVAÇÕES ---
        //Atentar-se as condições de divisões que podem gerar DIZMA PERIODICA! Nesse caso utilizar o truncate na dizma e acrescer 1 centavo na ultima parcela.
        // Deve gerar quantidade de transferencia igual a quantidade de parcelas
        // Irá ser considerada qualquer transferencia de DATA FUTURA como sendo uma transferencia futura.
    //--- CENÁRIOS ---
    // deve programar uma transferência futura! - FALTANDO

    private Transfer build(Double value, LocalDateTime date, boolean isRevert) {
        Account origin = new Account("4421", "01520446", "9", 589.23);
        Account destination = new Account("5817", "82516", "8", 1008.87);
        return new Transfer(origin,destination, date,value, isRevert);
    }

    private TransferDto convertDto(Transfer transfer) {
        TransferDto dto = new TransferDto();
        dto.setOrigin(new AccountDto(transfer.getOrigin().getAgency(),transfer.getOrigin().getNumber(),transfer.getOrigin().getDigit()));
        dto.setDestination(new AccountDto(transfer.getDestination().getAgency(),transfer.getDestination().getNumber(),transfer.getDestination().getDigit()));
        dto.setValue(transfer.getValue());
        dto.setDate(transfer.getDate());
        dto.setRevert(transfer.isRevert());
        return dto;
    }

    private Transfer buildNewTransferForRevert(Transfer transferRevert,Double value) {
        Transfer newTranfer = build(value, LocalDateTime.now(), false);
        Account origin = transferRevert.getOrigin();
        Account destination = transferRevert.getDestination();
        origin.setBalance(origin.getBalance() + value);
        destination.setBalance(destination.getBalance() - value);
        newTranfer.setOrigin(destination);
        newTranfer.setDestination(origin);
        return newTranfer;
    }
}
