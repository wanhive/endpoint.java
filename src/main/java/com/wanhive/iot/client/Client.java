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

import com.wanhive.iot.protocol.Message;

/**
 * Wanhive client (Blocking IO) interface
 * 
 * @author amit
 *
 */
public interface Client extends AutoCloseable {
	/**
	 * Sends a message to the network
	 * 
	 * @param message Message to send out
	 * @throws Exception
	 */
	void send(Message message) throws Exception;

	/**
	 * Receives a message from the network
	 * 
	 * @return Message received from the network
	 * @throws Exception
	 */
	Message receive() throws Exception;

	/**
	 * Reads from the connection until a message matching the given sequence number
	 * is found
	 * 
	 * @param sequenceNumber desired sequence number (set to zero to ignore)
	 * @return Message containing matching sequence number
	 * @throws Exception
	 */
	Message receive(short sequenceNumber) throws Exception;

	/**
	 * Sets connection's read timeout to the given value
	 * 
	 * @param milliseconds timeout value in milliseconds (0= disable timeout)
	 * @throws Exception
	 */
	void setTimeout(int milliseconds) throws Exception;
}
