package de.unistuttgart.iaas.amyassist.amy.httpserver;

import static java.time.Duration.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;

/**
 * 
 * @author Leon Kiefer
 */
class ServerTest {

	private DependencyInjection dependencyInjection;

	@BeforeEach
	void setup() {
		this.dependencyInjection = new DependencyInjection();
	}

	@Test
	void test() {
		assertTimeout(ofSeconds(2), () -> {
			Server server = this.dependencyInjection.create(Server.class);
			server.start(TestRestResource.class);
			server.shutdown();
		}, "The Server start and shotdown takes longer then 2 Seconds");

	}

	@Test
	void testCantStartServerTwice() {
		Server server = this.dependencyInjection.create(Server.class);
		String message = assertThrows(IllegalStateException.class, () -> {
			server.start(TestRestResource.class);
			server.start(TestRestResource.class);
		}, "The Server dont throw an IllegalStateException if its started twice").getMessage();
		server.shutdown();
		assertThat(message, equalTo("The Server is already started"));
	}

	@Test
	void testRegister() {
		Server server = this.dependencyInjection.create(Server.class);
		server.register(TestRestResource.class);
		server.start();
		server.shutdown();
	}

	@Test
	void testRegisterNonResourceClass() {
		Server server = this.dependencyInjection.create(Server.class);
		assertThrows(IllegalArgumentException.class, () -> {
			server.register(ServerTest.class);
		}, "The Server dont throw an IllegalArgumentException if a registered class is not a Rest Resource");
	}
}
