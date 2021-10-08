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
public class TransferFutureDto extends TransferDto {

	private LocalDateTime date;

	public TransferFutureDto(Double value, Account origin , Account destination, boolean isRevert, LocalDateTime date) {
		super(value, origin , destination, isRevert);
		this.date = date;
	}

	public Transfer convert(){
		return new Transfer(getOrigin().convert(), getDestination().convert(), this.date, getValue(), isRevert());
	}
}



