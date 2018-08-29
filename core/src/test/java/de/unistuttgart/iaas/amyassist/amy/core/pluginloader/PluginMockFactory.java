package de.unistuttgart.iaas.amyassist.amy.core.pluginloader;

import java.nio.file.Path;
import java.util.function.Consumer;

import org.mockito.Mockito;

/**
 * Factory methods to create Plugin mocks with different properties. Minimal effort and maximal readable in the factory
 * user code.
 * 
 * @author Leon Kiefer
 */
public class PluginMockFactory {
	static IPlugin plugin() {
		return Mockito.mock(IPlugin.class);
	}

	public static IPlugin plugin(Path path) {
		IPlugin plugin = plugin();
		Mockito.when(plugin.getPath()).thenReturn(path);
		return plugin;
	}

	public static IPlugin plugin(ClassLoader classLoader) {
		IPlugin plugin = plugin();
		Mockito.when(plugin.getClassLoader()).thenReturn(classLoader);
		return plugin;
	}

	@SafeVarargs
	public static IPlugin plugin(Consumer<IPlugin>... modifiers) {
		IPlugin plugin = plugin();
		for (Consumer<IPlugin> modifier : modifiers) {
			modifier.accept(plugin);
		}

		return plugin;
	}

	public static Consumer<IPlugin> withUniqueName(String uniqueName) {
		return plugin -> Mockito.when(plugin.getUniqueName()).thenReturn(uniqueName);
	}
}
