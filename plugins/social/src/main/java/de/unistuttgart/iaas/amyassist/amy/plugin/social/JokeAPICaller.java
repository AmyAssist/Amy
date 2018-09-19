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

package de.unistuttgart.iaas.amyassist.amy.plugin.social;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

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
		try {
			URL url = new URL(jokeURL);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.setRequestProperty("Accept", "application/json");

			if (connection.getResponseCode() != 200) {
				return "Calling API failed";
			}

			StringBuilder sb = new StringBuilder();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String s;
				while ((s = br.readLine()) != null) {
					sb.append(s);
				}
			}
			final String response = sb.toString();
			if (response.startsWith("{")) {
				JsonValue json = Json.parse(response);
				return json.asObject().get("joke").toString();
			}
			return response;

		} catch (MalformedURLException e) {
			this.logger.error("URL malformed", e);
			return "URL malformed";
		} catch (IOException e) {
			this.logger.error("Couldn't set up connection", e);
			return "Couldn't set up connection";
		}
	}

	@PostConstruct
	private void init() {
		this.jokeAPIs = new ArrayList<>();
		this.jokeAPIs.add(DAD_JOKES_API);
	}
}
