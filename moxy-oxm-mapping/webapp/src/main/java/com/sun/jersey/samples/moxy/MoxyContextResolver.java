/*
 *
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
 * Class originally com.sun.jersey.moxy.MoxyContextResolver from
 * Jersey distribution. 
 * Portions Copyright 2012 Talend
 */
package com.sun.jersey.samples.moxy;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

@Provider
public class MoxyContextResolver implements ContextResolver<JAXBContext> {

    // This package holds the JAXB annotation-less POJOs that you want MOXy
	// to be able to map to XML.  Change to a semicolon-delimited String[] 
	// for multiple packages
	public static final String MOXY_OXM_BEAN_PACKAGES = "com.sun.jersey.samples.moxy.beans";

    final Set<String> oxmPackageNames = new HashSet<String>();
    final String oxmMappingUrl;

    public MoxyContextResolver() {
        oxmPackageNames.addAll(getPackageNames(MOXY_OXM_BEAN_PACKAGES));
        oxmMappingUrl = "eclipselink-oxm.xml";
    }

    private List<String> getPackageNames(Object p) {
        if (p == null) {
            return Collections.EMPTY_LIST;
        } else if (p instanceof String) {
            return Arrays.asList(getElements(new String[]{(String)p}));
        } else if (p instanceof String[]) {
            return Arrays.asList(getElements((String[])p));
        } else {
            throw new IllegalArgumentException("Package list must " +
                    "have a property value of type String or String[]");
        }
    }

    // method from com.sun.jersey.moxy.Helper class
    private static String[] getElements(String[] elements) {
        List<String> es = new LinkedList<String>();
        for (String element : elements) {
            if (element == null) continue;
            element = element.trim();
            if (element.length() == 0) continue;
            for (String subElement : getElements(element, ";")) {
                if (subElement == null || subElement.length() == 0) continue;
                es.add(subElement);
            }
        }
        return es.toArray(new String[es.size()]);
    }
    
    // method from com.sun.jersey.moxy.Helper class
    private static String[] getElements(String elements, String delimiters) {
        String regex = "[";
        for(char c : delimiters.toCharArray())
            regex += Pattern.quote(String.valueOf(c));
        regex += "]";

        String[] es = elements.split(regex);
        for (int i = 0; i < es.length; i++) {
            es[i] = es[i].trim();
        }
        return es;
    }    
    
    @Override
    public JAXBContext getContext(Class<?> type) {
    	final String typePackageName = type.getPackage().getName();
        System.out.println("typePackageName = " + typePackageName);

        if (!oxmPackageNames.contains(typePackageName)) {
            return null;
        }
        System.out.println("in oxmPackageNames");

        Map<String, Source> metadata = new HashMap<String, Source>();
        metadata.put(typePackageName,
                new StreamSource(type.getResourceAsStream(oxmMappingUrl)));

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadata);

        try {
            return JAXBContext.newInstance(typePackageName, 
                Thread.currentThread().getContextClassLoader(), properties);
        } catch (JAXBException ex) {
            throw new WebApplicationException(ex);
        }
    }
}