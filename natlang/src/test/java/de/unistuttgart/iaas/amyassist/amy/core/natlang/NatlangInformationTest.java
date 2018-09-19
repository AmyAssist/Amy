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
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.natlang.NLProcessingManager;
import de.unistuttgart.iaas.amyassist.amy.natlang.NatlangInformationImpl;
import de.unistuttgart.iaas.amyassist.amy.natlang.agf.AGFLexer;
import de.unistuttgart.iaas.amyassist.amy.natlang.agf.AGFParser;
import de.unistuttgart.iaas.amyassist.amy.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test for NatlangInformation Service
 * @author Felix Burk
 */
@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
public class NatlangInformationTest {
	
	@Reference
	private TestFramework testFramework;
	
	/**
	 * mock service
	 */
	private NLProcessingManager manager;
	/**
	 * service to test
	 */
	private NatlangInformation info;
	
	/**
	 * initializer
	 */
	@BeforeEach
	void init() {
		this.manager = this.testFramework.mockService(NLProcessingManager.class);
		this.info = this.testFramework.setServiceUnderTest(NatlangInformationImpl.class);
		String gram = "test [some] (weird | strange [test] grammar)";
		AGFLexer lex = new AGFLexer(gram);
		AGFParser parser = new AGFParser(lex);
		List<AGFNode> list = new ArrayList<>();
		list.add(parser.parseWholeExpression());
		
		when(this.manager.getPossibleGrammars()).thenReturn(list);
	}
	
	/**
	 * tests generating sample sentences from a grammar
	 */
	@Test
	public void generateSampleSentenceTest() {
		List<String> list = this.info.getAnySampleSentences(1);
		String result = list.get(0);
		assertEquals(true, result.contains("test"));
		assertEquals(true, (result.contains("weird") || result.contains("strange") && result.contains("grammar")));
	}
	
	/**
	 * tests generating sample sentences of a grammar containing a keyword
	 */
	@Test
	public void generateSentenceWithKeyword() {
		List<String> list = this.info.getSampleSentencesFromKeyword("test", 1);
		String result = list.get(0);
		assertEquals(true, result.contains("test"));
		assertEquals(true, (result.contains("weird") || result.contains("strange") && result.contains("grammar")));
		
		List<String> list2 = this.info.getSampleSentencesFromKeyword("xx", 1);
		assertEquals(true, list2.isEmpty());
	}
}
