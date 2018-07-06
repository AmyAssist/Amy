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

package de.unistuttgart.iaas.amyassist.amy.core.registry.contact;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.Contact;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.ContactRegistry;
import de.unistuttgart.iaas.amyassist.amy.core.registry.AbstractRegistry;
import de.unistuttgart.iaas.amyassist.amy.core.registry.RegistryException;

import javax.annotation.Nonnull;

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

    @Nonnull
    @Override
    protected Class<? extends Contact> getEntityClass() {
        return ContactImpl.class;
    }

    public void testMyself() {
        Contact personA = new ContactImpl();
        personA.setEmail("a@b.c");
        personA.setFirstName("Max");
        personA.setLastName("Mustermann");
        personA.setImportant(true);

        Contact personB = new ContactImpl();
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
        if (!b) {
            throw new RegistryException("Error in test");
        }
    }
}
