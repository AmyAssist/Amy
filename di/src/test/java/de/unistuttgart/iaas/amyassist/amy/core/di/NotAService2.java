/*
 * amy-di
 *
 * TODO: Project Beschreibung
 *
 * @author Tim Neumann
 * @version 1.0.0
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.di;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * Test service for DI
 * 
 * @author Tim Neumann
 */
public class NotAService2 {
	@Reference
	private Service1 s1;

	public int getInit() {
		return this.s1.init;
	}
}
