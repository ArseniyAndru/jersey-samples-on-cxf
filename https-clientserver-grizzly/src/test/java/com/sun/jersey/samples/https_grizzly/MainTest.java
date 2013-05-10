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

package com.sun.jersey.samples.https_grizzly;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Ignore;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import java.io.InputStream;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import com.sun.jersey.samples.https_grizzly.auth.AuthenticationException;

public class MainTest {
   
    private static final String CLIENT_CONFIG_FILE = "ClientConfig.xml";
    private static final String CLIENT_CONFIG_NOKEY_FILE = "ClientConfigNoKey.xml";
    
    @BeforeClass
    public static void setUp() throws Exception {
        Server.startServer();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Server.stopServer();
    }

    /**
     * Test to see that the message "CXF HTTPS EXAMPLE" is sent in the response.
     */
    @Test
    public void testSSLWithAuth() {
        System.out.println("***** testSSLwithAuth(): should work");
        WebClient wc = WebClient.create(Server.BASE_URI.toString(), "user", "password",
           CLIENT_CONFIG_FILE);

        try {
        	InputStream is = wc.get(InputStream.class);
            String pageContent = IOUtils.toString(is);
            System.out.println("Response string returned = " + pageContent);
            assertEquals(Server.CONTENT, pageContent);
        } catch (Exception ex) {
            Assert.fail("Exception reading InputStream: " + ex.getMessage());
        }
    }

    /**
     *
     * Test to see that HTTP 401 is returned when client tries to GET without
     * proper basic auth credentials.
     */
    @Test
    public void testHTTPBasicAuth1() {
        System.out.println("***** testHTTPBasicAuth1(): should fail");
        WebClient wc = WebClient.create(Server.BASE_URI.toString(), CLIENT_CONFIG_FILE);

        try {
        	InputStream is = wc.get(InputStream.class);
        } catch (Exception ex) {
        	assertTrue(ex instanceof javax.ws.rs.NotAuthorizedException);
        }
    }

    /**
     *
     * Test to see that SSLHandshakeException is thrown when client doesn't have
     * trusted key.
     */
    @Test
    public void testSSLAuth1() {
       System.out.println("***** testSSLAuth1(): should fail");
       WebClient wc = WebClient.create(Server.BASE_URI.toString(), "user", "password",
       		CLIENT_CONFIG_NOKEY_FILE);

        try {
        	InputStream is = wc.get(InputStream.class);
        } catch (Exception ex) {
            String msg = ex.getMessage();
            System.out.println("Error message = "  + msg);
        	assertTrue(msg.contains("ClientException"));
        }
    }
}
