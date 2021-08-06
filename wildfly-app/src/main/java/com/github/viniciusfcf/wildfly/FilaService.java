package com.github.viniciusfcf.wildfly;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;


@RequestScoped
public class FilaService {

	private LocalDateTime dataCriacao;
	
	@Inject
    @JMSConnectionFactory("java:/RemoteJmsXA")
    private JMSContext context;
	
    @Resource(lookup = "java:global/remoteContext/novaAvaliacaoQueue")
    private Queue queue;
	
	@PostConstruct
	private void initDataCriacao() {
		this.dataCriacao = LocalDateTime.now();
	}

	public Avaliacao avaliar(String nome) throws Exception {
		final Destination destination = queue;
		

		JMSProducer createProducer = context.createProducer();
		createProducer.send(destination, "Nome: "+nome);
		Avaliacao avaliacao = new Avaliacao();
		avaliacao.dataCriacaoFilaService = this.dataCriacao;
		avaliacao.nome = nome;
		
		Message message = context.createConsumer(destination).receive();
		avaliacao.messageID = message.getJMSMessageID();
				
		return avaliacao;
	}
	
	
}
