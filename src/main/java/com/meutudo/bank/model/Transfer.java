package com.meutudo.bank.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
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
	private Account Origin;
	
	@ManyToOne
	@JoinColumn(name="DESTINATION_ACCOUNT_FK")
	private Account Destination;
	
	private LocalDateTime date;
	
	private double value;
}
