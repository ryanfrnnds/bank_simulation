package com.meutudo.bank.model;

import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import com.meutudo.bank.enums.TypeAccountEnum;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@JsonSerializableSchema
@Getter
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "ACCOUNT", schema = "SIMULATION")
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
	private double balance;
	
	@ManyToOne
	@JoinColumn(name="BANK_FK")
	private Bank bank;
	
	@Column(name = "ACCOUNT_TYPE")
	@Convert(converter = TypeAccountEnum.Converter.class)
	private TypeAccountEnum typeAccountEnum;

}
