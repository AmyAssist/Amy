package de.unistuttgart.iaas.amyassist.amy.plugin.example.registry;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.registry.AbstractRegistry;

import javax.annotation.Nonnull;

/**
 * Custom registry for testing
 *
 * @author Benno Krauß
 */
@Service(ColorRegistry.class)
public class ColorRegistry extends AbstractRegistry<ColorEntity> {

    @Override
    protected String getPersistenceUnitName() {
        return "ColorRegistry";
    }

    @Nonnull
    @Override
    protected Class<? extends ColorEntity> getEntityClass() {
        return ColorEntity.class;
    }
}
