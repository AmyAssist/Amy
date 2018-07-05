package de.unistuttgart.iaas.amyassist.amy.core.registry.contact;

import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.Contact;

import javax.persistence.*;
import java.util.Objects;

/**
 * A contact entity for the contact registry
 *
 * @author Benno Krauß
 */
@Entity
@PersistenceUnit(unitName="ContactRegistry")
public class ContactImpl implements Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private int id;
    private String firstName;
    private String lastName;
    private boolean important;
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    /**
     * Generated equals implementation
     * @param o
     * @return areEqual
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return id == contact.getId() &&
                important == contact.isImportant() &&
                Objects.equals(firstName, contact.getFirstName()) &&
                Objects.equals(lastName, contact.getLastName()) &&
                Objects.equals(email, contact.getEmail());
    }

    /**
     * Generated hashCode implementation
     * @return
     */
    @Override
    public int hashCode() {

        return Objects.hash(id, firstName, lastName, important, email);
    }
}
