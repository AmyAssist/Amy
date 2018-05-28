/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.di;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * Test Service for DI
 * 
 * @author Leon Kiefer
 */
@Service(Service2.class)
public class Service2 {
	@Reference
	private Service1 service1;

	@Reference
	private Service3 service3;

	public boolean checkServices() {
		return this.service1 != null && this.service3 != null;
	}
}
