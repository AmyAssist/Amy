/*
 * amy-di
 *
 * TODO: Project Beschreibung
 *
 * @author Tim Neumann
 * @version 1.0.0
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.di;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Tests the dependency injection scopes.
 * 
 * @author Tim Neumann
 */
public class DependencyInjectionScopeTest {
	private DependencyInjection dependencyInjection;

	@BeforeEach
	public void setup() {
		this.dependencyInjection = new DependencyInjection();
	}

	@AfterEach
	public void clearLoggers() {
		TestLoggerFactory.clear();
	}
}
