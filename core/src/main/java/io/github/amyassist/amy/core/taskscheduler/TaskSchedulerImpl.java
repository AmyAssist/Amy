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

package io.github.amyassist.amy.core.taskscheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.slf4j.Logger;

import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.io.Environment;
import io.github.amyassist.amy.core.service.RunnableService;
import io.github.amyassist.amy.core.taskscheduler.api.TaskScheduler;

/**
 * Implementation of Service {@link TaskScheduler}
 * 
 * @author Leon Kiefer
 */
@Service(TaskScheduler.class)
public class TaskSchedulerImpl implements TaskScheduler, RunnableService {
	@Reference
	private Logger logger;

	@Reference
	private Environment environment;

	private ScheduledExecutorService scheduledExecutorService;

	@PostConstruct
	private void setup() {
		this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "TaskScheduler"));
	}

	private Runnable handleException(Runnable task) {
		return () -> {
			try {
				task.run();
			} catch (RuntimeException e) {
				this.logger.error("Exception while executing scheduled task.", e);
				throw e;
			}
		};
	}

	@Override
	public void execute(Runnable runnable) {
		this.scheduledExecutorService.execute(this.handleException(runnable));
	}

	@Override
	public @Nonnull ScheduledFuture<?> schedule(Runnable task, Instant instant) {
		this.logger.debug("schedule task for {}", instant);
		long delay = ChronoUnit.MILLIS.between(this.environment.getCurrentDateTime().toInstant(), instant);
		this.logger.debug("the delay of the task is {} ms", delay);
		return this.scheduledExecutorService.schedule(this.handleException(task), delay, TimeUnit.MILLISECONDS);
	}

	@Override
	public @Nonnull ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit timeUnit) {
		this.logger.debug("schedule task with delay of {} {}", delay, timeUnit);
		return this.scheduledExecutorService.schedule(this.handleException(task), delay, timeUnit);
	}

	@Override
	public void start() {
		// Do nothing.
	}

	@Override
	public void stop() {
		this.scheduledExecutorService.shutdownNow();
	}

}
