package com.meutudo.bank.dto;

import com.meutudo.bank.model.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
	private String agency;
	private String number;
	private String digit;

	public Account convert(){
		return new Account(agency, number, digit);
	}

	public AccountDto(Account account) {
		this.agency = account.getAgency();
		this.number = account.getNumber();
		this.digit = account.getDigit();
	}

}



