/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.httpserver;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Path;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * A class to create a http server
 * 
 * @author Christian Bräuner, Leon Kiefer
 */
@Service
public class Server {
	@Reference
	private ServiceLocator di;
	private Set<Class<?>> restResources = new HashSet<>();
	private HttpServer server;

	public static final URI BASE_URI = URI.create("http://localhost:8080/rest");

	/**
	 * creates and starts the HttpServer
	 */
	public void start(Class<?>... classes) {
		if (this.server != null) {
			throw new IllegalStateException("The Server is already started");
		}

		ResourceConfig resourceConfig = new ResourceConfig(classes);
		resourceConfig.registerClasses(this.restResources);
		resourceConfig.register(ThrowableExceptionMapper.class);
		resourceConfig.register(new AbstractBinder() {
			@Override
			protected void configure() {
				this.bind(new ServiceInjectionResolver(Server.this.di)).to(new TypeLiteral<Reference>() {
				});
			}
		});
		Logger.getLogger("org.glassfish.grizzly").setLevel(Level.WARNING);
		this.server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig);
	}

	/**
	 * shutdown the server if the server is running
	 */
	public void shutdown() {
		if (this.server == null) {
			throw new IllegalStateException("The Server is not running");
		}

		this.server.shutdownNow();
		this.server = null;
	}

	/**
	 * @param cls
	 */
	public void register(Class<?> cls) {
		if (!cls.isAnnotationPresent(Path.class))
			throw new RuntimeException();
		this.restResources.add(cls);
	}

}
