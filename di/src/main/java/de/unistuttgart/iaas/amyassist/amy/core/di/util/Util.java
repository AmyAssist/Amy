package de.unistuttgart.iaas.amyassist.amy.core.di.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;

/**
 * Util for checks and java reflection
 * 
 * @author Leon Kiefer
 */
public class Util {
	private Util() {
		// hide constructor
	}

	private static final Logger logger = LoggerFactory.getLogger(Util.class);

	/**
	 * Checks if the given class can be used as a Service. There for it must be
	 * a not abstract class with a default constructor.
	 * 
	 * @param cls
	 * @return
	 */
	public static boolean classCheck(@Nonnull Class<?> cls) {
		return constructorCheck(cls) && !cls.isArray() && !cls.isInterface()
				&& !Modifier.isAbstract(cls.getModifiers());
	}

	/**
	 * check if default constructor exists and is accessible
	 * 
	 * @param cls
	 *            The class to check
	 * @return Whether the default constructor is present.
	 */
	public static boolean constructorCheck(@Nonnull Class<?> cls) {
		try {
			cls.getConstructor();
			return true;
		} catch (NoSuchMethodException | SecurityException e) {
			return false;
		}
	}

	public static void postConstruct(@Nonnull Object instance) {
		Method[] methodsWithAnnotation = MethodUtils.getMethodsWithAnnotation(instance.getClass(), PostConstruct.class);
		for (Method m : methodsWithAnnotation) {
			try {
				m.invoke(instance);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.error("tryed to invoke method {} but got an error", m, e);
			}
		}
	}

	public static void inject(@Nonnull Object instance, @Nullable Object object, @Nonnull Field field) {
		if (object != null && !field.getType().isAssignableFrom(object.getClass())) {
			throw new IllegalArgumentException(
					"the object doesn't have the correct type to be assigable to the given field. The object is of type "
							+ object.getClass() + " and the field of " + field.getType());
		}

		try {
			FieldUtils.writeField(field, instance, object, true);
		} catch (IllegalAccessException e) {
			logger.error("tryed to inject the dependency {} into {} but failed", object, instance, e);
		}
	}
}
