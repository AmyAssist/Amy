/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api;

import java.util.Date;

/**
 * Service to schedule and execute concurrent tasks.
 * 
 * @author Leon Kiefer
 */
public interface TaskSchedulerAPI {
	/**
	 * Executes the given command at some time in the future.
	 * 
	 * @param runnable
	 *            The runnable to run in the future
	 */
	void execute(Runnable runnable);

	/**
	 * Schedules the given task to execute at the given time
	 * 
	 * @param task
	 *            the task to execute
	 * @param date
	 *            The date at which to execute that task
	 */
	void schedule(Runnable task, Date date);
}
