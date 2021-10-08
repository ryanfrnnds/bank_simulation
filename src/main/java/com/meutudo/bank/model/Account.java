package com.meutudo.bank.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@JsonSerializableSchema
@Getter
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "ACCOUNT", schema = "SIMULATION")
public class Account extends BaseModel<Long> {
	
	private String agency;
	private String number;
	private String digit;
	private Double balance;

	@OneToMany(mappedBy = "origin", targetEntity = Transfer.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Transfer> transfers;
	
	@ManyToOne
	@JoinColumn(name="BANK_FK")
	private Bank bank;

	public Account(String agency, String number, String digit) {
		this.agency = agency;
		this.number = number;
		this.digit = digit;
		this.balance = balance;
	}

	public Account(String agency, String number, String digit, double balance) {
		this.agency = agency;
		this.number = number;
		this.digit = digit;
		this.balance = balance;
	}
}
