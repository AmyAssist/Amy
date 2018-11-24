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

package io.github.amyassist.amy.remotesr;

/**
 * response object from the remote SR
 * 
 * @author Benno Krauß
 */
public class SRResult {

	private String text;

	/**
	 * Get the text of this result
	 * 
	 * @return The text.
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Set the text of this result
	 * 
	 * @param text
	 *            The text.
	 */
	public void setText(String text) {
		this.text = text;
	}
}
