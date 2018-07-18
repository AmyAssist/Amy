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

package de.unistuttgart.iaas.amyassist.amy.registry;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Objects;


/**
 * Location entity implementation
 * @author Benno Krauß
 */
@Entity
@PersistenceUnit(unitName="LocationRegistry")
public class LocationImpl implements Location {
    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private int persistentId;

    private String name;
    private String zipCode;
    private String city;
    private String street;
    private int houseNumber;
    private double longitude;
    private double latitude;
    private boolean isHome;
    private boolean isWork;

    public int getPersistentId() {
        return persistentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean home) {
        isHome = home;
    }

    public boolean isWork() {
        return isWork;
    }

    public void setWork(boolean work) {
        isWork = work;
    }

    @XmlTransient
    @Override
    public String getAddressString() {
        return String.format("%s %d, %s %s", getStreet(), getHouseNumber(), getZipCode(), getCity());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationImpl location = (LocationImpl) o;
        return persistentId == location.persistentId &&
                houseNumber == location.houseNumber &&
                Double.compare(location.longitude, longitude) == 0 &&
                Double.compare(location.latitude, latitude) == 0 &&
                isHome == location.isHome &&
                isWork == location.isWork &&
                Objects.equals(name, location.name) &&
                Objects.equals(zipCode, location.zipCode) &&
                Objects.equals(city, location.city) &&
                Objects.equals(street, location.street);
    }

    @Override
    public int hashCode() {

        return Objects.hash(persistentId, name, zipCode, city, street, houseNumber, longitude, latitude, isHome, isWork);
    }
}
