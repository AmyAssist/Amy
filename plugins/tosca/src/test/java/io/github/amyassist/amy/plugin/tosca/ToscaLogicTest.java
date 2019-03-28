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

package io.github.amyassist.amy.plugin.tosca;

import java.util.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.opentosca.container.client.ContainerClient;
import org.opentosca.container.client.model.Application;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.taskscheduler.api.TaskScheduler;
import io.github.amyassist.amy.plugin.tosca.configurations.ConfigurationEntry;
import io.github.amyassist.amy.plugin.tosca.configurations.ConfigurationRegistry;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;
import io.swagger.client.model.CsarDTO;
import io.swagger.client.model.PlanDTO;
import io.swagger.client.model.TParameter;

/**
 * Test for the tosca logic.
 * 
 * @author Felix Burk, Leon Kiefer
 */
@ExtendWith(FrameworkExtension.class)
public class ToscaLogicTest {

	private final static String CONTAINER_HOST = "somehost";
	private final static int CONTAINER_HOST_PORT = 1337;

	@Reference
	private TestFramework framework;

	private ToscaLogic toskaLogic;

	private ContainerClient toscaClient;

	private List<Application> apps;

	private List<ConfigurationEntry> createConfig() {
		List<ConfigurationEntry> config = new ArrayList<>();
		ConfigurationEntry entry1 = new ConfigurationEntry();
		entry1.setTag("testConfig");
		entry1.setKey("testKey");
		entry1.setValue("testValue");
		ConfigurationEntry entry2 = new ConfigurationEntry();
		entry2.setTag("testConfig");
		entry2.setKey("unused");
		entry2.setValue("value");
		ConfigurationEntry entry3 = new ConfigurationEntry();
		entry3.setTag("unused");
		entry3.setKey("testKey");
		entry3.setValue("wrong");
		ConfigurationEntry entry4 = new ConfigurationEntry();
		entry4.setTag("other");
		entry4.setKey("key");
		entry4.setValue("value");
		config.add(entry1);
		config.add(entry2);
		config.add(entry3);
		config.add(entry4);
		return config;
	}

	private List<Application> createApps() {
		List<Application> ret = new ArrayList<>();
		CsarDTO csarDTO = new CsarDTO();
		csarDTO.setDisplayName("Test App");
		csarDTO.setVersion("V1");
		PlanDTO planDTO = new PlanDTO();
		planDTO.setInputParameters(Arrays.asList(new TParameter().name("testKey")));

		ret.add(Application.builder().csar(csarDTO).buildPlan(planDTO).build());

		return ret;
	}

	/**
	 * Setup
	 * 
	 * @throws Exception
	 *             When anything goes wrong
	 */
	@BeforeEach
	public void setup() throws Exception {
		Properties properties = this.framework.mockService(Properties.class);
		Mockito.when(properties.getProperty("container.host")).thenReturn(CONTAINER_HOST);
		Mockito.when(properties.getProperty("container.host.port")).thenReturn(String.valueOf(CONTAINER_HOST_PORT));

		TaskScheduler scheduler = this.framework.mockService(TaskScheduler.class);
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Runnable run = (Runnable) invocation.getArguments()[0];
				run.run();
				return null;
			}
		}).when(scheduler).execute(ArgumentMatchers.any());

		ConfigurationRegistry registry = this.framework.mockService(ConfigurationRegistry.class);
		List<ConfigurationEntry> config = createConfig();
		Mockito.when(registry.getAll()).thenReturn(config);
		this.toscaClient = Mockito.mock(ContainerClient.class);
		this.apps = createApps();
		Mockito.when(this.toscaClient.getApplications()).thenReturn(this.apps);

		ToscaLibraryAdapter adap = this.framework.mockService(ToscaLibraryAdapter.class);
		Mockito.when(adap.createLibrary(CONTAINER_HOST, CONTAINER_HOST_PORT)).thenReturn(this.toscaClient);

		this.toskaLogic = this.framework.setServiceUnderTest(ToscaLogic.class);
	}

	/**
	 * Test {@link ToscaLogic#getInstalledApps()}
	 */
	@Test
	public void testGetInstalledApps() {
		Assertions.assertEquals(this.apps, this.toskaLogic.getInstalledApps());
	}

	/**
	 * Tests {@link ToscaLogic#install(String, String)} and through that the other variants of that method in the normal
	 * case.
	 */
	@Test
	public void testInstall() {
		this.toskaLogic.install("Test App", "testConfig");
		Map<String, String> args = new HashMap<>();
		args.put("testKey", "testValue");
		Mockito.verify(this.toscaClient).provisionApplication(ArgumentMatchers.eq(this.apps.get(0)),
				ArgumentMatchers.eq(args));
	}

	/**
	 * Tests {@link ToscaLogic#install(String, String)} when the given name does not correspond to an app.
	 */
	@Test
	public void testInstallNoSuchApp() {
		Assertions.assertThrows(NoSuchElementException.class,
				() -> this.toskaLogic.install("Not an app", "testConfig"));
	}

	/**
	 * Tests {@link ToscaLogic#install(String, String)} and through that {@link ToscaLogic#install(Application, String)}
	 * when the given configuration does not contain all keys required.
	 */
	@Test
	public void testInstallConfigWrong() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> this.toskaLogic.install("Test App", "not a config"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> this.toskaLogic.install("Test App", "other"));
	}

}
