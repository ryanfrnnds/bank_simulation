package com.meutudo.bank.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import com.meutudo.bank.enums.TransferTypeEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
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

	@Column(updatable = false)
	private LocalDate date;

	@Column(updatable = false)
	private Long revertTransferId;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@Column(updatable = false)
	private BigDecimal value;

	@Enumerated(EnumType.STRING)
	@Column(updatable = false)
	private TransferTypeEnum type = TransferTypeEnum.DEFAULT;

	public Transfer(Account origin, Account destination, LocalDate date, BigDecimal value) {
		this.origin = origin;
		this.destination = destination;
		this.date = date;
		this.value = value;
	}

	public Transfer revert() {
		Transfer revert = new Transfer();
		revert.setValue(this.value);
		revert.setDate(LocalDate.now());
		revert.setRevertTransferId(this.id);
		revert.setOrigin(this.destination);
		revert.setDestination(this.origin);
		revert.setType(TransferTypeEnum.REVERT);

		revert.getOrigin().setBalance(this.destination.getBalance().subtract(this.value));
		revert.getDestination().setBalance(this.origin.getBalance().add(this.value));
		return revert;
	}

}
