/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.jersey.samples.https_grizzly.auth;

import java.io.IOException;
import java.util.List;
import java.security.Principal;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.jaxrs.ext.RequestHandler;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.message.Message;

@Provider
public class SecurityCXFJAXRSFilter implements RequestHandler {

	@Context
    UriInfo uriInfo;
	
	@Context
	HttpHeaders httpHeaders;
	
    private static final String REALM = "HTTPS Example authentication";

    public Response handleRequest(Message inputMessage, ClassResourceInfo resourceClass) {
    	authenticate();
    	return null;
    }

    private User authenticate() {
        // Extract authentication credentials
        List<String> authenticationList = httpHeaders.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authenticationList.size() < 1) {
            throw new AuthenticationException("Authentication credentials are required", REALM);
        }
        String authentication = authenticationList.get(0); 
        if (!authentication.startsWith("Basic ")) {
            return null;
            // additional checks should be done here
            // "Only HTTP Basic authentication is supported"
        }
        authentication = authentication.substring("Basic ".length());
        String[] values = new String(Base64.decodeBase64(authentication)).split(":");
        if (values.length < 2) {
            throw new WebApplicationException(400);
            // "Invalid syntax for username and password"
        }
        String username = values[0];
        String password = values[1];
        if ((username == null) || (password == null)) {
            throw new WebApplicationException(400);
            // "Missing username or password"
        }

        // Validate the extracted credentials
        User user = null;

        if (username.equals("user") && password.equals("password")) {
            user = new User("user", "user");
            System.out.println("USER AUTHENTICATED");
        //        } else if (username.equals("admin") && password.equals("adminadmin")) {
        //            user = new User("admin", "admin");
        //            System.out.println("ADMIN AUTHENTICATED");
        } else {
            System.out.println("USER NOT AUTHENTICATED");
            throw new AuthenticationException("Invalid username or password\r\n", REALM);
        }
        return user;
    }

    public class Authorizer implements SecurityContext {

        private User user;
        private Principal principal;

        public Authorizer(final User user) {
            this.user = user;
            this.principal = new Principal() {

                public String getName() {
                    return user.username;
                }
            };
        }

        public Principal getUserPrincipal() {
            return this.principal;
        }

        public boolean isUserInRole(String role) {
            return (role.equals(user.role));
        }

        public boolean isSecure() {
            return "https".equals(uriInfo.getRequestUri().getScheme());
        }

        public String getAuthenticationScheme() {
            return SecurityContext.BASIC_AUTH;
        }
    }

    public class User {

        public String username;
        public String role;

        public User(String username, String role) {
            this.username = username;
            this.role = role;
        }
    }
}
