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

package io.github.amyassist.amy.plugin.email;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.natlang.EntityData;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * test class for EmailSpeech
 * 
 * @author Lars Buttgereit
 */
@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
class EMailSpeechTest {

	@Reference
	private TestFramework testFramework;

	private EMailLogic emailLogic;
	private EMailSpeech speech;

	@Mock
	private EntityData important;
	@Mock
	private EntityData number;
	@Mock
	private EntityData all;

	@BeforeEach
	void init() {
		this.emailLogic = this.testFramework.mockService(EMailLogic.class);
		this.speech = this.testFramework.setServiceUnderTest(EMailSpeech.class);
	}

	@Test
	void testnewMesagesImportant() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.important.getString()).thenReturn("important");
		when(this.emailLogic.hasNewMessages(true)).thenReturn(true);
		map.put("important", this.important);
		assertThat(this.speech.newMessages(map), equalTo("You have new important messages."));
		when(this.emailLogic.hasNewMessages(true)).thenReturn(false);
		assertThat(this.speech.newMessages(map), equalTo("No important new messages"));
	}
	
	@Test
	void testnewMesagesUnimportant() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.emailLogic.hasNewMessages(false)).thenReturn(true);
		map.put("important", null);
		assertThat(this.speech.newMessages(map), equalTo("You have new messages."));
		when(this.emailLogic.hasNewMessages(false)).thenReturn(false);
		assertThat(this.speech.newMessages(map), equalTo("No new messages"));
	}

	@Test
	void testnumberMesagesImportant() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.important.getString()).thenReturn("important");
		when(this.emailLogic.getNewMessageCount(true)).thenReturn(2);
		map.put("important", this.important);
		assertThat(this.speech.numberOfNewMails(map), equalTo("2 new important messages"));
		when(this.emailLogic.getNewMessageCount(true)).thenReturn(0);
		assertThat(this.speech.numberOfNewMails(map), equalTo("No important new messages"));
	}
	
	@Test
	void testnumberMesagesUnimportant() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.emailLogic.getNewMessageCount(false)).thenReturn(2);
		map.put("important", null);
		assertThat(this.speech.numberOfNewMails(map), equalTo("2 new mails."));
		when(this.emailLogic.getNewMessageCount(false)).thenReturn(0);
		assertThat(this.speech.numberOfNewMails(map), equalTo("No new messages"));
	}
	
	@Test
	void testReadRecentMails() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.important.getString()).thenReturn("important");
		when(this.number.getNumber()).thenReturn(1);
		map.put("important", this.important);
		map.put("number", this.number);
		this.speech.readRecentMails(map);
		verify(this.emailLogic).printMessages(1, true);
		map.put("important", null);
		this.speech.readRecentMails(map);
		verify(this.emailLogic).printMessages(1, false);
	}
	
	@Test
	void testReadRecentMailsAll() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.important.getString()).thenReturn("important");
		when(this.all.getString()).thenReturn("all");
		map.put("important", this.important);
		map.put("all", this.all);
		this.speech.readRecentMails(map);
		verify(this.emailLogic).printMessages(-1, true);
		map.put("important", null);
		this.speech.readRecentMails(map);
		verify(this.emailLogic).printMessages(-1, false);
	}
	
	
}
