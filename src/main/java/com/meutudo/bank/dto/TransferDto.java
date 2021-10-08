package com.meutudo.bank.dto;

import com.meutudo.bank.model.Transfer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {

	private AccountDto origin;
	private AccountDto destination;
	private LocalDateTime date;
	private Double value;

	public Transfer convert(){
		return new Transfer(origin.convert(), destination.convert(), date, value);
	}
}



