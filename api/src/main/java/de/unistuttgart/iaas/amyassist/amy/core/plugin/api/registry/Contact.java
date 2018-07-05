package de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry;

import javax.persistence.*;
import java.util.Objects;

/**
 * A contact entity for the contact registry
 *
 * @author Benno Krauß
 */
public interface Contact {

    public String getFirstName();

    public void setFirstName(String firstName);

    public String getLastName();

    public void setLastName(String lastName);

    public boolean isImportant();

    public void setImportant(boolean important);

    public String getEmail();

    public void setEmail(String email);

    public int getId();
}
