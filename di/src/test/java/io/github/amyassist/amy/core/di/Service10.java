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

package io.github.amyassist.amy.core.di;

import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;

/**
 * Test Service for DI
 * 
 * @author Leon Kiefer
 */
public class Service10 {
	@Reference
	private Service1 service1;
	private int i;
	private boolean init = false;

	/**
	 * @return the init
	 */
	public boolean isInit() {
		return init;
	}

	/**
	 * @return the service1
	 */
	public Service1 getService1() {
		return service1;
	}

	/**
	 * @return the i
	 */
	public int getI() {
		return i;
	}

	public Service10(int important) {
		this.i = important;
	}

	@PostConstruct
	public void setup() {
		init = true;
	}
}
