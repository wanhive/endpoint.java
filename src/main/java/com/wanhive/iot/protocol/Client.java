/*
 * Client.java
 * 
 * The Client interface
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
package com.wanhive.iot.protocol;

import java.io.IOException;

/**
 * The Client interface (supports blocking IO)
 * 
 * @author amit
 *
 */
public interface Client extends AutoCloseable {
	/**
	 * Sends a message to the network
	 * 
	 * @param message The {@link Message} to send out
	 * @throws IOException
	 */
	void send(Message message) throws IOException;

	/**
	 * Receives a {@link Message} from the network
	 * 
	 * @return A {@link Message} received from the network
	 * @throws IOException
	 */
	Message receive() throws IOException;

	/**
	 * Reads from the connection until a {@link Message} matching the given sequence
	 * number is found
	 * 
	 * @param sequenceNumber The desired sequence number (set to zero to ignore)
	 * @return A {@link Message} containing the matching sequence number
	 * @throws IOException
	 */
	Message receive(short sequenceNumber) throws IOException;

	/**
	 * Executes a request
	 * 
	 * @param request The request {@link Message} to the host
	 * @return The response {@link Message} from the host
	 * @throws IOException
	 */
	Message execute(Message request) throws IOException;

	/**
	 * Sets connection's read timeout to the given value
	 * 
	 * @param milliseconds timeout value in milliseconds (set to 0 to block forever)
	 * @throws IOException
	 */
	void setTimeout(int milliseconds) throws IOException;
}
