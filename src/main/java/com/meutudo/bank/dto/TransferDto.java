package com.meutudo.bank.dto;

import com.meutudo.bank.model.Account;
import com.meutudo.bank.model.Transfer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TransferDto {

	private AccountDto origin;
	private AccountDto destination;
	private LocalDateTime date;
	private Double value;
	private boolean revert;

	public TransferDto(Double value, Account origin , Account destination,LocalDateTime date, boolean isRevert) {
		this.origin = new AccountDto(origin);
		this.destination = new AccountDto(destination);
		revert = isRevert;
		this.date = date;
		this.value = value;
	}

	public Transfer convert(){
		return new Transfer(origin.convert(), destination.convert(), date, value, revert);
	}
}



