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

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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
     * @param sink sse event sink
     * @param sse sse object
     */
    @GET
    @Path("eventstream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void eventStream(@Context SseEventSink sink, @Context Sse sse) {

        SSEClient newClient = new SSEClient(sink, sse);
        sr.setClient(newClient);

    }

    /**
     * endpoint to supply the static sr website for chrome
     * @return static html website
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getStaticWebsite() {
        try {
            URL file = getClass().getClassLoader().getResource("index.html");
            if (file != null) {
                URI filePath = file.toURI();
                return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            } else {
                throw new IOException("Couldn't get URL for resource file index.html");
            }
        } catch (URISyntaxException | IOException e) {
            throw new WebApplicationException("Couldn't load static html file: ", e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void postSRResult(SRResult res) {
        sr.processResult(res);
    }
}
