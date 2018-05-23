/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.rest;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

import de.unistuttgart.iaas.amyassist.amy.rest.resource.HelloWorldResource;

/**
 * A class to create a http server
 * 
 * @author Christian Bräuner
 */
public class Server {

	public static final URI BASE_URI = URI.create("http://localhost:8080/rest");
	
	/**
	 * creates and starts the HttpServer
	 * 
	 * @return the started HttpServer
	 */
	public static HttpServer start() {
		Map<String, String> initParams = new HashMap<>();
		initParams.put(
				ServerProperties.PROVIDER_PACKAGES,
				HelloWorldResource.class.getPackage().getName());
		HttpServer server = null;
		try {
			server = GrizzlyWebContainerFactory.create(BASE_URI, ServletContainer.class, initParams);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return server;
	}

}
