package com.meutudo.bank.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;

@Getter
@JsonDeserialize(using = CustomEnumDeserializer.class)
@JsonFormat(shape = Shape.OBJECT)
public enum TypeAccountEnum implements IBaseEnum<TypeAccountEnum> {

	CHECKING(1, "CONTA CORRENTE"),
	SAVINGS(2, "CONTA POUPANÇA");
	
	private Integer codigo;
	private String descricao;

	TypeAccountEnum(Integer codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	public static class Converter extends CustomEnumJPAConverter<TypeAccountEnum, Integer> {
		public Converter() {
			super(TypeAccountEnum.class);
		}
	}
}