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

package io.github.amyassist.amy.plugin.social;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;

import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;

/**
 * This class is responsible for calling the RESTful Joke APIs
 * 
 * @author Patrick Singer
 */
@Service
public class JokeAPICaller {

	private static final String DAD_JOKES_API = "https://icanhazdadjoke.com";

	private List<String> jokeAPIs;

	@Reference
	private Logger logger;

	/**
	 * Get a joke from one of the given APIs (random)
	 * 
	 * @return a random joke
	 */
	protected String getRandomJoke() {
		Random r = new Random();
		final int apiIndex = r.nextInt(this.jokeAPIs.size());
		return callJokeAPI(this.jokeAPIs.get(apiIndex));
	}

	private String callJokeAPI(String jokeURL) {
		WebTarget target = ClientBuilder.newClient().target(UriBuilder.fromPath(jokeURL));
		try (Response response = target.request(MediaType.APPLICATION_JSON).get()) {
			if (response.getStatus() != 200) {
				return "Calling API failed";
			}

			final String responseString = response.readEntity(String.class);

			if (responseString.startsWith("{")) {
				JsonValue json = Json.parse(responseString);
				return json.asObject().get("joke").toString().replaceAll("\"", "");
			}
			return responseString;
		}

	}

	@PostConstruct
	private void init() {
		this.jokeAPIs = new ArrayList<>();
		this.jokeAPIs.add(DAD_JOKES_API);
	}
}
