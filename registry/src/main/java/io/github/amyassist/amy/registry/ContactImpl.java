/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 *
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information see notice.md
 */

package io.github.amyassist.amy.registry;

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
    private int persistentId;
    private String firstName;
    private String lastName;
    private boolean important;
    private String email;
    private String tag;

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

    public int getPersistentId() {
        return persistentId;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Generated equals implementation
     * @param o object to be compared
     * @return areEqual
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return persistentId == contact.getPersistentId() &&
                important == contact.isImportant() &&
                Objects.equals(firstName, contact.getFirstName()) &&
                Objects.equals(lastName, contact.getLastName()) &&
                Objects.equals(email, contact.getEmail()) &&
                Objects.equals(tag, contact.getTag());
    }

    /**
     * Generated hashCode implementation
     * @return hash value
     */
    @Override
    public int hashCode() {

        return Objects.hash(persistentId, firstName, lastName, important, email, tag);
    }
}
