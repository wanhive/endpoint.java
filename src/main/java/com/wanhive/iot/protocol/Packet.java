/*
 * Packet.java
 * 
 * Data packet properties
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
 * Data packet properties
 * 
 * @author amit
 *
 */
public class Packet {
	/**
	 * The maximum packet size in bytes
	 */
	public static final int MTU = 1024;
	/**
	 * Message header size in bytes
	 */
	public static final int HEADER_SIZE = 32;
	/**
	 * The maximum payload size in bytes
	 */
	public static final int PAYLOAD_SIZE = (MTU - HEADER_SIZE);

	/**
	 * Returns true if the given length is valid
	 * 
	 * @param length The message length to validate
	 * @return true if the length is valid, false otherwise
	 */
	public static boolean isValidLength(int length) {
		return length >= HEADER_SIZE && length <= MTU;
	}

	/**
	 * Returns the number of packets required for carrying the given bytes of data
	 * as payload
	 * 
	 * @param bytes Data size in bytes
	 * @return Message count
	 */
	public static long count(int bytes) {
		if (bytes < 0) {
			return 0;
		} else if (bytes <= PAYLOAD_SIZE) {
			return 1;
		} else {
			return ((long) bytes + PAYLOAD_SIZE - 1) / PAYLOAD_SIZE;
		}
	}
}
