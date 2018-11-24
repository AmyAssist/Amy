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

package de.unistuttgart.iaas.amyassist.amy.plugin.mensa;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl;
import org.slf4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;

/**
 * Mensa logic class
 * 
 * @author Benno Krauß
 */
@Service
public class MensaLogic {

	private static final String HOST = "https://sws2.maxmanager.xyz/inc/ajax-php_konnektor.inc.php";

	@Reference
	private Logger logger;

	List<Dish> getDishes(LocalDate date) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		LocalDate previousMonday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate nextMonday = date.plusDays(7);

		try {
			Content c = Request.Post(HOST)
				.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
				.bodyForm(
					Form.form()
						.add("func", "make_spl")
						.add("locId", "2")
						.add("date", date.format(formatter))
						.add("lang", "de")
						.add("startThisWeek", previousMonday.format(formatter))
						.add("startNextWeek", nextMonday.format(formatter))
						.build()
				).execute().returnContent();

			String xml = c.asString();

			SAXParser parser = new SAXFactoryImpl().newSAXParser();
			XmlHandler handler = new XmlHandler();
			parser.parse(new ByteArrayInputStream((xml).getBytes(StandardCharsets.UTF_8)), handler);

			return handler.getMeals();
		} catch (IOException | ParserConfigurationException | SAXException e) {
			logger.error("Couldn't get meals from mensa: ", e);
			return Collections.emptyList();
		}
	}
}
