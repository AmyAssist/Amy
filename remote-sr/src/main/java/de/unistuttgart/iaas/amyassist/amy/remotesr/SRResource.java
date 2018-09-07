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

package de.unistuttgart.iaas.amyassist.amy.remotesr;

import java.io.InputStream;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * SSE resource for the remote SR. Chrome connects to this resource and stays connected indefinitely
 *
 * @author Benno Krauß
 */
@Path("remotesr")
public class SRResource {

	@Reference
	private RemoteSR sr;

	/**
	 * SSE endpoint for the chrome instance
	 * 
	 * @param sink
	 *            sse event sink
	 * @param sse
	 *            sse object
	 */
	@GET
	@Path("eventstream")
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void eventStream(@Context SseEventSink sink, @Context Sse sse) {

		SSEClient newClient = new SSEClient(sink, sse);
		this.sr.setClient(newClient);

	}

	/**
	 * endpoint to supply the static sr website for chrome
	 * 
	 * @return static html website
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	@SuppressWarnings("resource")
	public Response getStaticWebsite() {
		InputStream is = getClass().getClassLoader().getResourceAsStream("index.html");
		if (is == null)
			throw new WebApplicationException("Couldn't get input stream for file index.html",
					Response.Status.INTERNAL_SERVER_ERROR);
		// The input stream is kept open on purpose. Jax-rs will close the stream after it's done reading
		// from it
		return Response.ok(is).build();

	}

	/**
	 * Endpoint to which the java script posts the speech recognition results
	 * 
	 * @param res
	 *            The result recognized.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void postSRResult(SRResult res) {
		this.sr.processResult(res);
	}
}
