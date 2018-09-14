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

package de.unistuttgart.iaas.amyassist.amy.core.natlang;

/**
 *
 * Class that represents a rich response.
 * A 'rich response' can contain a text message and additional metadata.
 *
 * @author Benno Krauß
 */
public class Response {

    /*
    ATTRIBUTES
     */

    private String text;
    private String link;

    /*
    BUILDER
     */

    public static class ResponseBuilder {
        private String text = null;
        private String link = null;

        private ResponseBuilder() {
        }

        public ResponseBuilder text(String text) {
            this.text = text;
            return this;
        }

        public ResponseBuilder link(String link) {
            this.link = link;
            return this;
        }

        public Response build() {
            return new Response(text, link);
        }
    }

    /*
    BUILDER METHODS
     */

    public static ResponseBuilder text(String text) {
        return new ResponseBuilder().text(text);
    }

    public static ResponseBuilder link(String link) {
        return new ResponseBuilder().link(link);
    }

    /*
    CONSTRUCTOR
     */

    private Response(String text, String link) {
        this.text = text;
        this.link = link;
    }

    private Response() {
    }

    /*
    GETTERS
     */

    public String getText() {
        return text;
    }

    public String getLink() {
        return link;
    }
}
