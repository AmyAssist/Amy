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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.natlang.Dialog;
import de.unistuttgart.iaas.amyassist.amy.natlang.DialogHandlerImpl;
import de.unistuttgart.iaas.amyassist.amy.natlang.NLProcessingManager;
import de.unistuttgart.iaas.amyassist.amy.natlang.NLProcessingManagerImpl;
import de.unistuttgart.iaas.amyassist.amy.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.natlang.userinteraction.Entity;
import de.unistuttgart.iaas.amyassist.amy.natlang.userinteraction.UserIntent;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test class for DialogHandlerImpl
 * 
 * @author Lars Buttgereit
 */
@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
public class DialogHandlerImplTest {
	@Reference
	private TestFramework testFramework;

	private NLProcessingManager manager;
	private DialogHandlerImpl dialogHandler;
	private UUID uuid;

	@Mock
	private Consumer<String> consumer;

	private ServiceLocator serviceLocator;

	@Mock
	private Dialog dialog;

	@Mock
	private UserIntent intent;

	@BeforeEach
	void init() {
		this.manager = this.testFramework.mockService(NLProcessingManager.class);
		this.dialogHandler = this.testFramework.setServiceUnderTest(DialogHandlerImpl.class);
		this.uuid = this.dialogHandler.createDialog(this.consumer);
	}

	@Test
	void testDeleteDialog() {
		this.dialogHandler.deleteDialog(this.uuid);
		assertThrows(IllegalArgumentException.class, () -> this.dialogHandler.process("", this.uuid));
	}

	@Test
	void testProcess() {
		Map<String, Entity> entities = new HashMap<>();
		entities.put("blub", new Entity("bla", new AGFNode("blub"), true));
		when(this.manager.decideIntent(any(), any())).thenReturn(this.dialog);
		when(this.dialog.getIntent()).thenReturn(this.intent);
		when(this.intent.isFinished()).thenReturn(true);
		when(this.intent.getEntityList()).thenReturn(entities);
		this.dialogHandler.process("", this.uuid);
		verify(this.manager).decideIntent(any(), any());
		verify(this.dialog, VerificationModeFactory.times(4)).getIntent();
		verify(this.dialog).setIntent(null);
	}

}
