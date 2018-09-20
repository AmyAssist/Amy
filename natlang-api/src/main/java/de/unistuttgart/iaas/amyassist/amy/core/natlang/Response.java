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
    private String widget;
    private Object attachment;

    /*
    BUILDER
     */

    public static class ResponseBuilder {
        private String text = null;
        private String widget = null;
        private Object attachment = null;

        private ResponseBuilder() {
        }

        public ResponseBuilder text(String text) {
            this.text = text;
            return this;
        }

        public ResponseBuilder widget(String widget) {
            this.widget = widget;
            return this;
        }

        public ResponseBuilder attachment(Object attachment) {
            this.attachment = attachment;
            return this;
        }

        public Response build() {
            return new Response(text, widget, attachment);
        }
    }

    /*
    BUILDER METHODS
     */

    public static ResponseBuilder text(String text) {
        return new ResponseBuilder().text(text);
    }

    public static ResponseBuilder widget(String widget) {
        return new ResponseBuilder().widget(widget);
    }

    public static ResponseBuilder attachment(Object attachment) {
        return new ResponseBuilder().attachment(attachment);
    }

    /*
    CONSTRUCTOR
     */

    private Response(String text, String widget, Object attachment) {
        this.text = text;
        this.widget = widget;
        this.attachment = attachment;
    }

    private Response() {
    }

    /*
    GETTERS
     */

    public String getText() {
        return text;
    }

    public String getWidget() {
        return widget;
    }

    public Object getAttachment() {
        return attachment;
    }

    @Override
    public String toString() {
        return "Response{" +
            "text='" + text + '\'' +
            ", widget='" + widget + '\'' +
            ", attachment='" + attachment +
            '}';
    }
}
