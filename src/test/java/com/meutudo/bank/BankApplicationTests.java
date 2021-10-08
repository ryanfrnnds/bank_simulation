package com.meutudo.bank;

import com.meutudo.bank.dto.AccountDto;
import com.meutudo.bank.dto.TransferDto;
import com.meutudo.bank.model.Account;
import com.meutudo.bank.model.Transfer;
import com.meutudo.bank.repository.TransferRepository;
import com.meutudo.bank.service.AccountService;
import com.meutudo.bank.service.TransferService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BankApplicationTests {

	@Autowired
	TransferService transferService;

	@Autowired
	AccountService accountService;

	@Autowired
	TransferRepository transferRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void testConcurrencyWithOptimistLock() throws InterruptedException {
		Double value = Double.valueOf(89.23);
		Transfer params = build(value, LocalDateTime.now());

		TransferService transferServiceSpy = Mockito.spy(new TransferService());

		Thread thread = new Thread(() -> {
			transferService.create(convertDto(params));
		});

		Thread thread2 = new Thread(() -> {
			transferService.create(convertDto(params));
		});
		thread.start();
		thread2.start();

		Thread.sleep(1000L);

		int countTransfer = transferRepository.findAll().size();
		assertEquals(1, countTransfer);
	}

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
