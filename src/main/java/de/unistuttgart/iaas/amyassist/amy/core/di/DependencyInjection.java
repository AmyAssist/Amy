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

	protected Map<Class<?>, Object> externalServices;

	/**
	 * 
	 */
	public DependencyInjection() {
		this.register = new HashMap<>();
		this.dependencyRegister = new HashMap<>();
		this.instances = new HashMap<>();
		this.externalServices = new HashMap<>();
	}

	public synchronized void register(Class<?> cls) {
		Service annotation = cls.getAnnotation(Service.class);
		if (annotation == null) {
			throw new ClassIsNotAServiceException(cls);
		}
		Class<?>[] serviceTypes = annotation.value();
		if (serviceTypes.length == 0) {
			serviceTypes = cls.getInterfaces();
		}
		if (serviceTypes.length == 0) {
			serviceTypes = new Class[1];
			serviceTypes[0] = cls;
		}
		// TODO check if serviceType matches cls
		for (Class<?> serviceType : serviceTypes) {
			if (this.hasServiceOfType(serviceType)) {
				throw new DuplicateServiceException();
			}
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
		for (Class<?> serviceType : serviceTypes) {
			this.register.put(serviceType, cls);
		}
		this.dependencyRegister.put(cls, dependencies);
	}

	/**
	 * Adds an external Service instance to the DI. The DI does not manage the
	 * dependencies of the external Service, but the DI can inject the external
	 * Service as dependency into other managed services.
	 * 
	 * @param serviceType
	 * @param service
	 */
	public synchronized void addExternalService(Class<?> serviceType,
			Object externalService) {
		if (this.hasServiceOfType(serviceType)) {
			throw new DuplicateServiceException();
		}
		this.externalServices.put(serviceType, externalService);
	}

	private boolean hasServiceOfType(Class<?> serviceType) {
		if (this.register.containsKey(serviceType)) {
			return true;
		}
		if (this.externalServices.containsKey(serviceType)) {
			return true;
		}
		return false;
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

	/**
	 * Get a Service instance
	 * 
	 * @param serviceType
	 * @return
	 */
	public <T> T get(Class<T> serviceType) {
		return this.get(serviceType, new Stack<>(), this.instances);
	}

	private <T> T get(Class<T> serviceType, Stack<Class<?>> stack,
			Map<Class<?>, Object> resolved) {
		if (this.externalServices.containsKey(serviceType)) {
			return (T) this.externalServices.get(serviceType);
		}

		Class<?> required = this.getRequired(serviceType);
		return (T) this.resolve(required, stack, resolved);
	}

	/**
	 * Resolve the dependency of a Service implementation and create an instance
	 * of the Service
	 * 
	 * @param serviceClass
	 *            the implementation class of a Service
	 * @return the instance of the Service implementation
	 */
	public <T> T resolve(Class<T> serviceClass) {
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

	/**
	 * 
	 * @param instance
	 */
	public <T> void inject(T instance) {
		this.inject(instance, new Stack<>(), this.instances);
	}

	private <T> void inject(T instance, Stack<Class<?>> stack,
			Map<Class<?>, Object> resolved) {
		Field[] dependencyFields = FieldUtils
				.getFieldsWithAnnotation(instance.getClass(), Reference.class);
		for (Field field : dependencyFields) {
			Class<?> dependency = field.getType();
			Object object = this.get(dependency, stack, resolved);
			try {
				FieldUtils.writeField(field, instance, object, true);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private Class<?> getRequired(Class<?> serviceType) {
		if (!this.register.containsKey(serviceType)) {
			throw new ServiceNotFoundException(serviceType);
		}
		Class<?> required = this.register.get(serviceType);
		return required;
	}
}
