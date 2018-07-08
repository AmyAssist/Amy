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

package de.unistuttgart.iaas.amyassist.amy.plugin.email;

import java.util.Properties;

import javax.mail.MessagingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * TODO: Description
 * @author Felix Burk
 */
@ExtendWith(FrameworkExtension.class)
public class EmailLogicTest {
	
	@Mock
	private Properties configLoader;

	
	@Reference
	private TestFramework framework;
	
	private EMailLogic emailLogic;
	
	/**
	 * Initializes the class variables before each test
	 */
	@BeforeEach
	public void setup() {
		this.configLoader = this.framework.mockService(Properties.class);
		this.emailLogic = this.framework.setServiceUnderTest(EMailLogic.class);
	}
	
	/**
	 * more to come
	 */
	@Test
	public void testHasNewMessages() {
		boolean b = false;
		try {
			b = this.emailLogic.hasNewMessages();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
