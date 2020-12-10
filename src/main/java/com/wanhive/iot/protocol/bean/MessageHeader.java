/*
 * MessageHeader.java
 * 
 * Message header
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
 * Fixed size message header compliant with the Wanhive protocol
 * 
 * @author amit
 *
 */
public final class MessageHeader {
	/**
	 * The label identifier (implementation dependent)
	 */
	private long label;
	/**
	 * The source identifier
	 */
	private long source;
	/**
	 * The destination identifier
	 */
	private long destination;
	/**
	 * The total message length in bytes (including the header)
	 */
	private short length;
	/**
	 * The message sequence number
	 */
	private short sequenceNumber;
	/**
	 * The session or topic identifier
	 */
	private byte session;
	/**
	 * The command classifier
	 */
	private byte command;
	/**
	 * The command qualifier
	 */
	private byte qualifier;
	/**
	 * The status code
	 */
	private byte status;

	/**
	 * The message header size in bytes
	 */
	public static final int SIZE = 32;

	/**
	 * The getter for label identifier
	 * 
	 * @return The label identifier
	 */
	public long getLabel() {
		return label;
	}

	/**
	 * The setter for label identifier
	 * 
	 * @param label The label identifier will be set to this value
	 */
	public void setLabel(long label) {
		this.label = label;
	}

	/**
	 * The getter for source identifier
	 * 
	 * @return The source identifier
	 */
	public long getSource() {
		return source;
	}

	/**
	 * The setter for source identifier
	 * 
	 * @param The source identifier will be set to this value
	 */
	public void setSource(long source) {
		this.source = source;
	}

	/**
	 * The getter for destination identifier
	 * 
	 * @return The destination identifier
	 */
	public long getDestination() {
		return destination;
	}

	/**
	 * The setter for destination identifier
	 * 
	 * @param destination The destination identifier will be set to this value
	 */
	public void setDestination(long destination) {
		this.destination = destination;
	}

	/**
	 * The getter for message length
	 * 
	 * @return The message length
	 */
	public short getLength() {
		return length;
	}

	/**
	 * The setter for message length
	 * 
	 * @param length The message length in bytes
	 */
	public void setLength(short length) {
		this.length = length;
	}

	/**
	 * The getter for message's sequence number
	 * 
	 * @return The sequence number
	 */
	public short getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * The setter for sequence number
	 * 
	 * @param sequenceNumber The sequence number will be set to this value
	 */
	public void setSequenceNumber(short sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * The getter for message's session identifier
	 * 
	 * @return The session identifier
	 */
	public byte getSession() {
		return session;
	}

	/**
	 * The setter for message's session identifier
	 * 
	 * @param session The session identifier will be set to this value
	 */
	public void setSession(byte session) {
		this.session = session;
	}

	/**
	 * The getter for the command classifier
	 * 
	 * @return The command classifier
	 */
	public byte getCommand() {
		return command;
	}

	/**
	 * The setter for the command classifier
	 * 
	 * @param command The command classifier will be set to this value
	 */
	public void setCommand(byte command) {
		this.command = command;
	}

	/**
	 * The getter for the command qualifier
	 * 
	 * @return The command qualifier
	 */
	public byte getQualifier() {
		return qualifier;
	}

	/**
	 * The setter for the command qualifier
	 * 
	 * @param qualifier The command qualifier will be set to this value
	 */
	public void setQualifier(byte qualifier) {
		this.qualifier = qualifier;
	}

	/**
	 * The getter for the request/response status code
	 * 
	 * @return The status code
	 */
	public byte getStatus() {
		return status;
	}

	/**
	 * The setter for the request/response status code
	 * 
	 * @param status The status code will be set to this value
	 */
	public void setStatus(byte status) {
		this.status = status;
	}
}
