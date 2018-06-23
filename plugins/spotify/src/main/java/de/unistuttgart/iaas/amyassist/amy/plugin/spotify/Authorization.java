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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import java.util.Scanner;

/**
 * This class is only needed once for the frist authorization. This class will be removed soon.
 * 
 * @author Lars Buttgereit
 */
public class Authorization {

	/**
	 * needed for first init. It takes until the UI can control the authorization process. Probably after Sprint 2.
	 * Please follow the instruction the instruction in the console
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		SpotifyAPICalls spotifyAPICalls = new SpotifyAPICalls();
		System.out.println("copy this link in your browser an follow the login process".concat("\n")
				.concat(spotifyAPICalls.authorizationCodeUri().toString()).concat("\n")
				.concat("Please insert Auth Code form the result link. Copy all between 'code=' and '&'.".concat(
						" For example for this result: http://localhost:8888/?code=AQComIDO...qHvVw&state=TEST)")
						.concat("you copy only this: 'AQComIDO...qHvVw'")));
		spotifyAPICalls.createRefreshToken(sc.nextLine());
		System.out.println("complete");
		sc.close();
	}

}
