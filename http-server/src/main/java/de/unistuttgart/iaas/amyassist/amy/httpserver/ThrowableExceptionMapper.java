/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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

package de.unistuttgart.iaas.amyassist.amy.httpserver;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

	private final Logger logger = LoggerFactory
			.getLogger(ThrowableExceptionMapper.class);

	@Override
	public Response toResponse(Throwable t) {
		if (t instanceof WebApplicationException) {
			WebApplicationException webEx = (WebApplicationException) t;
			ResponseBuilder rb = Response.status(webEx.getResponse().getStatus());
			String message = webEx.getLocalizedMessage();
			if(webEx.getCause() != null) {
				message = message + " caused by " + webEx.getCause().toString();
			}
			return rb.entity(message).type(MediaType.TEXT_PLAIN).build();
		}
		ResponseBuilder rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		String message = t.toString();
		this.logger.error("Error while HTTP Request on the Server", t);
		// TODO change later only for debugging/ testing
		try {
			message = message + " " + t.getStackTrace()[0].toString();
		} catch (Exception e) {
			// no stacktrace
		}
		Response r = rb.entity(message).type(MediaType.TEXT_PLAIN).build();
		return r;
	}

}
