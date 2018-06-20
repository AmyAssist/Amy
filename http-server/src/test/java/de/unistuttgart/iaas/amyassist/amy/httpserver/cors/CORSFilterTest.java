/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 *
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information see notice.md
 */

package de.unistuttgart.iaas.amyassist.amy.httpserver.cors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * test class for {@link CORSFilter}
 * @author Christian
 *
 */
class CORSFilterTest {

	private static final String ALLOWED_ORIGIN = "https://amyassist.github.io";

	@Test
	public void testRequestFilter() {
		try {
			CORSFilter filter = new CORSFilter();
			ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
			Mockito.when(requestContext.getHeaderString(Headers.ORIGIN)).thenReturn(null);
		
			try {
				filter.filter(requestContext);
				Mockito.verify(requestContext, Mockito.never()).getMethod();
			} catch (WebApplicationException e) {
				fail("");
			}
			
			Mockito.when(requestContext.getHeaderString(Headers.ORIGIN)).thenReturn(ALLOWED_ORIGIN);
			Mockito.when(requestContext.getMethod()).thenReturn("GET");
			
			try {
				filter.filter(requestContext);
			} catch (WebApplicationException e) {
				fail("");
			}
			
			Mockito.when(requestContext.getHeaderString(Headers.ORIGIN)).thenReturn("bad.website.com");
			Mockito.when(requestContext.getMethod()).thenReturn("GET");
			
			try {
				filter.filter(requestContext);
				fail("");
			} catch (WebApplicationException e) {
				assertEquals(403, e.getResponse().getStatus());
				assertEquals("bad.website.com not allowed", e.getMessage());
				Mockito.verify(requestContext).setProperty("access.denied", true);
			}
			
			Mockito.when(requestContext.getHeaderString(Headers.ORIGIN)).thenReturn(ALLOWED_ORIGIN);
			Mockito.when(requestContext.getMethod()).thenReturn("Options");
			
			try {
				filter.filter(requestContext);
				Mockito.verify(requestContext).abortWith(Mockito.any());
			} catch (WebApplicationException e) {
				fail("");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testResponseFilter() {
		try {
			CORSFilter filter = new CORSFilter();
			ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
			ContainerResponseContext responseContext = Mockito.mock(ContainerResponseContext.class);
			MultivaluedMap<String, Object> headers = Mockito.mock(MultivaluedMap.class);
			
			Mockito.when(requestContext.getHeaderString(Headers.ORIGIN)).thenReturn(null);
			filter.filter(requestContext, responseContext);
			Mockito.verify(responseContext, Mockito.never()).getHeaders();
			
			Mockito.when(requestContext.getHeaderString(Headers.ORIGIN)).thenReturn(ALLOWED_ORIGIN);
			Mockito.when(requestContext.getMethod()).thenReturn("OPTIONS");
			filter.filter(requestContext, responseContext);
			Mockito.verify(responseContext, Mockito.never()).getHeaders();
			
			Mockito.when(requestContext.getHeaderString(Headers.ORIGIN)).thenReturn(ALLOWED_ORIGIN);
			Mockito.when(requestContext.getMethod()).thenReturn("POST");
			Mockito.when(requestContext.getProperty("access.denied")).thenReturn(true);
			filter.filter(requestContext, responseContext);
			Mockito.verify(responseContext, Mockito.never()).getHeaders();

			Mockito.when(requestContext.getHeaderString(Headers.ORIGIN)).thenReturn(ALLOWED_ORIGIN);
			Mockito.when(requestContext.getMethod()).thenReturn("POST");
			Mockito.when(requestContext.getProperty("access.denied")).thenReturn(null);
			Mockito.when(responseContext.getHeaders()).thenReturn(headers);
			filter.filter(requestContext, responseContext);
			Mockito.verify(headers).putSingle(Headers.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN);
			Mockito.verify(headers).putSingle(Headers.VARY, Headers.ORIGIN);
			
			Mockito.when(requestContext.getHeaderString(Headers.ORIGIN)).thenReturn("allowed.origin.com");
			Mockito.when(requestContext.getMethod()).thenReturn("POST");
			Mockito.when(requestContext.getProperty("access.denied")).thenReturn(null);
			Mockito.when(responseContext.getHeaders()).thenReturn(headers);
			filter.filter(requestContext, responseContext);
			Mockito.verify(headers).putSingle(Headers.ACCESS_CONTROL_ALLOW_ORIGIN, "allowed.origin.com");
			Mockito.verify(headers, Mockito.times(2)).putSingle(Headers.VARY, Headers.ORIGIN);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
