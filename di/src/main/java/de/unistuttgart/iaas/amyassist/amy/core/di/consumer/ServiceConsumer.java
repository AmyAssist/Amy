package de.unistuttgart.iaas.amyassist.amy.core.di.consumer;

/**
 * A Service Consumer
 * 
 * @author Leon Kiefer
 */
public interface ServiceConsumer {
	default Class<?> getConsumerClass() {
		return null;
	}
}
