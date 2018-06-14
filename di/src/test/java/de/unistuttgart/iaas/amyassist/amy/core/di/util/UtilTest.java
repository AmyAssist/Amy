package de.unistuttgart.iaas.amyassist.amy.core.di.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.di.Service1;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceWithConstructor;

/**
 * Tests for the DI Util
 * 
 * @author Leon Kiefer
 */
class UtilTest {

	@Test()
	void testConstructorCheck() {
		assertThat(Util.constructorCheck(ServiceWithConstructor.class), is(false));
		assertThat(Util.constructorCheck(Service1.class), is(true));
	}

}
