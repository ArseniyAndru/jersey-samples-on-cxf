/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2011 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 * Portions Copyright 2012 Talend
 */

package com.sun.jersey.samples.moxy;

import javax.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.client.WebClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import com.sun.jersey.samples.moxy.beans.Customer;

/* 
 * For these tests, the service WADL will be available at:
 * http://localhost:8080/moxy/?_wadl
 * The single customer resource can be viewed at:
 * http://localhost:8080/moxy/customer
 * This test is being run as an integration test (i.e., mvn integration-test, not mvn test) 
 * because it needs the compiled WAR file to run.
 */
public class MoxyWebAppTestIT {

    private static Server server;
    private static int port = 8080;
    private static String urlStem;
    
    @BeforeClass
    public static void startUp() throws Exception {
        server = new Server();
		
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(MoxyWebAppTestIT.port);
        server.setConnectors(new Connector[] {connector});

        WebAppContext webappcontext = new WebAppContext();
        webappcontext.setContextPath("/moxy");
 
        webappcontext.setWar("target/moxy.war");

        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[] {webappcontext, new DefaultHandler()});

        server.setHandler(handlers);
        server.start();
        System.out.println("Server ready...");
        // Use below command if wish to keep server running while testing in browser
//      server.join();
        
        urlStem = "http://localhost:" + MoxyWebAppTestIT.port + "/moxy";
    }

    @AfterClass
    public static void shutDown() throws Exception {
    	server.stop();
    }

    /**
     * Test that the expected response is sent back.
     * @throws java.lang.Exception
     */
    @Test
    public void testCustomer() throws Exception {
        WebClient wc = WebClient.create(urlStem);
        wc.path("customer");
        Customer customer = wc.accept(MediaType.APPLICATION_XML).get(Customer.class);
        customer.setName("Tom Dooley");
        wc.type(MediaType.APPLICATION_XML).put(customer);
        Customer updatedCustomer = wc.get(Customer.class);
        Assert.assertEquals(customer, updatedCustomer);
    }

    @Test
    @Ignore("TBI")
    public void testApplicationWadl() {
        // need to see how to obtain WADL (WebClient or ?)
    	WebClient wc = WebClient.create(urlStem);
        String serviceWadl = wc.accept(MediaType.APPLICATION_XML).path("?_wadl").get(String.class);
        System.out.println(serviceWadl);
        Assert.assertTrue(serviceWadl.length() > 0);
    }
}
