package com.github.viniciusfcf.wildfly;

import java.security.Principal;
import java.text.ParseException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestRest {

    private static final Logger log = Logger.getLogger(TestRest.class.getName());

    @Inject
    JwtManager jwtManager;

//    @EJB
    UserService service;

    @Context
    private SecurityContext securityContext;

    //Security constraints are defined in web.xml

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
            User user = service.authenticate(username, password);
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
        String principalName = userPrincipal == null ? "anonymous" : userPrincipal.getName();
        return "\"Oi " + principalName + "!\"";
    }
}
