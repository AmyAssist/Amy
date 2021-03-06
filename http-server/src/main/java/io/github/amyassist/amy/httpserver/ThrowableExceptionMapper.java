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

package io.github.amyassist.amy.httpserver;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception Mapper for all possible exceptions <br>
 * This class generates a 500 response for all exceptions,
 * but filters for WebAppilcationExecptions, so they can be used to return HTTP responses
 * with a different status
 *  
 * @author Christian Bräuner
 */
@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

	private final Logger logger = LoggerFactory.getLogger(ThrowableExceptionMapper.class);

	@Override
	public Response toResponse(Throwable t) {
		if (t instanceof WebApplicationException) {
			WebApplicationException webEx = (WebApplicationException) t;
			if(webEx.getResponse().getStatus() == 500) {
				this.logger.error("Internal server error while handling a request", webEx);
				//Important: WebApplications with different status don't get logged
				//they are used for building responses that are a expected behaviour
				//but have a different http status
			}
			ResponseBuilder rb = Response.status(webEx.getResponse().getStatus());
			String message = webEx.getLocalizedMessage();
			if (webEx.getCause() != null) {
				message = message + " caused by " + webEx.getCause().toString();
			}
			return rb.entity(message).type(MediaType.TEXT_PLAIN).build();
		}
		ResponseBuilder rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		String message = t.toString();
		this.logger.error("Error while HTTP Request on the Server", t);

		return rb.entity(message).type(MediaType.TEXT_PLAIN).build();
	}

}
