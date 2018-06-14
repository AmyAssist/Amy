package de.unistuttgart.iaas.amyassist.amy.core.di.provider;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.unistuttgart.iaas.amyassist.amy.core.di.util.Util;

/**
 * A InjectionPoint is an abstraction of where an object is injected into an
 * instance.
 * 
 * @author Leon Kiefer
 */
class InjetionPoint {
	private Field field;

	public InjetionPoint(Field field) {
		this.field = field;
	}

	/**
	 * 
	 * @return
	 */
	public Class<?> getType() {
		return this.field.getType();
	}

	public void inject(@Nonnull Object instance, @Nullable Object object) {
		Util.inject(instance, object, this.field);
	}
}