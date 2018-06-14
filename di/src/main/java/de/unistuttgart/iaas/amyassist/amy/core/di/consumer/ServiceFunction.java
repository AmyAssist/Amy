package de.unistuttgart.iaas.amyassist.amy.core.di.consumer;

import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider;

/**
 * A combination of ServiceConsumer and ServiceProvider
 * 
 * @author Leon Kiefer
 */
public interface ServiceFunction<T> extends ServiceConsumer, ServiceProvider<T> {

}
