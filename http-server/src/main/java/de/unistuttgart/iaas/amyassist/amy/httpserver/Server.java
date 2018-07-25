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

package de.unistuttgart.iaas.amyassist.amy.httpserver;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.httpserver.adapter.LocalDateTimeMessageBodyWriter;
import de.unistuttgart.iaas.amyassist.amy.httpserver.adapter.LocalDateTimeProvider;
import de.unistuttgart.iaas.amyassist.amy.httpserver.adapter.ZonedDateTimeMessageBodyWriter;
import de.unistuttgart.iaas.amyassist.amy.httpserver.adapter.ZonedDateTimeProvider;
import de.unistuttgart.iaas.amyassist.amy.httpserver.cors.CORSFilter;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * A class to create a http server
 * 
 * @author Christian Bräuner, Leon Kiefer, Tim Neumann
 */
@Service
public class Server {
	/** The name of the config used by this class */
	public static final String CONFIG_NAME = "server.config";
	/** The name of the property, which specifies the port */
	public static final String PROPERTY_PORT = "server.socket.port";
	/** The name of the property, which specifies the root path of the server */
	public static final String PROPERTY_CONTEXT_PATH = "server.socket.contextPath";
	/** The name of the property, which specifies whether the server should bind to local host only. */
	public static final String PROPERTY_LOCALHOST = "local_host_only";
	/** The name of the property, which specifies the url where the server is available. */
	public static final String PROPERTY_SERVER_URL = "server.url";

	/**
	 * The ip used, when the server should only be accessible from localhost
	 */
	public static final String IP_LOCAL = "127.0.0.1";
	/**
	 * The ip used, when the server should be accessible from anywhere
	 */
	public static final String IP_GLOBAL = "0.0.0.0";

	@Reference
	private Logger logger;

	/**
	 * The dependency injection instance, needed for binding the di to the server
	 */
	@Reference
	ServiceLocator di;

	private Set<Class<?>> restResources = new HashSet<>();
	private HttpServer httpServer;

	@Reference
	private ConfigurationLoader configurationLoader;

	/**
	 * @return the URI of the server
	 */
	private URI socketURI() {
		Properties conf = this.configurationLoader.load(CONFIG_NAME);
		int port = Integer.parseInt(conf.getProperty(PROPERTY_PORT, "8080"));
		String contextPath = conf.getProperty(PROPERTY_CONTEXT_PATH, "");
		String local = conf.getProperty(PROPERTY_LOCALHOST);

		if (contextPath.indexOf('/') != -1) {
			// see GrizzlyWebContainerFactory.create()
			this.logger.warn("Only first path segment will be used as context path, the rest will be ignored.");
		}
		if (contextPath.isEmpty()) {
			contextPath = "/";
		}

		if (local == null) {
			this.logger.warn("Server config missing key {}.", PROPERTY_LOCALHOST);
			local = "true";
		}

		if (Boolean.parseBoolean(local))
			return UriBuilder.fromPath(contextPath).scheme("http").host(IP_LOCAL).port(port).build();
		return UriBuilder.fromPath(contextPath).scheme("http").host(IP_GLOBAL).port(port).build();
	}

	/**
	 * creates and starts the HttpServer
	 * 
	 * @param classes
	 *            the resource classes
	 */
	public void start(Class<?>... classes) {
		if (this.httpServer != null)
			throw new IllegalStateException("The Server is already started");
		this.logger.info("start the server");

		ResourceConfig resourceConfig = new ResourceConfig(classes);
		resourceConfig.registerClasses(this.restResources);

		OpenApiResource openApiResource = new OpenApiResource();
		OpenAPI openapi = new OpenAPI();
		List<io.swagger.v3.oas.models.servers.Server> servers = Collections
				.singletonList(new io.swagger.v3.oas.models.servers.Server()
						.url(this.configurationLoader.load(CONFIG_NAME).getProperty(PROPERTY_SERVER_URL)));
		openapi.servers(servers);
		SwaggerConfiguration oasConfig = new SwaggerConfiguration().openAPI(openapi).prettyPrint(true)
				.scannerClass("io.swagger.v3.jaxrs2.integration.JaxrsApplicationScanner");
		openApiResource.openApiConfiguration(oasConfig);
		resourceConfig.register(openApiResource);

		resourceConfig.register(ThrowableExceptionMapper.class);
		resourceConfig.register(ZonedDateTimeProvider.class);
		resourceConfig.register(ZonedDateTimeMessageBodyWriter.class);
		resourceConfig.register(LocalDateTimeProvider.class);
		resourceConfig.register(LocalDateTimeMessageBodyWriter.class);
		resourceConfig.register(CORSFilter.class);
		resourceConfig.register(new AbstractBinder() {
			@Override
			protected void configure() {
				this.bind(new ServiceInjectionResolver(Server.this.di)).to(new TypeLiteral<Reference>() {
				});
			}
		});
		try {
			URI socketURI = this.socketURI();
			this.httpServer = GrizzlyWebContainerFactory.create(socketURI, new ServletContainer(resourceConfig), null,
					null);
		} catch (IOException e) {
			throw new IllegalStateException("The Server is can not be started", e);
		}
	}

	/**
	 * shutdown the server if the server is running
	 */
	public void shutdown() {
		if (this.httpServer == null)
			throw new IllegalStateException("The Server is not running");
		this.logger.info("shutdown the server");
		this.httpServer.shutdownNow();
		this.httpServer = null;
	}

	/**
	 * Checks whether the server is running
	 * 
	 * @return whether the server is running
	 */
	public boolean isRunning() {
		return (this.httpServer != null);
	}

	/**
	 * @param cls
	 *            a resource class
	 */
	public void register(Class<?> cls) {
		if (!cls.isAnnotationPresent(Path.class)) {
			this.logger.error("can't register class {}, because it dont have the Path annotation", cls);
			throw new IllegalArgumentException();
		}

		if (this.httpServer != null)
			throw new IllegalStateException("The Server is already started");
		this.restResources.add(cls);
	}
}
