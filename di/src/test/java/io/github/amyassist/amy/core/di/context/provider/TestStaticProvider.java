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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.amyassist.amy.core.di.DependencyInjection;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Test for the StaticContext provider in combination with annotations of the consumer
 * 
 * @author Leon Kiefer
 */
public class TestStaticProvider {
	private DependencyInjection dependencyInjection;

	@BeforeEach
	public void setup() {
		this.dependencyInjection = new DependencyInjection();
	}

	@Test
	void testUseAnnoations() {
		this.dependencyInjection.getConfiguration().register(ServiceWithAnnotationContext.class);
		this.dependencyInjection.getConfiguration().register(ServiceWithDependencies.class);

		this.dependencyInjection.getConfiguration().registerContextProvider("annotation",
				consumer -> consumer.getServiceDescription().getAnnotations().stream()
						.filter(annotation -> annotation instanceof AnnotatoinWithValue).findFirst()
						.map(annotation -> ((AnnotatoinWithValue) annotation).value()).orElse(null));

		ServiceWithDependencies service = this.dependencyInjection.getServiceLocator()
				.getService(ServiceWithDependencies.class);
		assertThat(service.getValueOfService1(), is("1"));
		assertThat(service.getValueOfService2(), is("2"));
	}
}
