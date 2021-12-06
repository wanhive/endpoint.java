/*
 * MessageContext.java
 * 
 * The message context structure
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
 * The message context structure
 * 
 * @author amit
 *
 */
public class MessageContext {
	/**
	 * Command ID
	 */
	private byte command;
	/**
	 * Qualifier ID
	 */
	private byte qualifier;
	/**
	 * Status code
	 */
	private byte status;

	/**
	 * The default constructor. Initializes all the fields to zero.
	 */
	public MessageContext() {
		set((byte) 0, (byte) 0, (byte) 0);
	}

	/**
	 * Constructor. Initializes the status code to zero.
	 * 
	 * @param command   The command identifier
	 * @param qualifier The qualifier ID
	 */
	public MessageContext(byte command, byte qualifier) {
		set(command, qualifier, (byte) 0);
	}

	/**
	 * Constructor
	 * 
	 * @param command   The command identifier
	 * @param qualifier The qualifier ID
	 * @param status    The status code
	 */
	public MessageContext(byte command, byte qualifier, byte status) {
		set(command, qualifier, status);
	}

	/**
	 * Sets the command and qualifier values
	 * 
	 * @param command   The command identifiers
	 * @param qualifier The qualifier ID
	 */
	void set(byte command, byte qualifier) {
		setCommand(command);
		setQualifier(qualifier);
	}

	/**
	 * Sets the command, qualifier and status values
	 * 
	 * @param command   The command identifier
	 * @param qualifier The qualifier ID
	 * @param status    The status code
	 */
	public void set(byte command, byte qualifier, byte status) {
		setCommand(command);
		setQualifier(qualifier);
		setStatus(status);
	}

	/**
	 * Returns the command
	 * 
	 * @return The command identifier
	 */
	public byte getCommand() {
		return command;
	}

	/**
	 * Sets the command
	 * 
	 * @param command The command identifier
	 */
	public void setCommand(byte command) {
		this.command = command;
	}

	/**
	 * Returns the qualifier
	 * 
	 * @return The qualifier ID
	 */
	public byte getQualifier() {
		return qualifier;
	}

	/**
	 * Sets the qualifier
	 * 
	 * @param qualifier The qualifier ID
	 */
	public void setQualifier(byte qualifier) {
		this.qualifier = qualifier;
	}

	/**
	 * Returns the status
	 * 
	 * @return The status code
	 */
	public byte getStatus() {
		return status;
	}

	/**
	 * Sets the status code
	 * 
	 * @param status The status code
	 */
	public void setStatus(byte status) {
		this.status = status;
	}
}
