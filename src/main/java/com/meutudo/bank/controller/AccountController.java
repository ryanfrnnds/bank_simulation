package com.meutudo.bank.controller;

import com.meutudo.bank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {
	
	@Autowired
	AccountService accountService;
	
	@GetMapping("/balance")
	public ResponseEntity balance(@RequestParam String agency, @RequestParam String number, @RequestParam String digit) {
		return new ResponseEntity<>(accountService.getBalance(agency,number,digit),  HttpStatus.OK);
	}
}
