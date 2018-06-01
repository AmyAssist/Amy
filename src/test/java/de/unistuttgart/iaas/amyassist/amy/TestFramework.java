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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.ws.rs.Path;

import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.AnnotationReader;
import de.unistuttgart.iaas.amyassist.amy.core.GlobalStorage;
import de.unistuttgart.iaas.amyassist.amy.core.Storage;
import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.rest.Server;

/**
 * A Framework to test plugins
 * 
 * @author Leon Kiefer
 */
public class TestFramework {

	private IStorage storage;
	private DependencyInjection dependencyInjection;
	private Server server;
	private List<Class<?>> restResources = new ArrayList<>();

	public TestFramework() {
		this.storage = Mockito.mock(Storage.class, Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS)
				.useConstructor("", new GlobalStorage()));
		this.dependencyInjection = new DependencyInjection();
		this.dependencyInjection.addExternalService(TestFramework.class, this);
		this.dependencyInjection.addExternalService(IStorage.class, this.storage);
		this.dependencyInjection.register(Server.class);
	}

	void setup(Object testInstance) {
		this.dependencyInjection.inject(testInstance);
	}

	public void before() {
		if (!this.restResources.isEmpty()) {
			this.server = this.dependencyInjection.getService(Server.class);
			this.server.start(this.restResources.toArray(new Class<?>[this.restResources.size()]));
		}

	}

	public void after() {
		if (this.server != null) {
			this.server.shutdown();
		}
	}

	@Service(ICore.class)
	class Core implements ICore {

		@Reference
		private IStorage storage;

		/**
		 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.ICore#getStorage()
		 */
		@Override
		public IStorage getStorage() {
			return this.storage;
		}

	}

	public IStorage storage(Consumer<IStorage>... modifiers) {
		for (Consumer<IStorage> modifier : modifiers) {
			modifier.accept(this.storage);
		}

		return this.storage;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection#register(Class)
	 */
	public void register(Class<?> cls) {
		this.dependencyInjection.register(cls);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection#getService(Class)
	 */
	public <T> T get(Class<T> serviceType) {
		return this.dependencyInjection.getService(serviceType);
	}

	/**
	 * Instantiate a plugin class with core components. Can only be used on
	 * SpeechCommend classes.
	 * 
	 * @param cls
	 *            the plugin class
	 * @return an instance of the given class
	 */
	@Deprecated
	public <T> T init(Class<T> cls) {
		AnnotationReader annotationReader = new AnnotationReader();
		String[] speechKeyword = annotationReader.getSpeechKeyword(cls);
		Method initMethod = annotationReader.getInitMethod(cls);

		try {
			T newInstance;
			if (cls.isAnnotationPresent(Service.class)) {
				newInstance = this.dependencyInjection.getService(cls);
			} else {
				newInstance = cls.getConstructor().newInstance();
			}

			if (initMethod != null) {
				initMethod.invoke(newInstance, this.dependencyInjection.getService(ICore.class));
			}

			return newInstance;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			fail("The given class: " + cls.getName() + " is not managed by the framework and cant be initialized", e);
		}
		return null;
	}

	public static Consumer<IStorage> store(String key, String value) {
		return (storage) -> {
			Mockito.when(storage.has(key)).thenReturn(true);
			Mockito.when(storage.get(key)).thenReturn(value);
		};
	}

	/**
	 * create a mock for the serviceType and bind it in the DI.
	 * 
	 * @param serviceType
	 * @return the service mock
	 */
	public <T> T mockService(Class<T> serviceType) {
		T mock = Mockito.mock(serviceType);
		this.dependencyInjection.addExternalService(serviceType, mock);
		return mock;
	}

	/**
	 * specify the Service Under Test
	 * 
	 * @param serviceClass
	 *            the class to be tested
	 * @return the ServiceUnderTest
	 */
	public <T> T setServiceUnderTest(Class<T> serviceClass) {
		if (serviceClass.isAnnotationPresent(Service.class)) {
			this.dependencyInjection.register(serviceClass);
			return this.dependencyInjection.create(serviceClass);
		}
		throw new RuntimeException();
	}

	/**
	 * specify the Rest resource
	 * 
	 * @param resource
	 *            the class of the Rest resource
	 */
	public void setRESTResource(Class<?> resource) {
		if (resource.isAnnotationPresent(Path.class)) {
			this.restResources.add(resource);
		} else {
			throw new RuntimeException();
		}
	}

}
