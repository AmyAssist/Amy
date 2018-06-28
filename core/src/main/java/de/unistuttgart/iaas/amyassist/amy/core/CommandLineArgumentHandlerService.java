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

package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.EnumMap;
import java.util.Map;

import asg.cliche.Command;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * This class handles the command line arguments passed to the core.
 *
 * @author Tim Neumann
 */
@Service
public class CommandLineArgumentHandlerService implements CommandLineArgumentHandler {

	private Map<Flag, String> parameters;

	private boolean flagsValid = true;

	/**
	 * Initializes the command line handler
	 *
	 * @param args
	 *            The command line arguments.
	 */
	public void init(String[] args) {
		this.parameters = new EnumMap<>(Flag.class);
		Flag flagExpectingPara = null;
		for (String s : args) {
			if (flagExpectingPara != null) {
				this.parameters.put(flagExpectingPara, s);
				flagExpectingPara = null;
				continue;
			}
			Flag f = Flag.getFlagFromString(s);
			if (f == null) {
				output("Unknown command line flag: " + s + ". Run with -h for help.");
				this.flagsValid = false;
				return;
			}

			if (f.hasParameter()) {
				flagExpectingPara = f;
			} else {
				this.parameters.put(f, "");
			}

			switch (f) {
			case HELP:
				printHelp();
				return;
			case VERSION:
				output(version());
				return;
			case NOTICE:
				output(notice());
				return;
			default:
				// All other flags don't do anything immediately.
				break;
			}
		}
		output("This is Amy. Copyright (c) 2018 the Amy project authors. For help run with flag -h.");
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.CommandLineArgumentHandler#shouldProgramContinue()
	 */
	@Override
	public boolean shouldProgramContinue() {
		if (this.parameters == null)
			throw new IllegalStateException("Not initialiozed.");
		if (!this.flagsValid)
			return false;
		for (Flag f : this.parameters.keySet()) {
			if (f.isStopExecutaion())
				return false;
		}
		return true;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.CommandLineArgumentHandler#getConfigPath()
	 */
	@Override
	public String getConfigPath() {
		return this.parameters.get(Flag.CONFIG);
	}

	private void printHelp() {
		output("This is Amy. A open source personal assitance system.");
		output("Developed by a group of students at University of Stuttgart.");
		output("This is a research project. No functionality is tested. There may be harmful errors.");
		output("");
		output("Copyright (c) 2018 the Amy project authors.");
		output("");
		output("For information about the license please start the program with --notice");
		output("");
		output("For further information about the project see: github.com/AmyAssist");
		output("");
		output("For help with the command line interface, after the program has started please enter '?'.");
		output("");
		output("Command line flags:");
		output("");
		for (Flag f : Flag.values()) {
			String s = "  ";
			if (!f.getShortVariant().isEmpty()) {
				s += f.getShortVariant();
			}
			if ((!f.getShortVariant().isEmpty()) && (!f.getLongVariant().isEmpty())) {
				s += " or ";
			}
			if (!f.getLongVariant().isEmpty()) {
				s += f.getLongVariant();
			}

			s += " : " + f.getDescription();

			output(s);
		}
	}

	/**
	 * The Version String
	 * 
	 * @return the version of amy core
	 */
	@Command(name = "version", description = "Prints out the version.")
	public String version() {
		return "Amy core version: " + getClass().getPackage().getImplementationVersion();
	}

	/**
	 * The Notice text
	 * 
	 * @return the notice of Amy project
	 */
	@Command(name = "notice", description = "Prints out the license notice.")
	public String notice() {
		return "Copyright (c) 2018 the Amy project authors.\n" + " \n" + "  SPDX-License-Identifier: Apache-2.0\n"
				+ " \n" + "  Licensed under the Apache License, Version 2.0 (the \"License\");\n"
				+ "  you may not use this file except in compliance with the License.\n"
				+ "  You may obtain a copy of the License at\n" + " \n"
				+ "    http://www.apache.org/licenses/LICENSE-2.0\n" + " \n"
				+ "  Unless required by applicable law or agreed to in writing, software\n"
				+ "  distributed under the License is distributed on an \"AS IS\" BASIS,\n"
				+ "  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
				+ "  See the License for the specific language governing permissions and\n"
				+ "  limitations under the License.\n" + " \n" + "  For more information see notice.md";
	}

	private void output(String s) {
		System.out.println(s);
	}

	private enum Flag {
		/**
		 * The help flag.
		 */
		HELP("-h", "--help", "Prints a help message", true, false),

		/**
		 * The version flag
		 */
		VERSION("-v", "--version", "Prints out the version.", true, false),

		/**
		 * The license notice flag
		 */
		NOTICE("", "--notice", "Prints out the license notice.", true, false),

		/**
		 * The config dir flag
		 */
		CONFIG("-c", "--config <path>", "Set a alternate location for the config directory", false, true);

		private String shortVariant;
		private String longVariant;
		private String description;
		private boolean hasParameter;
		private boolean stopExecutaion;

		Flag(String pShortVariant, String pLongVariant, String pDescription, boolean pStopExecution,
				boolean pHasParameter) {
			this.shortVariant = pShortVariant;
			this.longVariant = pLongVariant;
			this.description = pDescription;
			this.hasParameter = pHasParameter;
			this.stopExecutaion = pStopExecution;
		}

		/**
		 * Returns the flag, that corresponds to the given string
		 *
		 * @param s
		 *            The string to give the flag for
		 * @return The flag for that string or null if such a flag was not found.
		 */
		public static Flag getFlagFromString(String s) {
			for (Flag f : values()) {
				if (f.longVariant.equals(s) || f.shortVariant.equals(s))
					return f;
			}
			return null;
		}

		/**
		 * Get's {@link #shortVariant shortVariant}
		 *
		 * @return shortVariant
		 */
		public String getShortVariant() {
			return this.shortVariant;
		}

		/**
		 * Get's {@link #longVariant longVariant}
		 *
		 * @return longVariant
		 */
		public String getLongVariant() {
			return this.longVariant;
		}

		/**
		 * Get's {@link #description description}
		 *
		 * @return description
		 */
		public String getDescription() {
			return this.description;
		}

		/**
		 * Get's {@link #hasParameter hasParameter}
		 *
		 * @return hasParameter
		 */
		public boolean hasParameter() {
			return this.hasParameter;
		}

		/**
		 * Get's {@link #stopExecutaion stopExecutaion}
		 *
		 * @return stopExecutaion
		 */
		public boolean isStopExecutaion() {
			return this.stopExecutaion;
		}

	}
}
