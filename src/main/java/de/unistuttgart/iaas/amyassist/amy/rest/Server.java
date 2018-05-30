/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.rest;

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
