/*
 * MessageControl.java
 * 
 * The message flow control structure
 * 
 * This program is part of Wanhive IoT Platform.
 * 
 * Apache-2.0 License
 * Copyright 2021 Wanhive Systems Private Limited
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
 * The message flow control structure
 * 
 * @author amit
 *
 */
public class MessageControl {
	/**
	 * Message length
	 */
	private short length;
	/**
	 * Sequence number
	 */
	private short sequenceNumber;
	/**
	 * Session/topic identifier
	 */
	private byte session;

	/**
	 * The default constructor. Initializes all the fields to zero.
	 */
	public MessageControl() {
		set((short) 0, (short) 0, (byte) 0);
	}

	/**
	 * Constructor
	 * 
	 * @param length         The message length (bytes)
	 * @param sequenceNumber The sequence number
	 * @param session        The session identifier
	 */
	public MessageControl(short length, short sequenceNumber, byte session) {
		set(length, sequenceNumber, session);
	}

	/**
	 * Sets all the fields.
	 * 
	 * @param length         The message length (bytes)
	 * @param sequenceNumber The sequence number
	 * @param session        The session identifier
	 */
	public void set(short length, short sequenceNumber, byte session) {
		setLength(length);
		setSequenceNumber(sequenceNumber);
		setSession(session);
	}

	/**
	 * Returns the message length
	 * 
	 * @return The message length in bytes
	 */
	public short getLength() {
		return length;
	}

	/**
	 * Sets the message length
	 * 
	 * @param length The message length in bytes
	 */
	public void setLength(short length) {
		this.length = length;
	}

	/**
	 * Returns the sequence number
	 * 
	 * @return The sequence number
	 */
	public short getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * Sets the sequence number
	 * 
	 * @param sequenceNumber The sequence number
	 */
	public void setSequenceNumber(short sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * Returns the session identifier
	 * 
	 * @return The session identifier
	 */
	public byte getSession() {
		return session;
	}

	/**
	 * Sets the session identifier
	 * 
	 * @param session The session identifier
	 */
	public void setSession(byte session) {
		this.session = session;
	}

}
