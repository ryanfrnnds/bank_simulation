package com.meutudo.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meutudo.bank.model.Account;
import com.meutudo.bank.repository.AccountRepository;

import java.util.Optional;

@Service
public class AccountService {
	
	@Autowired
	AccountRepository accountRepository;
	
	public Optional<Double> getBalance(String agency, String number, String digit) {
		Optional<Account> optAccount = accountRepository.findByAgencyAndNumberAndDigit(agency,number,digit);
		return optAccount.isPresent() ? optAccount.map(account -> account.getBalance()) : Optional.empty();
	}
	
	public boolean checkFound(String agency, String number, String digit) {
		Optional<Account> optAccount = accountRepository.findByAgencyAndNumberAndDigit(agency,number,digit);
		return optAccount.isPresent();
	}
}
