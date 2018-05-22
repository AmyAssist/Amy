package de.unistuttgart.iaas.amyassist.amy.plugin.example;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.AnnotationReader;

public class HelloWorldTest {

	@Test
	public void test() {
		assertThat(new AnnotationReader().getSpeechKeyword(HelloWorld.class), equalTo("Hello world"));
	}
}
