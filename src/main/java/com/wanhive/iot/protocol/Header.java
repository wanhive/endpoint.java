/*
 * Header.java
 * 
 * Message header implementation
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

import java.nio.ByteBuffer;

/**
 * Message header implementation
 * 
 * @author amit
 *
 */
public class Header {
	private static final String BAD_LENGTH = "Invalid length";
	/**
	 * Stores the message data
	 */
	private final ByteBuffer buffer;

	/**
	 * Constructor
	 * 
	 * @param buffer The working buffer
	 */
	Header(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	/**
	 * Returns the label
	 * 
	 * @return The label
	 */
	public long getLabel() {
		return buffer.getLong(0);
	}

	/**
	 * Sets the label
	 * 
	 * @param label The label
	 * @return {@code this} {@link Header}
	 */
	public Header setLabel(long label) {
		buffer.putLong(0, label);
		return this;
	}

	/**
	 * Returns the source identifier
	 * 
	 * @return The source identifier
	 */
	public long getSource() {
		return buffer.getLong(8);
	}

	/**
	 * Sets the source identifier
	 * 
	 * @param source The source identifier
	 * @return {@code this} {@link Header}
	 */
	public Header setSource(long source) {
		buffer.putLong(8, source);
		return this;
	}

	/**
	 * Returns the destination identifier
	 * 
	 * @return The destination identifier
	 */
	public long getDestination() {
		return buffer.getLong(16);
	}

	/**
	 * Sets the destination identifier
	 * 
	 * @param destination The destination identifier
	 * @return {@code this} {@link Header}
	 */
	public Header setDestination(long destination) {
		buffer.putLong(16, destination);
		return this;
	}

	/**
	 * Returns the message length
	 * 
	 * @return The message length in bytes
	 */
	public short getLength() {
		return buffer.getShort(24);
	}

	/**
	 * Sets the message length
	 * 
	 * @param length The message length in bytes
	 * @return {@code this} {@link Header}
	 */
	public Header setLength(short length) {
		if (Packet.isValidLength(length)) {
			buffer.limit(length);
			buffer.putShort(24, length);
			return this;
		} else {
			throw new IllegalArgumentException(BAD_LENGTH);
		}
	}

	/**
	 * Returns the sequence number
	 * 
	 * @return The sequence number
	 */
	public short getSequenceNumber() {
		return buffer.getShort(26);
	}

	/**
	 * Sets the sequence number
	 * 
	 * @param sequenceNumber The sequence number
	 * @return {@code this} {@link Header}
	 */
	public Header setSequenceNumber(short sequenceNumber) {
		buffer.putShort(26, sequenceNumber);
		return this;
	}

	/**
	 * Returns the session identifier
	 * 
	 * @return The session identifier
	 */
	public byte getSession() {
		return buffer.get(28);
	}

	/**
	 * Sets the session identifier
	 * 
	 * @param session The session identifier
	 * @return {@code this} {@link Header}
	 */
	public Header setSession(byte session) {
		buffer.put(28, session);
		return this;
	}

	/**
	 * Returns the command classifier
	 * 
	 * @return The command classifier
	 */
	public byte getCommand() {
		return buffer.get(29);
	}

	/**
	 * Sets the command classifier
	 * 
	 * @param command The command classifier
	 * @return {@code this} {@link Header}
	 */
	public Header setCommand(byte command) {
		buffer.put(29, command);
		return this;
	}

	/**
	 * Returns the command qualifier
	 * 
	 * @return The command qualifier
	 */
	public byte getQualifier() {
		return buffer.get(30);
	}

	/**
	 * Sets the command qualifier
	 * 
	 * @param qualifier The command qualifier
	 * @return {@code this} {@link Header}
	 */
	public Header setQualifier(byte qualifier) {
		buffer.put(30, qualifier);
		return this;
	}

	/**
	 * Returns the request/response status code
	 * 
	 * @return The status code
	 */
	public byte getStatus() {
		return buffer.get(31);
	}

	/**
	 * Sets the request/response status code
	 * 
	 * @param status The status code
	 * @return {@code this} {@link Header}
	 */
	public Header setStatus(byte status) {
		buffer.put(31, status);
		return this;
	}
}
