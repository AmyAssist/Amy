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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.PluginProvider;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Tests the dependency injection scopes.
 * 
 * @author Tim Neumann
 */
public class DependencyInjectionScopeTest {
	private DependencyInjection dependencyInjection;

	/**
	 * Setup
	 */
	@BeforeEach
	public void setup() {
		this.dependencyInjection = new DependencyInjection();
	}

	// Scope GLOBAL and once are already being tested by the other test.

	/**
	 * Scope DI for scope class.
	 */
	@Test
	public void testScopeClass() {
		this.dependencyInjection.register(Service11.class);
		this.dependencyInjection.register(Service12.class);
		this.dependencyInjection.register(Service13.class);

		Service11 s1 = this.dependencyInjection.getService(Service11.class);
		s1.s.id = 4;

		Service13 s2 = this.dependencyInjection.getService(Service13.class);
		assertThat(s1.s, not(theInstance(s2.s1)));
		assertThat(s1, theInstance(s2.s2));
		assertThat(s1.s, theInstance(s2.s2.s));
	}

	/**
	 * Scope DI for scope plugin.
	 */
	@Test
	public void testScopePlugin() {
		this.dependencyInjection.register(Service14.class);
		this.dependencyInjection.register(Service15.class);
		this.dependencyInjection.register(Service16.class);
		this.dependencyInjection.register(ServiceForPlugins.class);

		ArrayList<IPlugin> plugins = new ArrayList<>();
		this.dependencyInjection.registerContextProvider(PluginProvider.class, new PluginProvider(plugins));

		Class<?> cls1 = Service14.class;
		Class<?> cls2 = Service15.class;
		Class<?> cls3 = Service16.class;

		ArrayList<Class<?>> classes1 = new ArrayList<>();
		classes1.add(cls1);
		classes1.add(cls2);
		plugins.add(new TestPlugin(classes1));

		ArrayList<Class<?>> classes2 = new ArrayList<>();
		classes2.add(cls3);
		plugins.add(new TestPlugin(classes2));

		Service14 s1 = this.dependencyInjection.getService(Service14.class);
		Service15 s2 = this.dependencyInjection.getService(Service15.class);

		Service16 s3 = this.dependencyInjection.getService(Service16.class);

		assertThat(s1.s, theInstance(s2.s));
		assertThat(s1.s, not(theInstance(s3.s)));
	}

	@Test
	public void testContextValue() {
		this.dependencyInjection.register(Service11.class);
		this.dependencyInjection.register(Service12.class);
		this.dependencyInjection.register(Service13.class);

		Service11 s1 = this.dependencyInjection.getService(Service11.class);
		assertThat(s1.s.getConsumerClass(), notNullValue());
		assertThat(s1.s.getConsumerClass(), equalTo(Service11.class));
	}

	@Test
	public void testNoContextProvider() {
		this.dependencyInjection.register(Service16.class);
		this.dependencyInjection.register(ServiceForPlugins.class);

		String message = assertThrows(NoSuchElementException.class,
				() -> this.dependencyInjection.getService(Service16.class)).getMessage();
		assertThat(message, equalTo(PluginProvider.class.getName()));
	}

	/**
	 * Clear loggers
	 */
	@SuppressWarnings("static-method")
	@AfterEach
	public void clearLoggers() {
		TestLoggerFactory.clear();
	}
}
