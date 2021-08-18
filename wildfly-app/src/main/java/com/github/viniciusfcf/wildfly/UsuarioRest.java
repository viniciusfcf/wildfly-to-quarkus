package com.github.viniciusfcf.wildfly;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.viniciusfcf.wildfly.entity.Avaliacao;
import com.github.viniciusfcf.wildfly.entity.Evento;

//JAX-RS
@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioRest {

    //EJB
	//Tem um Interceptor (MeuInterceptor)
    @EJB
    private UsuarioServiceEJB service;

    //JSON-B
    //JMS
    //PostConstruct de um Managed Bean
    @GET
    @Path("/avaliar")
    public Response avaliar(@QueryParam("nome") 
    	//BEAN VALIDATION
    	@NotBlank String nome) throws Exception {
        Avaliacao avaliacao = service.avaliar(nome);
		return Response.ok(avaliacao).build();
    }
    
    //JAXB
    @GET
    @Path("/xml/avaliar")
    @Produces(MediaType.APPLICATION_XML)
    public Response avaliarXml(@QueryParam("nome") @NotBlank String nome) throws Exception {
        Avaliacao avaliacao = service.avaliar(nome);
		return Response.ok(avaliacao).build();
    }
    
    //JPA
    //Buscando todos os Usuários
    @GET
    public Response buscarTodos() {
    	return Response.ok(service.buscarTodos()).build();
    }
    
    //JPA
    //Buscando todos os eventos
    @GET
    @Path("/eventos")
    public List<Evento> eventos() {
    	return service.buscarTodosEventos();
    }

    //JSON-P
    @GET
    @Path("/json-p")
    @Produces(MediaType.TEXT_PLAIN)
    public String jsonP() {
        JsonObject json = Json.createObjectBuilder()
        .add("nome", "Vinicius Ferraz")
        .add("idade", BigDecimal.valueOf(36)).build();
    	return "JSON gerado: "+json.toString();
    }

    @GET
    @Path("jta")
    public List<Evento> testeRollback(@QueryParam("newTransaction") boolean newTransaction) {
    	return service.testeRollback(newTransaction);
    }

    
}
