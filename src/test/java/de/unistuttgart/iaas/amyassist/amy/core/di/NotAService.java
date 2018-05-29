/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.di;

/**
 * Test Service for DI
 * 
 * @author Leon Kiefer
 */
public class NotAService {
	private Service4 service4;

	public NotAService(Service4 service4) {
		this.service4 = service4;
	}
}
