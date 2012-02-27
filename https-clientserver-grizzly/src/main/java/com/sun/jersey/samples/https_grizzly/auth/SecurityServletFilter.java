/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.jersey.samples.https_grizzly.auth;

import org.apache.commons.codec.binary.Base64;
import java.security.Principal;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

/* Kept inactive in this sample, but provides an alternative
 * to using a CXF JAX-RS filter for doing authorization.
 * 
 * Note if this class is used instead of the SecurityCXFJAXRSFilter,
 * the AuthenticationException raised below will *not* get mapped
 * by the AuthenticationExceptionMapper, as Servlet Filter-originated
 * exceptions are presently outside the scope of JAX-RS exception 
 * mapping.-- only a 500 response code ("internal server error") 
 * will get returned instead.
 */
public class SecurityServletFilter implements Filter {

	@Context
    UriInfo uriInfo;
    private static final String REALM = "HTTPS Example authentication";

    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {

    	User user = authenticate((HttpServletRequest) req);
        chain.doFilter(req, res);
    }

    public void init(FilterConfig config) throws ServletException {    	
    }

    public void destroy() {
    }
    
    private User authenticate(HttpServletRequest request) {
        // Extract authentication credentials
        String authentication = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authentication == null) {
            throw new AuthenticationException("Authentication credentials are required", REALM);
        }
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
            throw new RuntimeException(new AuthenticationException("Invalid username or password\r\n", REALM));
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
