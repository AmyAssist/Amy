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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import de.unistuttgart.iaas.amyassist.amy.core.AnnotationReader;
import de.unistuttgart.iaas.amyassist.amy.core.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.IStorage;

/**
 * A Test for the Hello World Plugin
 * 
 * @author Leon Kiefer
 */
@ExtendWith(MockitoExtension.class)
public class HelloWorldTest {

	@Mock
	private IStorage storage;

	@Mock
	private ICore core;

	/**
	 * Test
	 */
	@Test
	public void test() {
		assertThat(new AnnotationReader().getSpeechKeyword(HelloWorldSpeech.class),
				equalTo(new String[] { "Hello world" }));
	}

	@Test
	public void testcount() {
		Mockito.when(this.core.getStorage()).thenReturn(this.storage);

		Mockito.when(this.storage.has("hellocount")).thenReturn(true);
		Mockito.when(this.storage.get("hellocount")).thenReturn("10");

		HelloWorldLogic helloWorldLogic = new HelloWorldLogic();
		assertThat(this.core, notNullValue());
		helloWorldLogic.init(this.core);

		assertThat(helloWorldLogic.helloWorld(), equalTo("hello11"));

		Mockito.verify(this.storage).put("hellocount", "11");
	}
}
