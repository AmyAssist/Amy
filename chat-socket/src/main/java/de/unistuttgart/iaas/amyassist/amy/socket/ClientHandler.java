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

package de.unistuttgart.iaas.amyassist.amy.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.DialogHandler;

/**
 * A handler thread class to start a new session for each client.
 * Responsible for a dealing with a single client and sending its messages
 * to the respective user.
 * 
 * @author Christian Bräuner
 */
public class ClientHandler extends Thread {

	private static final String TERMINATE = "quit";
	
	private Socket clientSocket;
	
	@Reference
	private DialogHandler handler;
	
	/**
	 * Constructs a handler thread.
	 * 
	 * @param socket the socket to handle
	 */
	public ClientHandler(Socket socket) {
		// init handler
		this.clientSocket = socket;
	}

	/**
	 * Services this thread's client by repeatedly requesting a screen name
	 * until a unique one has been submitted, then acknowledges the name and
	 * registers the output stream for the client in a global set.
	 */
	@Override
	public void run() {
		try(PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
				) {
					
			Queue<String> answerQueue = new LinkedList<>();
			UUID uuid = this.handler.createDialog(answerQueue::add);
			
			boolean running = true;
			while(running) {
				
				//look for input
				if(in.ready()) {
					String input = in.readLine();
					if(input.equalsIgnoreCase(ClientHandler.TERMINATE)) {
						running = false;
						break;
					}
					this.handler.process(input, uuid);
				}
				
				//output if possible
				String output = answerQueue.poll();
				if(output != null) {
					out.println(output);
				}
			}
			
			in.close();
			out.close();
			this.clientSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
	

