/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.AnnotationReader;
import de.unistuttgart.iaas.amyassist.amy.core.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.IStorage;

/**
 * A Framework to test plugins
 * 
 * @author Leon Kiefer
 */
public class TestFramework {

	private IStorage storage;
	private ICore core;

	public TestFramework() {
		this.core = Mockito.mock(ICore.class);
		this.storage = Mockito.mock(IStorage.class);
		Mockito.when(this.core.getStorage()).thenReturn(this.storage);
	}

	public ICore core() {
		return this.core;
	}

	public IStorage storage(Consumer<IStorage>... modifiers) {
		for (Consumer<IStorage> modifier : modifiers) {
			modifier.accept(this.storage);
		}

		return this.storage;
	}

	/**
	 * Instantiate a plugin class with core components. Can only be used on
	 * SpeechCommend classes.
	 * 
	 * @param cls
	 *            the plugin class
	 * @return an instance of the given class
	 */
	public <T> T init(Class<T> cls) {
		AnnotationReader annotationReader = new AnnotationReader();
		String[] speechKeyword = annotationReader.getSpeechKeyword(cls);
		if (speechKeyword == null) {
			fail("The given class: " + cls.getName()
					+ " does not have a SpeechCommand Annotation");
			return null;
		}
		Method initMethod = annotationReader.getInitMethod(cls);

		try {
			T newInstance = cls.getConstructor().newInstance();

			if (initMethod != null) {
				initMethod.invoke(newInstance, this.core);
			}

			return newInstance;
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			fail("The given class: " + cls.getName()
					+ " is not managed by the framework and cant be initialized",
					e);
		}
		return null;
	}

	public static Consumer<IStorage> store(String key, String value) {
		return (storage) -> {
			Mockito.when(storage.has(key)).thenReturn(true);
			Mockito.when(storage.get(key)).thenReturn(value);
		};
	}
}
