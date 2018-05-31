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
public class Service7 implements Service7API{

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.di.Service7API#foo()
	 */
	@Override
	public void foo() {
		int i = 1 + 1;
	}

}
