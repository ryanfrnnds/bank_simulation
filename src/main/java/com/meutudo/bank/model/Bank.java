package com.meutudo.bank.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;

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
@Table(name = "BANK", schema = "SIMULATION")
public class Bank implements Serializable {

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID", unique=true, updatable = false, nullable = false)
	private Long id;
	
	private String code;
	
	private String description;

}
