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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for parsing the mensa html and retrieving the dishes
 * This relies on the proprietary html format, obviously.
 *
 * It has to be used in conjunction with an SAXParser.
 * After the parsing, the result can be accessed by using the getMeals-method
 *
 * @author Benno Krauß
 */
public class XmlHandler extends DefaultHandler {

    public List<Dish> getMeals() {
        return result;
    }

    private List<Dish> result = new ArrayList<>();

    private String category = null;
    private boolean isCategory = false;

    private boolean isDish = false;

    private String name = null;
    private float discountedPrice = Float.NEGATIVE_INFINITY;

    private boolean priceNext = false;

    /**
     * Whether an xml element with these attributes is kind of a CSS class className
     * @param attributes attributes object from xml parser
     * @param className css class name
     * @return true if it is member of this css class
     */
    private boolean isMemberOfClass(Attributes attributes, String className) {

        String classNames = attributes.getValue("class");
        return Arrays.asList((classNames != null ? classNames : " ").split(" ")).contains(className);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (isMemberOfClass(attributes, "gruppenname")) {
            isCategory = true;
        }

        if (isMemberOfClass(attributes, "splMeal")) {
            isDish = true;
        }

    }

    @SuppressWarnings("squid:S1244")
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        String content = new String(ch, start, length).trim();

        if (isCategory) {
            category = content;
            isCategory = false;
        }

        if (isDish && content.length() > 0) {
            name = content;
            isDish = false;
        }

        if (content.contains("€") && name != null) {
            priceNext = true;
        }

        if (priceNext) {
            try {
                // Remove non-breaking spaces and replace decimal separator with dot
                String contentForFloatParsing = content.replaceAll("(^\\h*)|(\\h*$)","")
                    .replaceAll(",", ".");

                float f = Float.parseFloat(contentForFloatParsing);

                if (discountedPrice == Float.NEGATIVE_INFINITY) {
                    discountedPrice = f;
                } else {
                    // now f is the regular price
                    result.add(new Dish(Dish.Category.valueOf(category), name, discountedPrice, f));
                    discountedPrice = Float.NEGATIVE_INFINITY;
                    priceNext = false;
                    name = null;
                }

            } catch (NumberFormatException e) {
                // do nothing
            }
        }
    }
}
