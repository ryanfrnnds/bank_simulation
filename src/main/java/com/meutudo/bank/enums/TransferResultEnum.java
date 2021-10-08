package com.meutudo.bank.enums;

import com.meutudo.bank.model.Transfer;
import lombok.Getter;

import java.text.MessageFormat;

@Getter
public enum TransferResultEnum {

	CREATED(1, "Transferência realizada com sucesso!"),
	INSUFFICIENT_FUNDS(2, "Saldo insuficiente. Saldo: {0}, valor da transferência: {1}"),
	VALUE_MUST_BE_GREATER_THAN_ZERO(3, "Valor da transferência deve ser maior que zero"),
	ORIGIN_NOT_FOUND(4, "Conta de ORIGEM não encontrada. Agência: {0}, Conta: {1}"),
	DESTINATION_NOT_FOUND(4, "Conta de DESTINO não encontrada. Agência: {0}, Conta: {1}");
	
	private Integer code;
	private String description;

	TransferResultEnum(Integer code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getMessage(Transfer params) {
		if(this.getCode().equals(INSUFFICIENT_FUNDS.getCode())) {
			return MessageFormat.format(this.description,params.getOrigin().getBalance(),params.getValue());
		}
		if(this.getCode().equals(VALUE_MUST_BE_GREATER_THAN_ZERO.getCode())) {
			return this.description;
		}
		if(this.getCode().equals(ORIGIN_NOT_FOUND.getCode())) {
			return MessageFormat.format(this.description,params.getOrigin().getAgency(),(params.getOrigin().getNumber()+"-"+params.getOrigin().getDigit()));
		}
		if(this.getCode().equals(DESTINATION_NOT_FOUND.getCode())) {
			return MessageFormat.format(this.description,params.getDestination().getAgency(),(params.getDestination().getNumber()+"-"+params.getDestination().getDigit()));
		}
		return description;
	}
}