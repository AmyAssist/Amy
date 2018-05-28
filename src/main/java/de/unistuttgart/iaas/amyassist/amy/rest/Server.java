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

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * A class to create a http server
 * 
 * @author Christian Bräuner, Leon Kiefer
 */
public class Server {

	DependencyInjection di;

	public Server(DependencyInjection di) {
		this.di = di;
	}

	public static final URI BASE_URI = URI.create("http://localhost:8080/rest");

   /**
	 * creates and starts the HttpServer
	 * 
	 * @return the started HttpServer
	 */
	public HttpServer start(Class<?>... classes) {
		ResourceConfig resourceConfig = new ResourceConfig(classes);
		resourceConfig.register(new AbstractBinder() {
			@Override
			protected void configure() {
				this.bind(new ServiceInjectionResolver(Server.this.di))
						.to(new TypeLiteral<Reference>() {
						});
			}
		});
		HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI,
				resourceConfig);
		return server;
	}

}
