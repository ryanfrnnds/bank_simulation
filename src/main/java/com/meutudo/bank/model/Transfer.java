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

	private LocalDateTime date;

	@CreationTimestamp
	private LocalDateTime createdAt;

	
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
