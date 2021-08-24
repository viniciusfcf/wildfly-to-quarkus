package com.github.viniciusfcf.wildfly;

import java.time.LocalDateTime;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.github.viniciusfcf.wildfly.entity.Evento;

// @Stateless
@ApplicationScoped
public class OutroService {

	// @PersistenceContext(unitName = "myPersistenceUnit")
	@Inject
	private EntityManager entityManager;
	
	// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Transactional(value=TxType.REQUIRES_NEW)
	public List<Evento> metodoComRequiresNew() {
		entityManager.persist(novoEvento());
		throw new IllegalArgumentException();
	}

	// @TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Transactional(value=TxType.REQUIRED)
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
