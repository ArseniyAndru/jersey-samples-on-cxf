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
package com.sun.jersey.samples.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.RuntimeDelegate;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import com.sun.jersey.samples.jaxb.JAXBResource;
import com.sun.jersey.samples.jaxb.JAXBCollectionResource;
import com.sun.jersey.samples.jaxb.JAXBArrayResource;

public class Main {

    private static org.apache.cxf.endpoint.Server server;

    protected Main() {}

    public static final String BASE_URI = "http://localhost:9998/";

    public static void main(String args[]) throws Exception {
        startServer();
        System.out.println("Server ready...");

        Thread.sleep(125 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }

    public static void startServer() throws Exception {
        if (server == null) {
            JAXRSServerFactoryBean bean = new JAXRSServerFactoryBean();      

            List perRequestResourceList = new ArrayList();
            perRequestResourceList.add(JAXBResource.class);
            perRequestResourceList.add(JAXBCollectionResource.class);
            perRequestResourceList.add(JAXBArrayResource.class);
            bean.setResourceClasses(perRequestResourceList);
        
            bean.setAddress(BASE_URI);
            server = bean.create();
        }
    }

    public static void stopServer() throws Exception {
        if (server != null) {
            server.stop();
            server.destroy();
            server = null;
        }
    }
}

