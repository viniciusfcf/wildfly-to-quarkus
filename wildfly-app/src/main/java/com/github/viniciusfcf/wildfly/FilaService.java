package com.github.viniciusfcf.wildfly;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;


@RequestScoped
public class FilaService {

	private LocalDateTime dataCriacao;
	
	@PostConstruct
	private void initDataCriacao() {
		this.dataCriacao = LocalDateTime.now();
	}

	public Avaliacao avaliar(String nome) {
		Avaliacao avaliacao = new Avaliacao();
		avaliacao.dataCriacaoFilaService = this.dataCriacao;
		avaliacao.nome = nome;
		return avaliacao;
	}
	
	
}
