package de.unistuttgart.iaas.amyassist.amy.core.di;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.ClassProvider;

/**
 * Test Service for DI
 * 
 * @author Leon Kiefer
 */
@Service
public class Service9 {
	@Context(ClassProvider.class)
	private Class<?> consumerClass;
}
