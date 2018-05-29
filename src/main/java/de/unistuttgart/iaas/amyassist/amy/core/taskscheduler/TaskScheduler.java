/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.taskscheduler;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI;

/**
 * Implementation of {@link TaskSchedulerAPI}
 * 
 * @author Leon Kiefer
 */
public class TaskScheduler implements TaskSchedulerAPI {

	private ScheduledExecutorService scheduledExecutorService;

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
		this.scheduledExecutorService.schedule(task,
				date.getTime() - System.currentTimeMillis(),
				TimeUnit.MILLISECONDS);
	}

}
