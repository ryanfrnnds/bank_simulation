package com.meutudo.bank.enums;

import lombok.Getter;

@Getter
public enum TransferResultEnum {

	OK(1, "OK"),
	INSUFFICIENT_FUNDS(2, "SALDO INSUFICIENTE"),
	VALUE_MUST_BE_GREATER_THAN_ZERO(3, "VALOR DEVE SER MAIOR QUE ZERO"),
	ORIGIN_NOT_FOUND(4, "CONTA DE ORIGEM NÃO ENCONTRADA"),
	DESTINATION_NOT_FOUND(4, "CONTA DE DESTINO NÃO ENCONTRADA");
	
	private Integer code;
	private String description;

	TransferResultEnum(Integer code, String description) {
		this.code = code;
		this.description = description;
	}
}