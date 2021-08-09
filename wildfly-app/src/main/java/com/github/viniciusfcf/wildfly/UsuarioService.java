package com.github.viniciusfcf.wildfly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class UsuarioService {

	@Inject
	private FilaService filaService;

	@PersistenceContext(unitName = "myPersistenceUnit")
	private EntityManager entityManager;

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

}
