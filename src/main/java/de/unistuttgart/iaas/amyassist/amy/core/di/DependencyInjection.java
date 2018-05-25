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
import java.util.Stack;

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

	protected Map<Class<?>, Class<?>> register;

	protected Map<Class<?>, Set<Class<?>>> dependencyRegister;

	protected Map<Class<?>, Object> instances;

	/**
	 * 
	 */
	public DependencyInjection() {
		this.register = new HashMap<>();
		this.dependencyRegister = new HashMap<>();
		this.instances = new HashMap<>();
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
		Class<?> class1 = this.getRequired(serviceType);
		return (T) this.resolve(class1);
	}

	private <T> T resolve(Class<T> serviceClass) {
		return this.resolve(serviceClass, new Stack<>(), this.instances);
	}

	private <T> T resolve(Class<T> serviceClass, Stack<Class<?>> stack,
			Map<Class<?>, Object> resolved) {
		if (resolved.containsKey(serviceClass)) {
			return (T) resolved.get(serviceClass);
		}
		if (stack.contains(serviceClass)) {
			throw new RuntimeException("circular dependencies");
		}
		stack.push(serviceClass);

		try {
			T instance = serviceClass.newInstance();
			this.inject(instance, stack, resolved);

			resolved.put(serviceClass, instance);
			stack.pop();
			return instance;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> void inject(T instance) {
		this.inject(instance, new Stack<>(), this.instances);
	}

	private <T> void inject(T instance, Stack<Class<?>> stack,
			Map<Class<?>, Object> resolved) {
		Field[] dependencyFields = FieldUtils
				.getFieldsWithAnnotation(instance.getClass(), Reference.class);
		for (Field field : dependencyFields) {
			Class<?> dependency = field.getType();
			Class<?> required = this.getRequired(dependency);
			try {
				FieldUtils.writeField(field, instance,
						this.resolve(required, stack, resolved), true);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private Class<?> getRequired(Class<?> dependency) {
		if (!this.register.containsKey(dependency)) {
			throw new ServiceNotFoundException(dependency);
		}
		Class<?> required = this.register.get(dependency);
		return required;
	}
}
