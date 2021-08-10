package com.github.viniciusfcf.wildfly;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import com.github.viniciusfcf.wildfly.entity.Avaliacao;
import com.github.viniciusfcf.wildfly.entity.Evento;
import com.github.viniciusfcf.wildfly.entity.Usuario;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

//JAX-RS
@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioRest {

    private static final Logger log = Logger.getLogger(UsuarioRest.class.getName());

    //CDI
    @Inject
    private JwtManager jwtManager;

    //EJB
    @EJB
    private UsuarioServiceEJB service;

    //Modo comum em um Servidor de aplicação.
    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/customer")
    public String getCustomerJSON() {
        return "{\"path\":\"customer\",\"result\":" + sayHello() + "}";
    }

    @GET
    @Path("/protected")
    public String getProtectedJSON() {
        return "{\"path\":\"protected\",\"result\":" + sayHello() + "}";
    }

    @GET
    @Path("/public")
    public String getPublicJSON() {
        return "{\"path\":\"public\",\"result\":" + sayHello() + "}";
    }

    @GET
    @Path("/claims")
    public Response demonstrateClaims(@HeaderParam("Authorization") String auth) {
        if (auth != null && auth.startsWith("Bearer ")) {
            try {
                JWT j = JWTParser.parse(auth.substring(7));
                return Response.ok(j.getJWTClaimsSet().getClaims()).build(); //Note: nimbusds converts token expiration time to milliseconds
            } catch (ParseException e) {
                log.warning(e.toString());
                return Response.status(Status.BAD_REQUEST).build();
            }
        }
        return Response.status(Status.NO_CONTENT).build(); //no jwt means no claims to extract
    }

    @POST
    @Path("/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postJWT(@FormParam("username") String username, @FormParam("password") String password) {
        log.info("Authenticating " + username);
        try {
            Usuario user = service.authenticate(username, password);
            if (user != null) {
                if (user.getName() != null) {
                    log.info("Generating JWT for org.jboss.user " + user.getName());
                }
                String token = jwtManager.createJwt(user.getName(), user.getRoles());
                return Response.ok(new Jwt(token)).build();
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    private String sayHello() {
        Principal userPrincipal = securityContext.getUserPrincipal();
        String principalName = userPrincipal == null ? "anônimo" : userPrincipal.getName();
        return "\"Oi " + principalName + "!\"";
    }
    
    //JSONB
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
    
    @GET
    public Response buscarTodos() {
    	return Response.ok(service.buscarTodos()).build();
    }
    
    @GET
    @Path("jta")
    public List<Evento> testeRollback(@QueryParam("newTransaction") boolean newTransaction) {
    	return service.testeRollback(newTransaction);
    }
    
    @GET
    @Path("/eventos")
    public List<Evento> eventos() {
    	return service.buscarTodosEventos();
    }

    @GET
    @Path("/json-p")
    @Produces(MediaType.TEXT_PLAIN)
    public String jsonP() {
        JsonObject json = Json.createObjectBuilder()
        .add("nome", "Vinicius Ferraz")
        .add("idade", BigDecimal.valueOf(36)).build();
    	return "JSON gerado: "+json.toString();
    }

    
}
