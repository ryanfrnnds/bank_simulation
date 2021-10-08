package com.meutudo.bank.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import com.meutudo.bank.enums.TransferResultEnum;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@JsonSerializableSchema
@Getter
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "TRANSFER", schema = "SIMULATION")
public class Transfer implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7186480650802340609L;

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID", unique=true, updatable = false, nullable = false)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="ORIGIN_ACCOUNT_FK")
	@JsonBackReference
	private Account origin;
	
	@ManyToOne
	@JoinColumn(name="DESTINATION_ACCOUNT_FK")
	private Account destination;
	
	private LocalDateTime date;

	
	private Double value;

	@Transient
	@JsonIgnore
	TransferResultEnum result;

	public Transfer(Account origin, Account destination, LocalDateTime date, double value) {
		this.origin = origin;
		this.destination = destination;
		this.date = date;
		this.value = value;
	}

	public void generate() {
		origin.setBalance(origin.getBalance() - value);
		destination.setBalance(destination.getBalance() + value);
	}
}
