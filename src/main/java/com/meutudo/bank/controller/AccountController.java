package com.meutudo.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {
	
	@GetMapping("/saldo")
	public ResponseEntity<Double> getById(@RequestParam Long id) {
		return new ResponseEntity<Double>(Double.parseDouble("100.45"),  HttpStatus.OK);
	}

}
