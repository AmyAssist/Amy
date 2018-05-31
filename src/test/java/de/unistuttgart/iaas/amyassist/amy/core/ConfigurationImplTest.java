/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginLoader;

/**
 * Tests for the ConfigurationImpl
 * 
 * @author Leon Kiefer
 */
@ExtendWith(FrameworkExtention.class)
class ConfigurationImplTest {

	@Reference
	private TestFramework framework;
	private ConfigurationImpl configurationImpl;

	@BeforeEach
	public void setup() {
		PluginLoader mockService = this.framework.mockService(PluginLoader.class);
		Set<String> hashSet = new HashSet<>();
		hashSet.add("Plugin1");
		hashSet.add("Plugin2");
		hashSet.add("de.unistuttgart.iaas.amyassist.amy.plugin.example");
		Mockito.when(mockService.getPluginNames()).thenReturn(hashSet);

		this.configurationImpl = this.framework.setServiceUnderTest(ConfigurationImpl.class);
	}

	@Test
	void test() {
		assertThat(this.configurationImpl.getInstalledPlugins(),
				arrayContainingInAnyOrder("Plugin1", "Plugin2", "de.unistuttgart.iaas.amyassist.amy.plugin.example"));
		assertThat(this.configurationImpl.getInstalledPlugins(), arrayWithSize(3));
	}

}
