package com.github.viniciusfcf.wildfly;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class UsuarioDAO {


    @PersistenceContext(unitName="myPersistenceUnit") 
    private EntityManager entityManager;
    
    @SuppressWarnings("unchecked")
	public List<Usuario> findAll() {
    	return entityManager.createQuery("select u from Usuario").getResultList();
    }
}
