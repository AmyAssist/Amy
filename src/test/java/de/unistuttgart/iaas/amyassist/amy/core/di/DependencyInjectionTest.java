/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.di;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Test for DependencyInjection
 * 
 * @author Leon Kiefer
 */
class DependencyInjectionTest {

	private DependencyInjection dependencyInjection;

	@BeforeEach
	public void setup() {
		this.dependencyInjection = new DependencyInjection();
		this.dependencyInjection.register(Service1.class);
		this.dependencyInjection.register(Service2.class);
		this.dependencyInjection.register(Service3.class);
	}

	@Test
	void testServiceAnnotation() {
		Service1 service1 = this.dependencyInjection.get(Service1.class);
		assertThat(service1, is(instanceOf(Service1.class)));
	}

	@Test
	void testDependencyInjection() {
		Service2 service2 = this.dependencyInjection.get(Service2.class);
		assertThat(service2.checkServices(), is(true));
	}

	@Test
	void testCircularDependencies() {
		this.dependencyInjection.register(Service4.class);
		this.dependencyInjection.register(Service5.class);

		assertThrows(RuntimeException.class,
				() -> this.dependencyInjection.get(Service4.class));
	}

	@Test()
	void testRegisterNotAService() {
		String message = assertThrows(ClassIsNotAServiceException.class,
				() -> this.dependencyInjection.register(NotAService.class))
						.getMessage();

		assertThat(message, equalTo("The class " + NotAService.class.getName()
				+ " is not a Service"));
	}

}
