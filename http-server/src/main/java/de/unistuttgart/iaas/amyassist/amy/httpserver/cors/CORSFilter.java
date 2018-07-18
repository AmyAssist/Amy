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

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

/**
 * a filter for CORS requests and OPTIONS calls
 * 
 * @author Christian Bräuner, Benno Krauß
 *
 */
@Provider
@PreMatching
public class CORSFilter implements ContainerResponseFilter, ContainerRequestFilter {

	private static final String OPTIONS = "OPTIONS";
	private static final String ACCESS_DENIED = "access.denied";

	private static final String ALLOWED_HEADERS = "Content-Type, " + Headers.XOPTIONS;
	private static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";

	private static final String LOCALHOST_REGEX = "^http(s)?:\\/\\/localhost(:[1-9][0-9]*)?$";
	private static final String CONFIG_NAME = "cors.config";
	private static final String CONFIG_ORIGINS_KEY = "origins";

	@Reference
	public ConfigurationLoader configurationLoader;
	private Properties config;


	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		String origin = requestContext.getHeaderString(Headers.ORIGIN);
		if (origin == null || requestContext.getMethod().equalsIgnoreCase(OPTIONS)
				|| requestContext.getProperty(ACCESS_DENIED) != null) {
			// do nothing, either it isn't a cors request or an options call or already failed
			return;
		}
		responseContext.getHeaders().putSingle(Headers.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
		responseContext.getHeaders().putSingle(Headers.VARY, Headers.ORIGIN);
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String origin = requestContext.getHeaderString(Headers.ORIGIN);
		if (origin != null) {
			if (requestContext.getMethod().equalsIgnoreCase(OPTIONS)
					&& requestContext.getHeaderString(Headers.XOPTIONS) == null) {
				preflight(requestContext, origin);
			} else {
				checkOrigin(requestContext, origin);
			}
		}
	}

	private void preflight(ContainerRequestContext requestContext, String origin) {
		checkOrigin(requestContext, origin);

		ResponseBuilder builder = Response.ok();
		builder.header(Headers.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
		builder.header(Headers.ACCESS_CONTROL_ALLOW_HEADERS, ALLOWED_HEADERS);
		builder.header(Headers.ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS);

		requestContext.abortWith(builder.build());

	}

	private void checkOrigin(ContainerRequestContext requestContext, String origin) {
		if (!getAllowedOrigins().contains(origin) && !origin.matches(LOCALHOST_REGEX)) {
			requestContext.setProperty(ACCESS_DENIED, Boolean.TRUE);
			throw new WebApplicationException(origin + " not allowed", Status.FORBIDDEN);
		}

	}

	/**
	 * gets all allowed origins
	 * 
	 * @return a list of allowed origins
	 */
	public List<String> getAllowedOrigins() {
		if (config == null) {
			config = configurationLoader.load(CONFIG_NAME);
		}
		// Load pipe-separated list of allowed origins from config
		String[] origins = config.getProperty(CONFIG_ORIGINS_KEY).split("\\|");
		// Convert primitive array to list
		return Arrays.asList(origins);
	}
}
