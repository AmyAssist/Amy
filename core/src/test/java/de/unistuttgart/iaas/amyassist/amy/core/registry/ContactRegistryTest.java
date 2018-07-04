package de.unistuttgart.iaas.amyassist.amy.core.registry;

import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.Contact;
import de.unistuttgart.iaas.amyassist.amy.core.registry.contact.ContactRegistry;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class ContactRegistryTest {

    ContactRegistry cr = new ContactRegistry();

    //@Test
    public void testSingleContact() {
        Contact c = new Contact();
        c.setEmail("a@b.c");
        c.setFirstName("Max");
        c.setLastName("Mustermann");
        c.setImportant(true);

        assertThat(c.getId(), equalTo(0));

        cr.save(c);

        assertThat(c.getId(), is(not(0)));
        int id = c.getId();

        Contact c2 = cr.getById(c.getId());
        assertThat(c2, equalTo(c));

        cr.deleteById(c.getId());

        Contact c3 = cr.getById(id);
        assertThat(c3, is(nullValue()));
    }

}
