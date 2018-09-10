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

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.UUID;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.DialogHandler;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;

/**
 * the class running the server socket for the chat via TCP
 * 
 * @author Christian Bräuner
 */
@Service(TCPChatSocket.class)
public class TCPChatSocket implements RunnableService{

	/** The name of the config used by this class */
	public static final String CONFIG_NAME = "socket.config";
	/** The name of the property, which specifies the port */
	public static final String PROPERTY_PORT = "tcp.socket.port";
	
	/**
	 * the socket that represents the server
	 */
	ServerSocket serverSocket;
	
	/**
	 * the dialog handler
	 */
	@Reference DialogHandler handler;

	/**
	 * the logger
	 */
	@Reference Logger logger;
	
	@Reference
	private ConfigurationManager configurationManager;
	
	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#start()
	 */
	@Override
	public void start() {
		Properties conf = this.configurationManager.getConfigurationWithDefaults(CONFIG_NAME);
		int port = Integer.parseInt(conf.getProperty(PROPERTY_PORT));
		
		try {
			this.serverSocket = new ServerSocket(port);
			new Thread() {
				
				@Override
				public void run() {
					TCPChatSocket.this.logger.info("Chatserver started");
					
					// accept new connections and instantiate a new ClientHandler for each session
					try {
						while (true) {
							new ClientHandler(TCPChatSocket.this.serverSocket.accept()).start();
						}
					} catch (SocketException se) {
						if(!se.getMessage().equals("socket closed")) {
							TCPChatSocket.this.logger.error("Socket exception", se);
						}
					} catch (IOException e) {
						TCPChatSocket.this.logger.error("Error while running chat socket", e);
					}
				}
			}.start();
		} catch (IOException e) {
			this.logger.error("Error while starting", e);
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
			this.logger.error("Error while shuting down", e);
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
			try(PrintWriter out = new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream(),"UTF-8"), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream(),"UTF-8"));
					) {
					
				out.println("Hello");
				Queue<String> answerQueue = new LinkedList<>();
				UUID uuid = TCPChatSocket.this.handler.createDialog(answerQueue::add);
				
				while(true) {
					
					//look for input
					if(in.ready()) {
						String input = in.readLine();
						if(input.equalsIgnoreCase(ClientHandler.TERMINATE)) {
							break;
						}
						TCPChatSocket.this.handler.process(input, uuid);
					}
					
					//output if possible
					String output = answerQueue.poll();
					if(output != null) {
						out.println(output);
					}
				}
				
				this.clientSocket.close();
			} catch (IOException e) {
				TCPChatSocket.this.logger.error("Error in session", e);
			}
		}
	}
	
}
