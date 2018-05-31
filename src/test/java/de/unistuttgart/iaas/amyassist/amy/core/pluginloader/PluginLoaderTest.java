/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.pluginloader;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for PluginLoader
 * 
 * @author Leon Kiefer
 */
class PluginLoaderTest {

	private PluginLoader pluginLoader;

	@BeforeEach
	void setup() {
		this.pluginLoader = new PluginLoader();
	}

	@Test
	void test() {
		this.pluginLoader.loadPlugin("de.unistuttgart.iaas.amyassist.amy.plugin.example", "amy.plugin.example",
				"de.unistuttgart.iaas.amyassist", "0.0.1");
		assertThat(this.pluginLoader.getPlugins(), hasSize(1));
		assertThat(this.pluginLoader.getPlugin("de.unistuttgart.iaas.amyassist.amy.plugin.example"), notNullValue());
	}

	@Test
	void testClasses() {
		this.pluginLoader.loadPlugin("de.unistuttgart.iaas.amyassist.amy.plugin.example", "amy.plugin.example",
				"de.unistuttgart.iaas.amyassist", "0.0.1");
		Plugin plugin = this.pluginLoader.getPlugin("de.unistuttgart.iaas.amyassist.amy.plugin.example");
		assertThat(plugin.getClasses(), is(not(empty())));
	}
}
