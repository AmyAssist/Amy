/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.example;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import de.unistuttgart.iaas.amyassist.amy.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.core.IStorage;

/**
 * A Test for the Hello World Plugin
 * 
 * @author Leon Kiefer
 */
@ExtendWith(MockitoExtension.class)
public class HelloWorldTest {
	private TestFramework testFramework;

	@BeforeEach
	public void setup() {
		this.testFramework = new TestFramework();
		this.testFramework.register(HelloWorldSpeech.class);
		this.testFramework.register(HelloWorldLogic.class);
	}

	@Test
	public void testcount() {

		IStorage storage = this.testFramework
				.storage(TestFramework.store("hellocount", "10"));

		HelloWorldLogic helloWorldLogic = this.testFramework
				.get(HelloWorldLogic.class);

		assertThat(helloWorldLogic.helloWorld(), equalTo("hello11"));

		Mockito.verify(storage).put("hellocount", "11");
	}
}
