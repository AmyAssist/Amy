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

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.natlang.EntityData;
import io.github.amyassist.amy.core.natlang.Intent;
import io.github.amyassist.amy.core.natlang.SpeechCommand;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mensa plugin
 * 
 * @author Benno Krauß
 */
@SpeechCommand
public class MensaSpeech {
	
	@Reference
	private MensaLogic logic;
	
	@Intent
	public String getFood(Map<String, EntityData> entities) {

		LocalDate date = LocalDate.now();
		String timeString = "the";

		EntityData data = entities.get("amydate");
		if (data != null) {
			date = data.getDate();
			if (data.getString().equals("today") || data.getString().equals("tomorrow"))
				timeString = data.getString() + "'s";
		}

		List<Dish> dishes = logic.getDishes(date);

		if (dishes.isEmpty()) {
			return "I don't know about the lunch. Maybe try again later.";
		} else {
			StringBuilder r = new StringBuilder();
			r.append("Here's ");
			r.append(timeString);
			r.append(" lunch menu for the cafeteria. These are the main dishes: \n");
			String mainDishes = dishes.stream().filter(d -> d.getCategory() == Dish.Category.HAUPTGERICHT)
				.map(Dish::humanReadableString).collect(Collectors.joining(". \n"));
			r.append(mainDishes);

			return r.toString();
		}
	}
}
