package com.meutudo.bank.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import com.meutudo.bank.enums.TransferResultEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@JsonSerializableSchema
@Getter
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "TRANSFER", schema = "SIMULATION")
public class Transfer extends BaseModel<Long> {

	@ManyToOne
	@JoinColumn(name="ORIGIN_ACCOUNT_FK")
	@JsonBackReference
	private Account origin;
	
	@ManyToOne
	@JoinColumn(name="DESTINATION_ACCOUNT_FK")
	private Account destination;

	private LocalDate date;

	private Long revertTransferId;

	@CreationTimestamp
	private LocalDateTime createdAt;

	private Double value;

	@Transient
	@JsonIgnore
	TransferResultEnum result;

	@Transient
	@JsonIgnore
	private boolean revert;

	@Transient
	private int quantityCashPurcahses = 1;

	@Transient
	private Double valueLastCashPurchases;

	public Transfer(Account origin, Account destination, LocalDate date, double value, boolean isRevert) {
		this.origin = origin;
		this.destination = destination;
		this.date = date;
		this.value = value;
		this.revert = isRevert;
	}

	public Transfer(Account origin, Account destination, LocalDate date, double value, int quantityCashPurcahses) {
		this.origin = origin;
		this.destination = destination;
		this.date = date;
		this.value = value;
		this.revert = false;
		this.quantityCashPurcahses = quantityCashPurcahses;
	}

}
