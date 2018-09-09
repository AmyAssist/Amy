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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.DialogHandler;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;

/**
 * the class running the server socket for the chat 
 * 
 * @author Christian Bräuner
 */
@Service(ChatServer.class)
public class ChatServer implements RunnableService{

	/**
	 * the socket that represents the server
	 */
	ServerSocket serverSocket;
	
	private static final int PORT = 10000;
	
	/**
	 * the dialog handler
	 */
	@Reference DialogHandler handler;

	/**
	 * the logger
	 */
	@Reference Logger logger;
	
	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#start()
	 */
	@Override
	public void start() {
		try {
			this.serverSocket = new ServerSocket(PORT);
			new Thread() {
				
				@Override
				public void run() {
					ChatServer.this.logger.info("Chatserver started");
					
					// accept new connections and instantiate a new ClientHandler for each session
					try {
						while (true) {
							new ClientHandler(ChatServer.this.serverSocket.accept()).start();
						}
					} catch (SocketException se) {
						if(!se.getMessage().equals("socket closed")) {
							se.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#stop()
	 */
	@Override
	public void stop() {
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.logger.info("ChatServer shutdown");
	}


	/**
	 * A handler thread class to start a new session for each client.
	 * Responsible for a dealing with a single client and sending its messages
	 * to the respective user.
	 * 
	 * @author Christian Bräuner
	 */
	private class ClientHandler extends Thread {

		private static final String TERMINATE = "quit";
		
		private Socket clientSocket;
		
		
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
					
				out.println("Hello");
				Queue<String> answerQueue = new LinkedList<>();
				UUID uuid = ChatServer.this.handler.createDialog(answerQueue::add);
				
				boolean running = true;
				while(running) {
					
					//look for input
					if(in.ready()) {
						String input = in.readLine();
						if(input.equalsIgnoreCase(ClientHandler.TERMINATE)) {
							running = false;
							break;
						}
						ChatServer.this.handler.process(input, uuid);
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
	
}
