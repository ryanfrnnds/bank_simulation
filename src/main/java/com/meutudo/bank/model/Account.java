package com.meutudo.bank.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

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
	private String number;
	private String digit;
	private double balance;

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
