/*
 * FlowControl.java
 * 
 * Sequence number and session management
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
package com.wanhive.iot.protocol;

/**
 * Sequence number and session management
 * 
 * @author amit
 *
 */
public class FlowControl {
	/**
	 * Sequence number counter
	 */
	private short sequenceNumber;
	/**
	 * Session identifier
	 */
	private byte session;

	public FlowControl() {
		setSequenceNumber((short) 0);
		setSession((byte) 0);
	}

	/**
	 * Increments the sequence number counter and returns the next value
	 * 
	 * @return The next sequence number
	 */
	public short nextSequenceNumber() {
		++sequenceNumber;
		if (sequenceNumber <= 0) {
			sequenceNumber = 1;
		}
		return sequenceNumber;
	}

	/**
	 * Sets the sequence number counter to the given value
	 * 
	 * @param sequenceNumber The sequence number
	 */
	public void setSequenceNumber(short sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * Returns the current sequence number
	 * 
	 * @return The current sequence number
	 */
	public short getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * Sets the session identifier
	 * 
	 * @param session The session identifier
	 */
	public void setSession(byte session) {
		this.session = session;
	}

	/**
	 * Returns the session identifier
	 * 
	 * @return The current session identifier
	 */
	public byte getSession() {
		return session;
	}
}
