/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.di;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * Dependency Injection Used to manage dependencies and Service instantiation at
 * runtime. A Service that relies on DI is completely passive when it comes to
 * its runtime dependencies. There is no code in the Service that creates,
 * instantiates or gets the dependencies. The dependencies are injected into the
 * Service before the Service is executed. This reversal of responsibility to
 * instantiate (or ask for instantiate of) a dependency is called Inversion of
 * Control (IoC). This leads to loose coupling, because the Service doesn't need
 * to know about how the dependency is implemented.
 * 
 * @author Leon Kiefer
 */
public class DependencyInjection {

	private Map<Class<?>, Class<?>> register;

	private Map<Class<?>, Set<Class<?>>> dependencyRegister;

	/**
	 * 
	 */
	public DependencyInjection() {
		this.register = new HashMap<>();
		this.dependencyRegister = new HashMap<>();
	}

	public void register(Class<?> cls) {
		Service annotation = cls.getAnnotation(Service.class);
		if (annotation == null) {
			throw new ClassIsNotAServiceException(cls);
		}
		Class<?> serviceType = annotation.value();
		// TODO check if serviceType matches cls
		if (this.register.containsKey(serviceType)) {
			throw new DuplicateServiceException();
		}

		if (!this.constructorCheck(cls)) {
			throw new RuntimeException(
					"There is no default public constructor on class "
							+ cls.getName());
		}

		Field[] dependencyFields = FieldUtils.getFieldsWithAnnotation(cls,
				Reference.class);
		Set<Class<?>> dependencies = new HashSet<>();
		for (Field field : dependencyFields) {
			Class<?> dependency = field.getType();
			if (dependencies.contains(dependency)) {
				System.out.println("The Service " + cls.getName()
						+ " have a duplicate dependeny on "
						+ dependency.getName());
			} else {
				dependencies.add(dependency);
			}
		}

		this.register.put(serviceType, cls);
		this.dependencyRegister.put(cls, dependencies);
	}

	/**
	 * check if default constructor exists and is accessible
	 * 
	 * @param cls
	 * @return
	 */
	private boolean constructorCheck(Class<?> cls) {
		return true;
	}

	public <T> T get(Class<T> serviceType) {
		if (!this.register.containsKey(serviceType)) {
			throw new ServiceNotFoundException(serviceType);
		}
		Class<?> class1 = this.register.get(serviceType);
		return (T) this.resolve(class1);
	}

	private <T> T resolve(Class<T> serviceClass) {
		return this.resolve(serviceClass, new HashSet<>(), new HashMap<>());
	}

	private <T> T resolve(Class<T> serviceClass, Set<Class<?>> t,
			Map<Class<?>, Object> resolved) {
		if (resolved.containsKey(serviceClass)) {
			return (T) resolved.get(serviceClass);
		}
		if (t.contains(serviceClass)) {
			throw new RuntimeException("circular dependencies");
		}
		t.add(serviceClass);

		try {
			T instance = (T) serviceClass.newInstance();
			Field[] dependencyFields = FieldUtils
					.getFieldsWithAnnotation(serviceClass, Reference.class);
			for (Field field : dependencyFields) {
				Class<?> dependency = field.getType();
				if (!this.register.containsKey(dependency)) {
					throw new ServiceNotFoundException(dependency);
				}
				Class<?> required = this.register.get(dependency);
				FieldUtils.writeField(field, instance,
						this.resolve(required, t, resolved), true);
			}

			resolved.put(serviceClass, instance);
			return instance;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
