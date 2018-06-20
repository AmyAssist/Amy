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
import java.util.Set;

import javax.ws.rs.Path;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.httpserver.cors.CORSFilter;

/**
 * A class to create a http server
 * 
 * @author Christian Bräuner, Leon Kiefer
 */
@Service
public class Server {
	private final Logger logger = LoggerFactory.getLogger(Server.class);

	@Reference
	private ServiceLocator di;
	private Set<Class<?>> restResources = new HashSet<>();
	private HttpServer httpServer;

	public static final URI BASE_URI = URI.create("http://localhost:8080/rest");

	/**
	 * creates and starts the HttpServer
	 */
	public void start(Class<?>... classes) {
		if (this.httpServer != null) {
			throw new IllegalStateException("The Server is already started");
		}
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
		// java.util.logging.Logger.getLogger("org.glassfish.grizzly").setLevel(Level.WARNING);
		this.httpServer = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig);
	}

	/**
	 * shutdown the server if the server is running
	 */
	public void shutdown() {
		if (this.httpServer == null) {
			throw new IllegalStateException("The Server is not running");
		}
		this.logger.info("shutdown the server");
		this.httpServer.shutdownNow();
		this.httpServer = null;
	}

	/**
	 * @param cls
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
