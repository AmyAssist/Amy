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

package de.unistuttgart.iaas.amyassist.amy.plugin.example.registry;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;
import java.util.Objects;

/**
 * Custom entity for testing
 *
 * @author Benno Krauß
 */
@Entity
@PersistenceUnit(unitName="ColorRegistry")
public class ColorEntity {
    @Id
    @GeneratedValue
    private int id;

    private float redComponent;
    private float greenComponent;
    private float blueComponent;

    public int getId() {
        return id;
    }

    public float getRedComponent() {
        return redComponent;
    }

    public void setRedComponent(float redComponent) {
        this.redComponent = redComponent;
    }

    public float getGreenComponent() {
        return greenComponent;
    }

    public void setGreenComponent(float greenComponent) {
        this.greenComponent = greenComponent;
    }

    public float getBlueComponent() {
        return blueComponent;
    }

    public void setBlueComponent(float blueComponent) {
        this.blueComponent = blueComponent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorEntity that = (ColorEntity) o;
        return id == that.id &&
                Float.compare(that.redComponent, redComponent) == 0 &&
                Float.compare(that.greenComponent, greenComponent) == 0 &&
                Float.compare(that.blueComponent, blueComponent) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, redComponent, greenComponent, blueComponent);
    }
}
