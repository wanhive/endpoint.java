/*
 * MessageAddress.java
 * 
 * The message address structure
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
 * The message address structure
 * 
 * @author amit
 *
 */
public class MessageAddress {
	/**
	 * Source identifier
	 */
	private long source;
	/**
	 * Destination identifier
	 */
	private long destination;

	/**
	 * The default constructor. Initializes all the fields to zero.
	 */
	public MessageAddress() {
		set(0, 0);
	}

	/**
	 * Constructor
	 * 
	 * @param source      The source identifier
	 * @param destination The destination identifier
	 */
	public MessageAddress(long source, long destination) {
		set(source, destination);
	}

	/**
	 * Sets the source and destination identifiers.
	 * 
	 * @param source      The source identifier
	 * @param destination The destination identifier
	 */
	public void set(long source, long destination) {
		setSource(source);
		setDestination(destination);
	}

	/**
	 * Returns the source identifier
	 * 
	 * @return The source identifier
	 */
	public long getSource() {
		return source;
	}

	/**
	 * Sets the source identifier
	 * 
	 * @param source The source identifier
	 */
	public void setSource(long source) {
		this.source = source;
	}

	/**
	 * Returns the destination identifier
	 * 
	 * @return The destination identifier
	 */
	public long getDestination() {
		return destination;
	}

	/**
	 * Sets the destination identifier
	 * 
	 * @param destination The destination identifier
	 */
	public void setDestination(long destination) {
		this.destination = destination;
	}
}
