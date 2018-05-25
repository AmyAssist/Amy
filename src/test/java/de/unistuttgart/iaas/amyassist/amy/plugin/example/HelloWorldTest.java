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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import de.unistuttgart.iaas.amyassist.amy.FramworkExtention;
import de.unistuttgart.iaas.amyassist.amy.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.core.IStorage;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * A Test for the Hello World Plugin
 * 
 * @author Leon Kiefer
 */
@ExtendWith({ MockitoExtension.class, FramworkExtention.class })
public class HelloWorldTest {
	@Reference
	private TestFramework testFramework;
	@Reference
	private HelloWorldLogic helloWorldLogic;

	@Test
	public void testcount() {

		IStorage storage = this.testFramework
				.storage(TestFramework.store("hellocount", "10"));

		assertThat(this.helloWorldLogic.helloWorld(), equalTo("hello11"));

		Mockito.verify(storage).put("hellocount", "11");
	}
}
