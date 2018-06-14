package de.unistuttgart.iaas.amyassist.amy.core.di.provider;

import java.lang.reflect.Field;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Context;

/**
 * A ContextInjectionPoint is an InjectionPoint where the context is injected.
 * 
 * @author Leon Kiefer
 */
public class ContextInjectionPoint extends InjetionPoint {

	private Class<?> contextProviderType;

	/**
	 * @return the contextProviderType
	 */
	public Class<?> getContextProviderType() {
		return this.contextProviderType;
	}

	public ContextInjectionPoint(Field field) {
		super(field);
		Context[] annotations = field.getAnnotationsByType(Context.class);
		if (annotations.length > 1) {
			throw new IllegalArgumentException("In the service " + field.getDeclaringClass().getName() + " the field "
					+ field.getName() + " has the annotation @Context multiple times.");
		}
		Context context = annotations[0];
		this.contextProviderType = context.value();
	}

}
