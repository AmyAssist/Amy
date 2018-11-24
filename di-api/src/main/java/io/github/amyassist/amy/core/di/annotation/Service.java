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

package de.unistuttgart.iaas.amyassist.amy.core.di.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Register a class as a Service. This annotation marks classes to be loaded in the DI. You can also specify the type of
 * the Service. Services MUST use {@link PostConstruct} instead of a constructor.
 * 
 * @see Reference
 * @see PostConstruct
 * @see PreDestroy
 * 
 * @author Leon Kiefer
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(java.lang.annotation.ElementType.TYPE)
public @interface Service {
	/**
	 * Optional the type of the Service. This is required if this Class implements multiple interfaces. Also required if
	 * you want the class to be registered as service for the class type itself and not the interface it is
	 * implementing. Also remember this is the type you reference this Service with {@link Reference}.
	 * 
	 * @see Reference
	 * 
	 * @return the type of the Service, this value SHOULD be an interface.
	 */
	Class<?> value() default Void.class;
}
