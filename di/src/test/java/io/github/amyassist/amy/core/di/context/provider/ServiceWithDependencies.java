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

package io.github.amyassist.amy.core.di.context.provider;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;

/**
 * Service with two references to the same Service type with different annotations
 * 
 * @author Leon Kiefer
 */
@Service
public class ServiceWithDependencies {
	@AnnotatoinWithValue("1")
	@Reference
	private ServiceWithAnnotationContext service1;
	@AnnotatoinWithValue("2")
	@Reference
	private ServiceWithAnnotationContext service2;

	public String getValueOfService1() {
		return this.service1.getValue();
	}

	public String getValueOfService2() {
		return this.service2.getValue();
	}
}
