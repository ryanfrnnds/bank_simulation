package com.meutudo.bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "TRANSFERENCIA", schema = "SIMULACAO")
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
