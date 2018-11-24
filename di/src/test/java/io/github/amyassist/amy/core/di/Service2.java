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

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;

/**
 * Test Service for DI
 * 
 * @author Leon Kiefer
 */
@Service(Service2.class)
public class Service2 {
	@Reference
	private Service1 service1;

	@Reference
	private Service3 service3;

	public boolean checkServices() {
		return this.service1 != null && this.service3 != null;
	}

	/**
	 * Get's {@link #service1 service1}
	 * 
	 * @return service1
	 */
	public Service1 getService1() {
		return this.service1;
	}

	/**
	 * Get's {@link #service3 service3}
	 * 
	 * @return service3
	 */
	public Service3 getService3() {
		return this.service3;
	}
}
