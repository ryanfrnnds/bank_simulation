package com.meutudo.bank.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import com.meutudo.bank.enums.TypeAccountEnum;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@JsonSerializableSchema
@Getter
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "ACCOUNT", schema = "SIMULACAO")
public class Account implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1353102708614516888L;

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID", unique=true, updatable = false, nullable = false)
	private Long id;
	
	private String agency;
	private String digitAgency;
	private String number;
	private String digit;
	
	@ManyToOne
	@JoinColumn(name="BANK_FK")
	private Bank bank;
	
	@Column(name = "TIPO_CONTA")
	@Convert(converter = TypeAccountEnum.Converter.class)
	private TypeAccountEnum typeAccountEnum;

}
