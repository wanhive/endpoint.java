/*
 * MessageContext.java
 * 
 * The context of a request/response
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
package com.wanhive.iot.protocol.bean;

/**
 * The context of a request/response
 * 
 * @author amit
 *
 */
public class MessageContext {
	private byte command;
	private byte qualifier;
	private byte status;

	/**
	 * Returns the command
	 * 
	 * @return A byte value containing the command
	 */
	public byte getCommand() {
		return command;
	}

	/**
	 * Sets a command
	 * 
	 * @param command A byte value containing the command
	 */
	public void setCommand(byte command) {
		this.command = command;
	}

	/**
	 * Returns the qualifier
	 * 
	 * @return A byte value containing the qualifier
	 */
	public byte getQualifier() {
		return qualifier;
	}

	/**
	 * Sets a qualifier
	 * 
	 * @param qualifier A byte value containing the qualifier
	 */
	public void setQualifier(byte qualifier) {
		this.qualifier = qualifier;
	}

	/**
	 * Returns the status
	 * 
	 * @return A byte value containing the status code
	 */
	public byte getStatus() {
		return status;
	}

	/**
	 * Sets a status code
	 * 
	 * @param status A byte value containing the status code
	 */
	public void setStatus(byte status) {
		this.status = status;
	}

	/**
	 * The default constructor. Sets command, qualifier, and status to zero.
	 */
	public MessageContext() {
		this.command = 0;
		this.qualifier = 0;
		this.status = 0;
	}

	/**
	 * Constructor
	 * 
	 * @param command   The byte value of the command
	 * @param qualifier The byte value of the qualifier
	 */
	public MessageContext(byte command, byte qualifier) {
		this.command = command;
		this.qualifier = qualifier;
		this.status = 0;
	}

	/**
	 * Constructor
	 * 
	 * @param command   The byte value of the command
	 * @param qualifier The byte value of the qualifier
	 * @param status    The byte value of the status
	 */
	public MessageContext(byte command, byte qualifier, byte status) {
		this.command = command;
		this.qualifier = qualifier;
		this.status = status;
	}
}
