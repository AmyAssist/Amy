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
import java.util.*;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;
import de.unistuttgart.iaas.amyassist.amy.httpserver.adapter.LocalDateTimeMessageBodyWriter;
import de.unistuttgart.iaas.amyassist.amy.httpserver.adapter.LocalDateTimeProvider;
import de.unistuttgart.iaas.amyassist.amy.httpserver.adapter.ZonedDateTimeMessageBodyWriter;
import de.unistuttgart.iaas.amyassist.amy.httpserver.adapter.ZonedDateTimeProvider;
import de.unistuttgart.iaas.amyassist.amy.httpserver.cors.CORSFilter;
import de.unistuttgart.iaas.amyassist.amy.httpserver.di.DependencyInjectionBinder;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * A class to create a http server
 * 
 * @author Christian Bräuner, Leon Kiefer, Tim Neumann
 */
@Service(Server.class)
public class ServerImpl implements RunnableService, Server {
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
	private ServiceLocator di;

	private Set<Class<?>> restResources = new HashSet<>();
	private HttpServer httpServer;

	@Reference
	private ConfigurationManager configurationManager;

	private String serverURL;
	private int serverPort;
	private String contextPath;
	private boolean local;

	@PostConstruct
	private void init() {
		Properties conf = this.configurationManager.getConfigurationWithDefaults(CONFIG_NAME);
		this.serverURL = conf.getProperty(PROPERTY_SERVER_URL);
		this.serverPort = Integer.parseInt(conf.getProperty(PROPERTY_PORT));
		this.contextPath = conf.getProperty(PROPERTY_CONTEXT_PATH);
		this.local = Boolean.parseBoolean(conf.getProperty(PROPERTY_LOCALHOST));
	}

	@Override
	public URI getSocketUri() {

		if (this.contextPath.indexOf('/') != -1) {
			// see GrizzlyWebContainerFactory.create()
			this.logger.warn("Only first path segment will be used as context path, the rest will be ignored.");
		}
		if (this.contextPath.isEmpty()) {
			this.contextPath = "/";
		}

		String ip = this.local ? IP_LOCAL : IP_GLOBAL;
		return UriBuilder.fromPath(this.contextPath).scheme("http").host(ip).port(this.serverPort).build();
	}

	@Override
	public void start() {
		this.startWithResources();
	}

	@Override
	public void startWithResources(Class<?>... classes) {
		if (this.httpServer != null)
			throw new IllegalStateException("The Server is already started");
		this.logger.info("start the server");

		ResourceConfig resourceConfig = new ResourceConfig(classes);
		resourceConfig.registerClasses(this.restResources);

		OpenApiResource openApiResource = new OpenApiResource();
		OpenAPI openapi = new OpenAPI();

		if (this.serverURL.isEmpty()) {
			this.logger.warn("Missing public server URL."
					+ " The Url is required to create links to the server itself, that are valid in the global scope.");
		} else {
			List<io.swagger.v3.oas.models.servers.Server> servers = Collections
					.singletonList(new io.swagger.v3.oas.models.servers.Server().url(this.serverURL));
			openapi.servers(servers);
		}

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
		resourceConfig.register(new DependencyInjectionBinder(this.di));
		try {
			URI socketURI = this.getSocketUri();
			this.httpServer = GrizzlyWebContainerFactory.create(socketURI, new ServletContainer(resourceConfig), null,
					null);
		} catch (IOException e) {
			throw new IllegalStateException("The Server is can not be started", e);
		}
		this.logger.info("started the server");
	}

	@Override
	public void stop() {
		if (this.httpServer == null)
			throw new IllegalStateException("The Server is not running");
		this.logger.info("shutdown the server");
		this.httpServer.shutdownNow();
		this.httpServer = null;
	}

	@Override
	public boolean isRunning() {
		return (this.httpServer != null);
	}

	@Override
	public void register(Class<?> cls) {
		if (!cls.isAnnotationPresent(Path.class)) {
			this.logger.error("can't register class {}, because it dont have the Path annotation", cls);
			throw new IllegalArgumentException();
		}

		if (this.httpServer != null)
			throw new IllegalStateException("The Server is already started");
		this.restResources.add(cls);
	}

	@Override
	public String getBaseUrl() {
		return this.serverURL;
	}
}
