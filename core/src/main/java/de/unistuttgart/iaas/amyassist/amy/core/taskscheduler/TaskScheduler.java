/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.core.taskscheduler;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI;

/**
 * Implementation of {@link TaskSchedulerAPI}
 * 
 * @author Leon Kiefer
 */
public class TaskScheduler implements TaskSchedulerAPI {

	private final Logger logger = LoggerFactory.getLogger(TaskScheduler.class);

	private ScheduledExecutorService scheduledExecutorService;

	/**
	 * @param scheduledExecutorService
	 */
	public TaskScheduler(ScheduledExecutorService scheduledExecutorService) {
		this.scheduledExecutorService = scheduledExecutorService;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI#execute(java.lang.Runnable)
	 */
	@Override
	public void execute(Runnable runnable) {
		this.scheduledExecutorService.execute(runnable);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI#schedule(java.lang.Runnable,
	 *      java.util.Date)
	 */
	@Override
	public void schedule(Runnable task, Date date) {
		this.logger.debug("schedule task for {}", date);
		long delay = date.getTime() - System.currentTimeMillis();
		this.logger.debug("the delay of the task is {} ms", delay);
		this.scheduledExecutorService.schedule(task, delay, TimeUnit.MILLISECONDS);
	}

}
