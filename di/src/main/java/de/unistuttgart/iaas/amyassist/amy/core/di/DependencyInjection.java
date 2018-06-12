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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Scope;
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
 * @author Leon Kiefer, Tim Neumann
 */
public class DependencyInjection implements ServiceLocator {

	/**
	 * The logger of the DI
	 */
	protected final Logger logger = LoggerFactory.getLogger(DependencyInjection.class);

	/**
	 * A register which maps a class to it's service type.
	 */
	protected Map<Class<?>, Class<?>> register;

	/**
	 * A register which maps a class to it's dependencies, for each dependency
	 * also noting the scope.
	 */
	protected Map<Class<?>, Map<Class<?>, Scope>> dependencyRegister;

	/**
	 * The map of global instances for each service class
	 */
	protected Map<Class<?>, Object> global_instances;

	/**
	 * The map of class instances for a class
	 */
	protected Map<Class<?>, Map<Class<?>, Object>> class_instances;

	/**
	 * The map of class instances for a plugin
	 */
	protected Map<IPlugin, Map<Class<?>, Object>> plugin_instances;

	/**
	 * The map of external services
	 */
	protected Map<Class<?>, Object> externalServices;

	private List<IPlugin> plugins;

	/**
	 * Creates a new Dependency Injection
	 */
	public DependencyInjection() {
		this.register = new HashMap<>();
		this.dependencyRegister = new HashMap<>();
		this.global_instances = new HashMap<>();
		this.externalServices = new HashMap<>();
		this.class_instances = new HashMap<>();
		this.plugin_instances = new HashMap<>();

		this.addExternalService(ServiceLocator.class, this);
	}

	/**
	 * Set's the internal list of plugins, which the DI needs to be able to
	 * understand the scope plugin.
	 * 
	 * @param p_plugins
	 *            The list of plugins
	 */
	public void setPlugins(List<IPlugin> p_plugins) {
		this.plugins = p_plugins;
	}

	/**
	 * Registers a service
	 * 
	 * @param cls
	 *            The service to register.
	 */
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
		Map<Class<?>, Scope> dependencies = new HashMap<>();
		for (Field field : dependencyFields) {
			Class<?> dependency = field.getType();
			if (dependencies.containsKey(dependency)) {
				this.logger.warn("The Service {} have a duplicate dependeny on {}", cls.getName(),
						dependency.getName());
			} else {
				Reference[] annotations = field.getAnnotationsByType(Reference.class);
				if (annotations.length > 1) {
					this.logger.warn("In the service {} the field {} has the annotation @Reference multiple times."
							+ "Only respecting the first.", cls.getName(), field.getName());
				}
				dependencies.put(dependency, annotations[0].value());
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
	 *            The type of this service
	 * @param externalService
	 *            The instance of this service
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
	 *            The class to check
	 * @return Whether the default constructor is present.
	 */
	@SuppressWarnings("static-method")
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
		return this.get(serviceType, new ArrayDeque<>(), new ScopeInformation(Scope.GLOBAL));
	}

	/**
	 * Get the service of the given type, considering the stack of classes, for
	 * which this service is needed. This method can return external services
	 * and does return a object of the type registered for the service type.
	 * 
	 * @param <T>
	 *            The type of the service.
	 * @param serviceType
	 *            The class of the service type.
	 * @param stack
	 *            The stack of classes, for which this service is needed.
	 * @return The instance of the service.
	 */
	private <T> T get(Class<T> serviceType, Deque<Class<?>> stack, ScopeInformation scope) {
		if (this.externalServices.containsKey(serviceType)) {
			if (scope.getScope() == Scope.GLOBAL)
				return (T) this.externalServices.get(serviceType);
		}
		Class<?> required = this.getRequired(serviceType);
		return (T) this.resolve(required, stack, scope);
	}

	/**
	 * Resolve a class considering the stack of classes, for which this class is
	 * needed. This method does not return external services and can only return
	 * a instance of the exact type
	 * 
	 * @param <T>
	 *            The type of the class
	 * @param serviceClass
	 *            The class of the type.
	 * @param stack
	 *            The stack of classes, for which this class is needed.
	 * @return The instance of the class.
	 */
	private <T> T resolve(Class<T> serviceClass, Deque<Class<?>> stack, ScopeInformation scope) {
		Map<Class<?>, Object> resolved = new HashMap<>();
		if (scope.getScope() == Scope.GLOBAL) {
			resolved = this.global_instances;
		}

		if (scope.getScope() == Scope.PLUGIN) {
			if (!this.plugin_instances.containsKey(scope.getPlugin())) {
				this.plugin_instances.put(scope.getPlugin(), new HashMap<>());
			}
			resolved = this.plugin_instances.get(scope.getPlugin());
		}

		if (scope.getScope() == Scope.CLASS) {
			if (!this.class_instances.containsKey(scope.getCls())) {
				this.class_instances.put(scope.getCls(), new HashMap<>());
			}
			resolved = this.class_instances.get(scope.getCls());
		}

		if (resolved.containsKey(serviceClass))
			return (T) resolved.get(serviceClass);

		if (stack.contains(serviceClass))
			throw new RuntimeException("circular dependencies");

		stack.push(serviceClass);

		try {
			T instance = serviceClass.newInstance();
			this.inject(instance, stack);
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
		this.inject(instance, new ArrayDeque<>());
	}

	private void inject(Object instance, Deque<Class<?>> stack) {
		Field[] dependencyFields = FieldUtils.getFieldsWithAnnotation(instance.getClass(), Reference.class);
		for (Field field : dependencyFields) {
			Class<?> dependency = field.getType();
			Reference[] annotations = field.getAnnotationsByType(Reference.class);
			if (annotations.length > 1) {
				this.logger.warn("In the class {} the field {} has the annotation @Reference multiple times."
						+ "Only respecting the first.", instance.getClass().getName(), field.getName());
			}
			Scope s = annotations[0].value();
			ScopeInformation si = null;
			switch (s) {
			case CLASS:
				si = new ScopeInformation(instance.getClass());
				break;
			case PLUGIN:
				IPlugin p = this.getPluginFromClass(instance.getClass());
				if (p == null) {
					si = new ScopeInformation(instance.getClass());
				} else {
					si = new ScopeInformation(p);
				}
				break;
			default:
				si = new ScopeInformation(s);
				break;
			}
			Object object = this.get(dependency, stack, si);
			try {
				FieldUtils.writeField(field, instance, object, true);
			} catch (IllegalAccessException e) {
				this.logger.error("tryed to inject the dependency {} into {} but failed", object, instance, e);
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
				this.logger.error("tryed to invoke method {} but got an error", m, e);
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
		Deque<Class<?>> stack = new ArrayDeque<>();
		stack.push(serviceClass);
		try {
			T instance = serviceClass.newInstance();
			this.inject(instance, stack);
			this.postConstruct(instance);

			stack.pop();
			return instance;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private IPlugin getPluginFromClass(Class<?> cls) {
		for (IPlugin p : this.plugins) {
			if (p.getClasses().contains(cls))
				return p;
		}
		this.logger.error("The class {} does not seem to belong to any plugin. Falling back to class scope.",
				cls.getName());
		return null;
	}
}
