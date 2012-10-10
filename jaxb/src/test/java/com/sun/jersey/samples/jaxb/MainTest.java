/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Collection;
import java.util.Collections;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;

public class MainTest {

	@Before
	public void setUp() throws Exception {
		Main.startServer();
	}

	@After
	public void tearDown() throws Exception {
		Main.stopServer();
	}

	/**
	 * Test checks that an application.wadl file is present for the resource.
	 */
	@Test
	public void testApplicationWadl() {
		WebClient wc = WebClient.create(Main.BASE_URI);
		String serviceWadl = wc.path("jaxb").query("_wadl", "").accept(MediaType.TEXT_XML).get(String.class);
		assertTrue("Looks like the expected wadl was not generated", serviceWadl.length() > 0);
	}

    @Test
    public void testRootElement() {
		WebClient wc = WebClient.create(Main.BASE_URI);
        JAXBXmlRootElement e1 = wc.path("jaxb/XmlRootElement").
                get(JAXBXmlRootElement.class);

        JAXBXmlRootElement e2 = wc.type("application/xml").
                post(e1, JAXBXmlRootElement.class);

        assertEquals(e1, e2);
    }


    @Test
    public void testRootElementWithHeader() {
		WebClient wc = WebClient.create(Main.BASE_URI);
        String e1 = wc.path("jaxb/XmlRootElement").
                get(String.class);

        String e2 = wc.back(false).path("XmlRootElementWithHeader").
                get(String.class);

        assertTrue(e2.contains(
        		"<?xml-stylesheet type='text/xsl' href='http://localhost:9998/foobar.xsl'?>")
                && e2.contains(e1.substring(e1.indexOf("?>") + 2).trim()));
    }
/*
    @Test
    public void testJAXBElement() {
		WebClient wc = WebClient.create(Main.BASE_URI);

        GenericType<JAXBElement<JAXBXmlType>> genericType =
                new GenericType<JAXBElement<JAXBXmlType>>() {};

        JAXBElement<JAXBXmlType> e1 = wc.path("jaxb/JAXBElement").
                get(genericType);

        JAXBElement<JAXBXmlType> e2 = wc.type("application/xml").
                post(e1, genericType);

        assertEquals(e1.getValue(), e2.getValue());
    }
*/    

    @Test
    public void testXmlType() {
		JAXBElementProvider provider = new JAXBElementProvider();
		provider.setUnmarshallAsJaxbElement(true);

		WebClient wc = WebClient.create(Main.BASE_URI, Collections.singletonList(provider)); 
		
		JAXBXmlType t1 = wc.path("jaxb/JAXBElement").
                get(JAXBXmlType.class);

        JAXBElement<JAXBXmlType> e = new JAXBElement<JAXBXmlType>(
                new QName("jaxbXmlRootElement"),
                JAXBXmlType.class,
                t1);

        JAXBXmlType t2 = wc.back(false).path("XmlType").type("application/xml").
                post(e, JAXBXmlType.class);

        assertEquals(t1, t2);
    }
/*    
    @Test
    public void testRootElementCollection() {
        WebResource webResource = resource();
        GenericType<Collection<JAXBXmlRootElement>> genericType =
                new GenericType<Collection<JAXBXmlRootElement>>() {};

        Collection<JAXBXmlRootElement> ce1 = webResource.path("jaxb/collection/XmlRootElement").
                get(genericType);
        Collection<JAXBXmlRootElement> ce2 = webResource.path("jaxb/collection/XmlRootElement").
                type("application/xml").
                post(genericType, new GenericEntity<Collection<JAXBXmlRootElement>>(ce1){});

        assertEquals(ce1, ce2);
    }

    @Test
    public void testXmlTypeCollection() {
        WebResource webResource = resource();
        GenericType<Collection<JAXBXmlRootElement>> genericRootElement =
                new GenericType<Collection<JAXBXmlRootElement>>() {};
        GenericType<Collection<JAXBXmlType>> genericXmlType =
                new GenericType<Collection<JAXBXmlType>>() {};

        Collection<JAXBXmlRootElement> ce1 = webResource.path("jaxb/collection/XmlRootElement").
                get(genericRootElement);

        Collection<JAXBXmlType> ct1 = webResource.path("jaxb/collection/XmlType").
                type("application/xml").
                post(genericXmlType, new GenericEntity<Collection<JAXBXmlRootElement>>(ce1){});

        Collection<JAXBXmlType> ct2 = webResource.path("jaxb/collection/XmlRootElement").
                get(genericXmlType);

        assertEquals(ct1, ct2);
    }
*/

    @Test
    public void testRootElementArray() {
		WebClient wc = WebClient.create(Main.BASE_URI);

		JAXBXmlRootElement[] ae1 = wc.path("jaxb/array/XmlRootElement").
                get(JAXBXmlRootElement[].class);
        
		JAXBXmlRootElement[] ae2 = wc.
                type("application/xml").
                post(ae1, JAXBXmlRootElement[].class);

        assertEquals(ae1.length, ae2.length);
        
        for (int i = 0; i < ae1.length; i++)
            assertEquals(ae1[i], ae2[i]);
    }

    @Test
    @Ignore("Not supported in CXF")
    public void testXmlTypeArray() {
		WebClient wc = WebClient.create(Main.BASE_URI);

        JAXBXmlRootElement[] ae1 = wc.path("jaxb/array/XmlRootElement").
                get(JAXBXmlRootElement[].class);

        /* Jersey apparently supports seamless conversion
         * of JAXB classes where the underlying XML elements and
         * attributes are equivalent.  Here, the swapping between
         * JAXBXmlRootElement and JAXBXmlType.  CXF does not
         * plan to offer this support.
         */
        
        // fail in CXF, post() requires JAXBXmlRootElement[].class
        JAXBXmlType[] at1 = wc.back(false).path("XmlType").
                type("application/xml").
                post(ae1, JAXBXmlType[].class);

        // fail in CXF, get() returns JAXBXmlRootElement[]
        JAXBXmlType[] at2 = wc.back(false).path("XmlRootElement").
                get(JAXBXmlType[].class);

        assertEquals(at1.length, at2.length);
        for (int i = 0; i < at1.length; i++)
            assertEquals(at1[i], at2[i]);
    }
    
}
