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

package io.github.amyassist.amy.httpserver.cors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import io.github.amyassist.amy.core.configuration.ConfigurationManager;
import io.github.amyassist.amy.core.di.annotation.Reference;

/**
 * a filter for CORS requests and OPTIONS calls "https://www.w3.org/TR/cors/#resource-requests"
 * 
 * @author Christian Bräuner, Benno Krauß, Leon Kiefer
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
	public ConfigurationManager configurationManager;
	private Properties config;

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		String origin = getOrigin(requestContext);
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
		String origin = getOrigin(requestContext);
		if (origin != null) {
			this.checkOrigin(requestContext, origin);
			if (requestContext.getMethod().equalsIgnoreCase(OPTIONS)
					&& requestContext.getHeaderString(Headers.XOPTIONS) == null) {
				preflight(requestContext, origin);
			}
		}
	}

	private String getOrigin(ContainerRequestContext requestContext) {
		return requestContext.getHeaderString(Headers.ORIGIN);
	}

	private void preflight(ContainerRequestContext requestContext, String origin) {
		ResponseBuilder builder = Response.ok();
		builder.header(Headers.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
		builder.header(Headers.ACCESS_CONTROL_ALLOW_HEADERS, ALLOWED_HEADERS);
		builder.header(Headers.ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS);
		builder.header(Headers.VARY, Headers.ORIGIN);

		requestContext.abortWith(builder.build());
	}

	private void checkOrigin(ContainerRequestContext requestContext, @Nonnull String origin) {
		if (getAllowedOrigins().contains("*") || getAllowedOrigins().contains(origin) || origin.matches(LOCALHOST_REGEX)) {
			return;
		}
		requestContext.setProperty(ACCESS_DENIED, Boolean.TRUE);
		throw new WebApplicationException(origin + " not allowed", Status.FORBIDDEN);
	}

	/**
	 * gets all allowed origins
	 * 
	 * @return a list of allowed origins
	 */
	public List<String> getAllowedOrigins() {
		if (this.config == null) {
			this.config = this.configurationManager.getConfigurationWithDefaults(CONFIG_NAME);
		}

		List<String> originsList = new ArrayList<>();
		if (this.config.getProperty(CONFIG_ORIGINS_KEY) != null) {
			// Load pipe-separated list of allowed origins from config
			String[] origins = this.config.getProperty(CONFIG_ORIGINS_KEY).split("\\|");
			// Convert primitive array to list
			originsList = Arrays.asList(origins);
		}
		return originsList;
	}
}
