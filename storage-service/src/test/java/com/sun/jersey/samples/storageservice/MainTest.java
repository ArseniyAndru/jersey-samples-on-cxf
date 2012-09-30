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

package com.sun.jersey.samples.storageservice;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import com.sun.jersey.samples.storageservice.Container;
import com.sun.jersey.samples.storageservice.Item;

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
	@Ignore
	public void testApplicationWadl() {
		WebClient wc = WebClient.create(Main.BASE_URI);
		String serviceWadl = wc.query("_wadl", null).accept(MediaType.TEXT_XML).get(String.class);
		assertTrue("Looks like the expected wadl was not generated", serviceWadl.length() > 0);
	}

	/**
	 * Test checks that an xml content is shown for the client request to
	 * resource "containers".
	 */
	@Test
	public void testContainersResource() {
		WebClient wc = WebClient.create(Main.BASE_URI);
		Containers containers = wc.path("containers").accept(MediaType.APPLICATION_XML)
				.get(Containers.class);
		assertNotNull(containers);
	}

	/**
	 * Test checks that containers and items could be added using PUT. It also
	 * checks that the number of items in the container is the same as the
	 * number which were added by PUT.
	 */
	@Test
	public void testPutOnContainerAndItemResource() {
		WebClient wc = WebClient.create(Main.BASE_URI);
		int createdResponse = Response.Status.CREATED.getStatusCode();

		// Create a child resource for the container "quotes"
		Container quotesContainer = new Container("quotes", Main.BASE_URI + "/quotes");

		// PUT the container "quotes"
		Response response = wc.path("containers/quotes").put(quotesContainer);
		assertEquals(createdResponse, response.getStatus());

		// PUT the items to be added to the "quotes" container
		response = wc.path("1").type(MediaType.TEXT_PLAIN)
				.put("Something is rotten in the state of Denmark");
		assertEquals(createdResponse, response.getStatus());

		response = wc.back(false).path("2").type(MediaType.TEXT_PLAIN).put("I could be bounded in a nutshell");
		assertEquals(createdResponse, response.getStatus());

		response = wc.back(false).path("3").type(MediaType.TEXT_PLAIN).put("catch the conscience of the king");
		assertEquals(createdResponse, response.getStatus());

		response = wc.back(false).path("4").type(MediaType.TEXT_PLAIN).put("Get thee to a nunnery");
		assertEquals(createdResponse, response.getStatus());

		// check that there are four items in the container "quotes"
		wc.back(true).path("containers/quotes");
		Container container = wc.accept(MediaType.APPLICATION_XML).get(Container.class);
		int numberOfItems = container.getItem().size();
		int expectedNumber = 4;
		assertEquals("Expected: " + expectedNumber + " items, Seeing: " + numberOfItems,
				expectedNumber, numberOfItems);

		// search the container for all items containing the word "king"
		wc.query("search", "king");
		container = wc.accept(MediaType.APPLICATION_XML).get(Container.class);
		numberOfItems = (container.getItem() == null) ? 0 : container.getItem().size();
		expectedNumber = 1;
		assertEquals("Expected: " + expectedNumber
				+ " items which pass the search criterion, Seeing: " + numberOfItems,
				expectedNumber, numberOfItems);
	}

	@Test
	public void testUpdateItem3() throws Exception {
		WebClient wc = WebClient.create(Main.BASE_URI);
		int createdResponse = Response.Status.CREATED.getStatusCode();

		// Create a child resource for the container "quotes"
		Container quotesContainer = new Container("quotes", Main.BASE_URI + "/quotes");

		// PUT the container "quotes"
		Response response = wc.path("containers/quotes").put(quotesContainer);

		// PUT the items to be added to the "quotes" container
		response = wc.path("3").type(MediaType.TEXT_PLAIN).put("catch the conscience of the king");

		// Get the last modified and etag of item 3
		String quote = wc.accept(MediaType.TEXT_PLAIN).get(String.class);
		response = wc.getResponse();

		// System.out.println(response.getMetadata().toString());
		String lastModified = (String) response.getMetadata().getFirst("Last-Modified");
		String etag = (String) response.getMetadata().getFirst("ETag");

		// Check that a Not Modified response is returned
		Item item = wc.header("If-Modified-Since", lastModified).header("If-None-Match", etag)
				.get(Item.class);
		response = wc.getResponse();
		assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());

		// Update item 3
		wc.type(MediaType.TEXT_PLAIN).put(
				"The play's the thing Wherein I'll catch the conscience of the king");

		// Check that a OK response is returned
		quote = wc.accept(MediaType.TEXT_PLAIN).header("If-Modified-Since", lastModified)
				.header("If-None-Match", etag).get(String.class);
		response = wc.getResponse();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals("The play's the thing Wherein I'll catch the conscience of the king", quote);
	}

	/**
	 * Test deletes the item 3, which is the only one which supposedly has the
	 * word "king" and then searches for the word in the other items of the
	 * container.
	 */
	@Test
	public void testDeleteItem3AndSearchForKing() {
		WebClient wc = WebClient.create(Main.BASE_URI);

		// Create a child resource for the container "quotes"
		Container quotesContainer = new Container("quotes", Main.BASE_URI + "/quotes");

		// PUT the container "quotes"
		Response response = wc.path("containers/quotes").put(quotesContainer);
		response = wc.path("1").type(MediaType.TEXT_PLAIN)
				.put("Something is rotten in the state of Denmark");
		response = wc.back(false).path("2").type(MediaType.TEXT_PLAIN).put("I could be bounded in a nutshell");
		response = wc.back(false).path("3").type(MediaType.TEXT_PLAIN).put("catch the conscience of the king");
		response = wc.back(false).path("4").type(MediaType.TEXT_PLAIN).put("Get thee to a nunnery");

		// delete item 3
		wc.back(false).path("3").delete();

		// search the container for all items containing the word "king"
		wc.back(true).path("containers/quotes").query("search", "king");
		Container container = wc.accept(MediaType.APPLICATION_XML).get(Container.class);
		int numberOfItems = (container.getItem() == null) ? 0 : container.getItem().size();
		int expectedNumber = 0;
		assertEquals("Expected: " + expectedNumber
				+ " items which pass the search criterion, Seeing: " + numberOfItems,
				expectedNumber, numberOfItems);
	}

	/**
	 * Test DELETEs the container "quotes" and sees that a 404 error is seen on
	 * subsequent requests for the container.
	 */
	@Test
	public void testDeleteContainerQuotes() {
		WebClient wc = WebClient.create(Main.BASE_URI);

		// Create a child resource for the container "quotes"
		Container quotesContainer = new Container("quotes", Main.BASE_URI + "/quotes");

		// PUT the container "quotes"
		Response response = wc.path("containers/quotes").put(quotesContainer);

		// delete the container
		wc.delete();

		boolean caught = false;
		try {
			wc.get(String.class);
		} catch (Exception e) {
			caught = true;
			response = wc.getResponse();
			assertEquals("404 error not seen on trying to access deleted container",
					Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		}
		assertTrue("Expecting a 404 exception to be thrown", caught);
	}
}
