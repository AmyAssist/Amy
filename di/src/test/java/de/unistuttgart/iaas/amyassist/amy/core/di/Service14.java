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
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Scope;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * A test service for DependencyInjectionScopeTest
 * 
 * @author Tim Neumann
 */
@Service
public class Service14 {
	@Reference(Scope.PLUGIN)
	Service12 s;
}
