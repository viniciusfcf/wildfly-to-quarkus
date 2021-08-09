package com.github.viniciusfcf.wildfly;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.github.viniciusfcf.wildfly.entity.Avaliacao;
import com.github.viniciusfcf.wildfly.entity.Evento;
import com.github.viniciusfcf.wildfly.entity.Roles;
import com.github.viniciusfcf.wildfly.entity.Usuario;

@Stateless
@Interceptors(MeuInterceptor.class)
public class UsuarioServiceEJB {

	@Inject
	private FilaService filaService;

	@PersistenceContext(unitName = "myPersistenceUnit")
	private EntityManager entityManager;
	
	@Inject
	private OutroService outroService;

	@SuppressWarnings("serial")
	private static final Map<String, Usuario> USER_DB = new HashMap<String, Usuario>() {
		{
			put("customer", new Usuario("customer", "customerpw", Roles.CUSTOMER));
			put("admin", new Usuario("admin", "adminpw", Roles.ADMIN));
		}
	};

	public Usuario authenticate(final String username, final String password) throws Exception {
		Usuario user = USER_DB.get(username);
		if (user != null && user.getPassword().equals(password)) {
			return user;
		}
		throw new Exception(
				"Failed logging in org.jboss.user with name '" + username + "': unknown username or wrong password");
	}

	public Avaliacao avaliar(String nome) throws Exception {
		return filaService.avaliar(nome);
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> buscarTodos() {
		return entityManager.createQuery("select u from Usuario u").getResultList();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Evento> testeRollback(Boolean newTransaction) {
		Evento evento = new Evento();
		evento.setMomento(LocalDateTime.now());
		evento.setNome("Primeiro Evento");
		entityManager.persist(evento);
		try {
			if(newTransaction) {
				outroService.metodoComRequiresNew();
			}else {
				outroService.metodoComRequired();
			}
		}catch (Exception e) {
			//Não façam isso em produção
		}
		
		return buscarTodosEventos();
	}

	@SuppressWarnings("unchecked")
	public List<Evento> buscarTodosEventos() {
		return entityManager.createQuery("select e from Evento e order by e.momento desc").getResultList();
	}

}
