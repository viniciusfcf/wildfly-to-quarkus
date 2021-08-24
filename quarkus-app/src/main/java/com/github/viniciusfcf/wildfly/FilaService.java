package com.github.viniciusfcf.wildfly;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;

import com.github.viniciusfcf.wildfly.entity.Avaliacao;


@RequestScoped
public class FilaService {

	private LocalDateTime dataCriacao;
	
	//JMS
	// @Inject
    // @JMSConnectionFactory("java:/RemoteJmsXA")
    // private JMSContext context;
	
	@Inject
	private ConnectionFactory connectionFactory;

    // @Resource(lookup = "java:global/remoteContext/novaAvaliacaoQueue")
    // private Queue queue;
	
	@PostConstruct
	private void initDataCriacao() {
		this.dataCriacao = LocalDateTime.now();
	}

	public Avaliacao avaliar(String nome) throws Exception {
		JMSContext context = connectionFactory.createContext();
		final Destination destination = context.createQueue("nova_avaliacao");
		

		JMSProducer createProducer = context.createProducer();
		createProducer.send(destination, "Nome: "+nome);
		Avaliacao avaliacao = new Avaliacao();
		avaliacao.dataCriacaoFilaService = this.dataCriacao.toString();
		avaliacao.nome = nome;
		
		try(JMSConsumer createConsumer = context.createConsumer(destination);) {

			Message message = createConsumer.receive(5000);
			if(message != null) {
				avaliacao.messageID = message.getJMSMessageID();
			}
					
		}
		return avaliacao;
	}
	
	
}
