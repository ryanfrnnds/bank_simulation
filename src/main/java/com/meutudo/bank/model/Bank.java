package com.meutudo.bank.model;

import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@JsonSerializableSchema
@Getter
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "BANK", schema = "SIMULATION")
public class Bank extends BaseModel<Long> {
	private String code;
	private String description;
}
