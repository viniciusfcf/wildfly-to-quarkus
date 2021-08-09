package com.github.viniciusfcf.wildfly;

import java.time.LocalDateTime;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.github.viniciusfcf.wildfly.entity.Evento;

@Stateless
public class OutroService {

	@PersistenceContext(unitName = "myPersistenceUnit")
	private EntityManager entityManager;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<Evento> metodoComRequiresNew() {
		entityManager.persist(novoEvento());
		throw new IllegalArgumentException();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Evento> metodoComRequired() {
		entityManager.persist(novoEvento());
		throw new IllegalArgumentException();
	}

	private Object novoEvento() {
		Evento evento = new Evento();
		evento.setMomento(LocalDateTime.now());
		evento.setNome("Segundo Evento");
		return evento ;
	}

}
