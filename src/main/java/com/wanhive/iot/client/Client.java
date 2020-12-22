/*
 * Client.java
 * 
 * Wanhive client (Blocking IO) interface
 * 
 * This program is part of Wanhive IoT Platform.
 * 
 * Apache-2.0 License
 * Copyright 2020 Wanhive Systems Private Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.wanhive.iot.client;

import java.io.IOException;

import com.wanhive.iot.protocol.Message;

/**
 * The Wanhive client interface for blocking IO
 * 
 * @author amit
 *
 */
public interface Client extends AutoCloseable {
	/**
	 * Sends a message to the network
	 * 
	 * @param message The message to send out
	 * @throws IOException
	 */
	void send(Message message) throws IOException;

	/**
	 * Receives a message from the network
	 * 
	 * @return A message received from the network
	 * @throws IOException
	 */
	Message receive() throws IOException;

	/**
	 * Reads from the connection until a message matching the given sequence number
	 * is found
	 * 
	 * @param sequenceNumber The desired sequence number (set to zero to ignore)
	 * @return A message containing the matching sequence number
	 * @throws IOException
	 */
	Message receive(short sequenceNumber) throws IOException;

	/**
	 * Executes a request. Combines send and receive calls in a single operation.
	 * 
	 * @param request The request
	 * @return The response to the request
	 * @throws IOException
	 */
	Message execute(Message request) throws IOException;

	/**
	 * Sets the socket connection's read timeout to the given value
	 * 
	 * @param milliseconds timeout value in milliseconds (set to 0 to block forever)
	 * @throws IOException
	 */
	void setTimeout(int milliseconds) throws IOException;
}
