/*
 * Payload.java
 * 
 * Wanhive's payload implementation
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

import java.nio.ByteBuffer;

/**
 * Wanhive's payload implementation
 * 
 * @author amit
 *
 */
public class Payload {
	/**
	 * Stores the message data
	 */
	private final ByteBuffer buffer;
	/**
	 * Offset to the data section within the buffer
	 */
	private final int offset;

	/**
	 * Constructor
	 * 
	 * @param buffer The working buffer
	 * @param offset Data offset inside the buffer
	 */
	Payload(ByteBuffer buffer, int offset) {
		this.buffer = buffer;
		this.offset = offset;
	}

	/**
	 * Reads a byte value from the payload at the given index
	 * 
	 * @param index The index from which the byte value will be read
	 * @return The byte value at the given index
	 */
	public byte getByte(int index) {
		return buffer.get(offset + index);
	}

	/**
	 * Writes a byte value at the given index in the payload
	 * 
	 * @param index The index at which the byte value will be written
	 * @param value The byte value to write
	 * @return This payload
	 */
	public Payload setByte(int index, byte value) {
		buffer.put(offset + index, value);
		return this;
	}

	/**
	 * Reads a char value from the payload at the given index
	 * 
	 * @param index The index from which the char value will be read
	 * @return The char value at the given index
	 */
	public char getChar(int index) {
		return buffer.getChar(offset + index);
	}

	/**
	 * Writes a char value at the given index in the payload
	 * 
	 * @param index The index at which the char value will be written
	 * @param value The char value to write
	 * @return This message
	 */
	public Payload setChar(int index, char value) {
		buffer.putChar(offset + index, value);
		return this;
	}

	/**
	 * Reads a short value from the payload at the given index
	 * 
	 * @param index The index from which the short value will be read
	 * @return The short value at the given index
	 */
	public short getShort(int index) {
		return buffer.getShort(offset + index);
	}

	/**
	 * Writes a short value at the given index in the payload
	 * 
	 * @param index The index at which the short value will be written
	 * @param value The short value to write
	 * @return This payload
	 */
	public Payload setShort(int index, short value) {
		buffer.putShort(offset + index, value);
		return this;
	}

	/**
	 * Reads an int value from the payload at the given index
	 * 
	 * @param index The index from which the int value will be read
	 * @return The int value at the given index
	 */
	public int getInt(int index) {
		return buffer.getInt(offset + index);
	}

	/**
	 * Writes an int value at the given index in the payload
	 * 
	 * @param index The index at which the int value will be written
	 * @param value The int value to write
	 * @return This payload
	 */
	public Payload setInt(int index, int value) {
		buffer.putInt(offset + index, value);
		return this;
	}

	/**
	 * Reads a long value from the payload at the given index
	 * 
	 * @param index The index from which the short value will be read
	 * @return The long value at the given index
	 */
	public long getLong(int index) {
		return buffer.getLong(offset + index);
	}

	/**
	 * Writes a long value at the given index in the payload
	 * 
	 * @param index The index at which the long value will be written
	 * @param value The long value to write
	 * @return This payload
	 */
	public Payload setLong(int index, long value) {
		buffer.putLong(offset + index, value);
		return this;
	}

	/**
	 * Reads a double value from the payload at the given index
	 * 
	 * @param index The index from which the double value will be read
	 * @return The double value at the given index
	 */
	public double getDouble(int index) {
		return buffer.getDouble(offset + index);
	}

	/**
	 * Writes a double value at the given index in the payload
	 * 
	 * @param index The index at which the double value will be written
	 * @param value The double value to write
	 * @return This payload
	 */
	public Payload setDouble(int index, double value) {
		buffer.putDouble(offset + index, value);
		return this;
	}

	/**
	 * Reads a sequence of bytes value from the payload at the given index
	 * 
	 * @param index  The index from which the bytes will be read
	 * @param length The number of bytes to read
	 * @return The byte array at the given index
	 */
	public byte[] getBlob(int index, int length) {
		int p = buffer.position();
		try {
			buffer.position(offset + index);
			byte[] blob = new byte[length];
			buffer.get(blob);
			return blob;
		} finally {
			buffer.position(p);
		}
	}

	/**
	 * Reads a sequence of bytes value from the payload at the given index
	 * 
	 * @param index The index from which the bytes will be read
	 * @param blob  The byte array where the bytes will be copied
	 */
	public void getBlob(int index, byte[] blob) {
		int p = buffer.position();
		try {
			buffer.position(offset + index);
			buffer.get(blob);
		} finally {
			buffer.position(p);
		}
	}

	/**
	 * Writes a sequence of bytes at the given index in the payload
	 * 
	 * @param index The index at which the bytes will be written
	 * @param blob  The bytes to write
	 * @return This payload
	 */
	public Payload setBlob(int index, byte[] blob) {
		int p = buffer.position();
		try {
			buffer.position(offset + index);
			buffer.put(blob);
			return this;
		} finally {
			buffer.position(p);
		}
	}
}
