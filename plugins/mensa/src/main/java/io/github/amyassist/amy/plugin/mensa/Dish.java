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

package io.github.amyassist.amy.plugin.mensa;

import java.util.Locale;

/**
 * Model class representing a dish
 *
 * @author Benno Krauß
 */
public class Dish {

    enum Category {
        VORSPEISE,
        HAUPTGERICHT,
        BEILAGE,
        DESSERT,
        BUFFET
    }

    private Category category;
    private String name;
    private float discountedPrice;
    private float regularPrice;

    public Dish(Category category, String name, float discountedPrice, float regularPrice) {
        this.category = category;
        this.name = name;
        this.discountedPrice = discountedPrice;
        this.regularPrice = regularPrice;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public float getDiscountedPrice() {
        return discountedPrice;
    }

    public float getRegularPrice() {
        return regularPrice;
    }

    @Override
    public String toString() {
        return "Dish{" +
            "category='" + category + '\'' +
            ", name='" + name + '\'' +
            ", discountedPrice=" + discountedPrice +
            ", regularPrice=" + regularPrice +
            '}';
    }

    public String humanReadableString() {
        return String.format(Locale.ROOT,"%s for €%.02f", this.name, this.discountedPrice);
    }
}
