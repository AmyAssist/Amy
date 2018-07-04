package de.unistuttgart.iaas.amyassist.amy.core.registry.contact;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.Contact;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.ContactRegistry;
import de.unistuttgart.iaas.amyassist.amy.core.registry.AbstractRegistry;

/**
 * A contact registry
 *
 * @author Benno Krauß
 */
@Service(ContactRegistry.class)
public class ContactRegistryImpl extends AbstractRegistry<Contact> implements ContactRegistry {

    @Override
    protected String getPersistenceUnitName() {
        return "ContactRegistry";
    }

    public void testMyself() {
        Contact personA = new Contact();
        personA.setEmail("a@b.c");
        personA.setFirstName("Max");
        personA.setLastName("Mustermann");
        personA.setImportant(true);

        Contact personB = new Contact();
        personB.setEmail("b@b.com");
        personB.setFirstName("Alice");
        personB.setLastName("Musterfrau");
        personB.setImportant(true);

        assertTrue(personA.getId() == 0);

        this.save(personA);
        this.save(personB);

        assertTrue(personA.getId() != personB.getId());
        int personAId = personA.getId();

        Contact personA2 = this.getById(personA.getId());
        assertTrue(personA.equals(personA2));

        this.deleteById(personA.getId());
        this.deleteById(personB.getId());

        Contact c3 = this.getById(personAId);
        assertTrue(c3 == null);
    }

    private void assertTrue(boolean b) {
        if (b != true) {
            throw new RuntimeException("Error in test");
        }
    }
}
