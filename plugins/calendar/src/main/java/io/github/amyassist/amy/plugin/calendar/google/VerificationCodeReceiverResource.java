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

package io.github.amyassist.amy.plugin.calendar.google;

import java.util.UUID;

import javax.ws.rs.*;
import javax.ws.rs.core.Response.Status;

import io.github.amyassist.amy.core.di.annotation.Reference;

/**
 * REST Resource for calendar verification code from google
 *
 * @author Leon Kiefer
 */
@Path("calendar/verification")
public class VerificationCodeReceiverResource {

	@Reference
	private VerificationCodeReceiverService service;

	/**
	 * Get the verificationCode after google oauth.
	 * 
	 * @param id
	 *            the unique id of the verification code request
	 * @param code
	 *            the verification code
	 * @param error
	 *            error or null
	 * @return landing page
	 */
	@GET
	@Path("code/{id}")
	public String verificationCode(@PathParam("id") UUID id, @QueryParam("code") String code,
			@QueryParam("error") String error) {
		if (error != null) {
			this.service.setVerificationCode(id, code);
			throw new WebApplicationException("Request contains error: " + error, Status.BAD_REQUEST);
		}
		this.service.setVerificationCode(id, code);
		return "OK";
	}

}
