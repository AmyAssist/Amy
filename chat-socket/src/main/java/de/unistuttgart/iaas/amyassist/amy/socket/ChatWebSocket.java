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

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.DialogHandler;

/**
 * A web socket for the chat communication 
 *  
 * @author Christian Bräuner
 */
@Service(ChatWebSocket.class)
public class ChatWebSocket extends WebSocketServer {
	
	@Reference 
	private Logger logger;
	
	@Reference 
	private DialogHandler handler;
	
	private Map<InetSocketAddress,UUID> dialogMap = new HashMap<>();
	
	/**
	 *creates a new web socket server
	 *
	 *@param port the port of the server
	 */
	public ChatWebSocket(int port) {
		super(new InetSocketAddress(port));
	}
	
	/**
	 * @see org.java_websocket.server.WebSocketServer#onClose(org.java_websocket.WebSocket, int, java.lang.String, boolean)
	 */
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		this.logger.debug(conn + " closed connection");
		this.logger.debug("Code was " + code + ". Reason was " + reason + "." );
		
	}

	/**
	 * @see org.java_websocket.server.WebSocketServer#onError(org.java_websocket.WebSocket, java.lang.Exception)
	 */
	@Override
	public void onError(WebSocket conn, Exception ex) {
		this.logger.error("Web Socket exception", ex);
	}

	/**
	 * @see org.java_websocket.server.WebSocketServer#onMessage(org.java_websocket.WebSocket, java.lang.String)
	 */
	@Override
	public void onMessage(WebSocket conn, String message) {
		this.handler.process(message, this.dialogMap.get(conn.getRemoteSocketAddress()));
	}

	/**
	 * @see org.java_websocket.server.WebSocketServer#onOpen(org.java_websocket.WebSocket, org.java_websocket.handshake.ClientHandshake)
	 */
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		conn.send("Hello");
		UUID uuid = this.handler.createDialog(conn::send);
		this.dialogMap.put(conn.getRemoteSocketAddress(), uuid);
	}

	/**
	 * @see org.java_websocket.server.WebSocketServer#onStart()
	 */
	@Override
	public void onStart() {
		this.logger.info("Chatserver started");
		setConnectionLostTimeout(100);
	}

}
