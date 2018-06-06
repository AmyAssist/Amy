/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.unistuttgart.iaas.amyassist.amy.core.di;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
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
public class DependencyInjection implements ServiceLocator {

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

		this.addExternalService(ServiceLocator.class, this);
	}

	public synchronized void register(Class<?> cls) {
		Service annotation = cls.getAnnotation(Service.class);
		if (annotation == null)
			throw new ClassIsNotAServiceException(cls);
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
			if (this.hasServiceOfType(serviceType))
				throw new DuplicateServiceException();
		}

		if (!this.constructorCheck(cls))
			throw new RuntimeException("There is no default public constructor on class " + cls.getName());

		Field[] dependencyFields = FieldUtils.getFieldsWithAnnotation(cls, Reference.class);
		Set<Class<?>> dependencies = new HashSet<>();
		for (Field field : dependencyFields) {
			Class<?> dependency = field.getType();
			if (dependencies.contains(dependency)) {
				System.out.println(
						"The Service " + cls.getName() + " have a duplicate dependeny on " + dependency.getName());
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
	public synchronized void addExternalService(Class<?> serviceType, Object externalService) {
		if (this.hasServiceOfType(serviceType))
			throw new DuplicateServiceException();
		this.externalServices.put(serviceType, externalService);
	}

	private boolean hasServiceOfType(Class<?> serviceType) {
		if (this.register.containsKey(serviceType))
			return true;
		if (this.externalServices.containsKey(serviceType))
			return true;
		return false;
	}

	/**
	 * check if default constructor exists and is accessible
	 * 
	 * @param cls
	 * @return
	 */
	private boolean constructorCheck(Class<?> cls) {
		try {
			cls.getConstructor();
			return true;
		} catch (NoSuchMethodException | SecurityException e) {
			return false;
		}
	}

	@Override
	public <T> T getService(Class<T> serviceType) {
		return this.get(serviceType, new ArrayDeque<>(), this.instances);
	}

	private <T> T get(Class<T> serviceType, Deque<Class<?>> stack, Map<Class<?>, Object> resolved) {
		if (this.externalServices.containsKey(serviceType))
			return (T) this.externalServices.get(serviceType);

		Class<?> required = this.getRequired(serviceType);
		return (T) this.resolve(required, stack, resolved);
	}

	/**
	 * Resolve the dependency of a Service implementation and create an instance of
	 * the Service
	 * 
	 * @param serviceClass
	 *            the implementation class of a Service
	 * @return the instance of the Service implementation
	 */
	public <T> T resolve(Class<T> serviceClass) {
		return this.resolve(serviceClass, new ArrayDeque<>(), this.instances);
	}

	private <T> T resolve(Class<T> serviceClass, Deque<Class<?>> stack, Map<Class<?>, Object> resolved) {
		if (resolved.containsKey(serviceClass))
			return (T) resolved.get(serviceClass);
		if (stack.contains(serviceClass))
			throw new RuntimeException("circular dependencies");
		stack.push(serviceClass);

		try {
			T instance = serviceClass.newInstance();
			this.inject(instance, stack, resolved);
			this.postConstruct(instance);

			resolved.put(serviceClass, instance);
			stack.pop();
			return instance;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void inject(Object instance) {
		this.inject(instance, new ArrayDeque<>(), this.instances);
	}

	private void inject(Object instance, Deque<Class<?>> stack, Map<Class<?>, Object> resolved) {
		Field[] dependencyFields = FieldUtils.getFieldsWithAnnotation(instance.getClass(), Reference.class);
		for (Field field : dependencyFields) {
			Class<?> dependency = field.getType();
			Object object = this.get(dependency, stack, resolved);
			try {
				FieldUtils.writeField(field, instance, object, true);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void postConstruct(Object instance) {
		Method[] methodsWithAnnotation = MethodUtils.getMethodsWithAnnotation(instance.getClass(), PostConstruct.class);
		for (Method m : methodsWithAnnotation) {
			try {
				m.invoke(instance);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private Class<?> getRequired(Class<?> serviceType) {
		if (!this.register.containsKey(serviceType))
			throw new ServiceNotFoundException(serviceType);
		return this.register.get(serviceType);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator#create(java.lang.Class)
	 */
	@Override
	public <T> T create(Class<T> serviceClass) {
		return this.resolve(serviceClass);
	}
}
