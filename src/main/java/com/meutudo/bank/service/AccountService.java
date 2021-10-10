package com.meutudo.bank.service;

import com.meutudo.bank.exceptions.BalanceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meutudo.bank.model.Account;
import com.meutudo.bank.repository.AccountRepository;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Optional;

@Service
public class AccountService {
	
	@Autowired
	AccountRepository accountRepository;

	public Optional<Account> get(String agency, String number, String digit) {
		return accountRepository.findByAgencyAndNumberAndDigit(agency,number,digit);
	}
	
	public BigDecimal getBalance(String agency, String number, String digit) {
		Optional<Account> optAccount = accountRepository.findByAgencyAndNumberAndDigit(agency,number,digit);
		if (optAccount.isEmpty()) {
			throw new BalanceNotFoundException(MessageFormat.format("Dados bancários não encontrado. Agencia: {0}, Conta: {1}-{2} ", agency, number,digit));
		}
		return optAccount.get().getBalance();
	}
	
	public boolean checkFound(String agency, String number, String digit) {
		Optional<Account> optAccount = accountRepository.findByAgencyAndNumberAndDigit(agency,number,digit);
		return optAccount.isPresent();
	}
}
