package com.meutudo.bank.dto;

import com.meutudo.bank.model.Transfer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TransferFutureDto extends TransferDto {

	private LocalDate date;
	private int quantityCachePurchase;

	public TransferFutureDto(BigDecimal value, AccountDto origin , AccountDto destination, boolean isRevert, LocalDate date, int quantityCachePurchase) {
		super(value, origin , destination, isRevert);
		this.date = date;
		this.quantityCachePurchase = quantityCachePurchase;
	}

	public Transfer convert(){
		return new Transfer(getOrigin().convert(), getDestination().convert(), this.date, getValue());
	}
}



