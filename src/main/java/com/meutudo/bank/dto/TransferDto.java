package com.meutudo.bank.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meutudo.bank.model.Account;
import com.meutudo.bank.model.Transfer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TransferDto {

	private AccountDto origin;
	private AccountDto destination;
	private Double value;
	@JsonIgnore
	private boolean revert;

	public TransferDto(Double value, Account origin , Account destination, boolean isRevert) {
		this.origin = new AccountDto(origin);
		this.destination = new AccountDto(destination);
		revert = isRevert;
		this.value = value;
	}

	public TransferDto(Double value, AccountDto origin , AccountDto destination, boolean isRevert) {
		this.origin = origin;
		this.destination = destination;
		revert = isRevert;
		this.value = value;
	}

	public Transfer convert(){
		return new Transfer(origin.convert(), destination.convert(), LocalDate.now(),value, revert);
	}
}



