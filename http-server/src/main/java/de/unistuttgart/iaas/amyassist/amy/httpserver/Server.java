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

import java.net.URI;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.httpserver.cors.CORSFilter;

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
	public static final String PROPERTY_PORT = "port";
	/** The name of the property, which specifies the root path of the server */
	public static final String PROPERTY_ROOT_PATH = "root_path";
	/** The name of the property, which specifies whether the server should bind to local host only. */
	public static final String PROPERTY_LOCALHOST = "local_host_only";

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
	private ConfigurationLoader configuration_loader;

	/**
	 * @return the URI of the server
	 */
	private URI baseURI() {
		Properties conf = this.configuration_loader.load(CONFIG_NAME);
		int port = Integer.parseInt(conf.getProperty(PROPERTY_PORT, "8080"));
		String root = conf.getProperty(PROPERTY_ROOT_PATH);
		String local = conf.getProperty(PROPERTY_LOCALHOST);

		if (root == null) {
			this.logger.warn("Server config missing key {}.", PROPERTY_ROOT_PATH);
			root = "rest";
		}

		if (local == null) {
			this.logger.warn("Server config missing key {}.", PROPERTY_LOCALHOST);
			local = "true";
		}

		if (local.equals("true"))
			return UriBuilder.fromPath(root).scheme("http").host(IP_LOCAL).port(port).build();
		return UriBuilder.fromPath(root).scheme("http").host("0.0.0.0").port(port).build();
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
		resourceConfig.register(ThrowableExceptionMapper.class);
		resourceConfig.register(CORSFilter.class);
		resourceConfig.register(new AbstractBinder() {
			@Override
			protected void configure() {
				this.bind(new ServiceInjectionResolver(Server.this.di)).to(new TypeLiteral<Reference>() {
				});
			}
		});
		this.httpServer = GrizzlyHttpServerFactory.createHttpServer(this.baseURI(), resourceConfig);
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
