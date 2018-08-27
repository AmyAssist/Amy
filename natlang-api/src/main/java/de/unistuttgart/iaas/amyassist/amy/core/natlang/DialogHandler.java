package de.unistuttgart.iaas.amyassist.amy.core.natlang;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * entry point for natural language interpretation
 *  
 * @author Lars Buttgereit, Felix Burk
 */
public interface DialogHandler {

	/**
	 * creates a new dialog
	 * 
	 * @param cons
	 *                 callback for consumer
	 * @return the matching uuid
	 */
	UUID createDialog(Consumer<String> cons);

	/**
	 * deletes a save dialog - this should be called once in a while if the core is running for any long amounts of time
	 *
	 * @param uuid
	 *                 of fialog to delete
	 */
	void deleteDialog(UUID uuid);

	/**
	 * processes a dialog from a given uuid with natural language input from a user
	 *
	 * @param naturalLanguageText
	 *                                from user
	 * @param uuid
	 *                                of dialog
	 */
	void process(String naturalLanguageText, UUID uuid);

}