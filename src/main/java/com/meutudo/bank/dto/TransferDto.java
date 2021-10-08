package br.com.iago.simulacaoBanco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaDto {

	private Long idContaOrigem;
	private Long idContaDestino;
	private double valor;
}
