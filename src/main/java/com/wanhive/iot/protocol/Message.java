/*
 * Message.java
 * 
 * Wanhive's data packet implementation
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
import com.wanhive.iot.protocol.bean.MessageHeader;

/**
 * Wanhive's data packet implementation
 * 
 * @author amit
 *
 */
public class Message {
	/**
	 * Message data is stored here
	 */
	private final ByteBuffer buffer;
	/**
	 * The maximum message size in bytes
	 */
	public static final int MTU = 1024;
	/**
	 * Message header size in bytes (fixed)
	 */
	public static final int HEADER_SIZE = MessageHeader.SIZE;
	/**
	 * The maximum payload size in bytes
	 */
	public static final int PAYLOAD_SIZE = (MTU - HEADER_SIZE);

	/**
	 * Validate whether the given length (in bytes) is valid message length
	 * 
	 * @param length The desired message length
	 * @return true if the length is valid, false otherwise
	 */
	public static boolean isValidMessageLength(int length) {
		return length >= HEADER_SIZE && length <= MTU;
	}

	/**
	 * Create a new message
	 */
	public Message() {
		buffer = ByteBuffer.allocate(MTU);
		buffer.putLong(0, 0);
	}

	/**
	 * Returns the backing array that stores the message data
	 * 
	 * @return The byte array that stores the message data
	 */
	public byte[] getBuffer() {
		return buffer.array();
	}

	/**
	 * Populate the message header fields
	 * 
	 * @param source         Source identifier of this message
	 * @param destination    Destination identifier of this message
	 * @param length         Total length of this message in bytes
	 * @param sequenceNumber Sequence number of this message
	 * @param session        Session identifier of this message
	 * @param command        Command classifier of this message
	 * @param qualifier      Command qualifier of this message
	 * @param status         Request/response status of this message
	 * @throws IndexOutOfBoundsException Invalid message length
	 */
	public void prepareHeader(long source, long destination, short length, short sequenceNumber, byte session,
			byte command, byte qualifier, byte status) {
		if (isValidMessageLength(length)) {
			buffer.putLong(8, source);
			buffer.putLong(16, destination);
			buffer.putShort(24, length);
			buffer.putShort(26, sequenceNumber);
			buffer.put(28, session);
			buffer.put(29, command);
			buffer.put(30, qualifier);
			buffer.put(31, status);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Populate the message header fields
	 * 
	 * @param label          Label of this message
	 * @param source         Source identifier of this message
	 * @param destination    Destination identifier of this message
	 * @param length         Total length of this message in bytes
	 * @param sequenceNumber Sequence number of this message
	 * @param session        Session identifier of this message
	 * @param command        Command classifier of this message
	 * @param qualifier      Command qualifier of this message
	 * @param status         Request/response status of this message
	 * @throws IndexOutOfBoundsException Invalid message length
	 */
	public void prepareHeader(long label, long source, long destination, short length, short sequenceNumber,
			byte session, byte command, byte qualifier, byte status) {
		prepareHeader(source, destination, length, sequenceNumber, session, command, qualifier, status);
		buffer.putLong(0, label);
	}

	/**
	 * Populate this message's header using the given MessageHeader object
	 * 
	 * @param header Desired message header
	 * @throws IndexOutOfBoundsException Invalid message length
	 */
	public void prepareHeader(MessageHeader header) {
		prepareHeader(header.getLabel(), header.getSource(), header.getDestination(), header.getLength(),
				header.getSequenceNumber(), header.getSession(), header.getCommand(), header.getQualifier(),
				header.getStatus());
	}

	/**
	 * Deserializes and returns the message header
	 * 
	 * @param header This message's header data is copied here
	 */
	public void getHeader(MessageHeader header) {
		header.setLabel(buffer.getLong(0));
		header.setSource(buffer.getLong(8));
		header.setDestination(buffer.getLong(16));
		header.setLength(buffer.getShort(24));
		header.setSequenceNumber(buffer.getShort(26));
		header.setSession(buffer.get(28));
		header.setCommand(buffer.get(29));
		header.setQualifier(buffer.get(30));
		header.setStatus(buffer.get(31));
	}

	/**
	 * Deserializes and returns the message header
	 * 
	 * @return The MessageHeader object containing this message's header
	 */
	public MessageHeader getHeader() {
		MessageHeader header = new MessageHeader();
		getHeader(header);
		return header;
	}

	/**
	 * Returns the label of this message
	 * 
	 * @return This message's label
	 */
	public long getLabel() {
		return buffer.getLong(0);
	}

	/**
	 * Sets the label of this message
	 * 
	 * @param label This message's label is set to the given value
	 */
	public void setLabel(long label) {
		buffer.putLong(0, label);
	}

	/**
	 * Returns the source identifier of this message
	 * 
	 * @return This message's source identifier
	 */
	public long getSource() {
		return buffer.getLong(8);
	}

	/**
	 * Sets the source identifier of this message
	 * 
	 * @param source This message's Source identifier is set to the given value
	 */
	public void setSource(long source) {
		buffer.putLong(8, source);
	}

	/**
	 * Returns the destination identifier of this message
	 * 
	 * @return This message's destination identifier
	 */
	public long getDestination() {
		return buffer.getLong(16);
	}

	/**
	 * Sets this message's destination identifier
	 * 
	 * @param destination This message's destination identifier is set to the given
	 *                    value.
	 */
	public void setDestination(long destination) {
		buffer.putLong(16, destination);
	}

	/**
	 * Returns this message's length in bytes
	 * 
	 * @return The message length
	 */
	public short getLength() {
		return buffer.getShort(24);
	}

	/**
	 * Sets the total length of this message (measured in bytes)
	 * 
	 * @param length Number of bytes in this message
	 * @throws IndexOutOfBoundsException Invalid length
	 */
	public void setLength(short length) {
		if (isValidMessageLength(length)) {
			buffer.putShort(24, length);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Returns the sequence number of this message
	 * 
	 * @return This message's sequence number
	 */
	public short getSequenceNumber() {
		return buffer.getShort(26);
	}

	/**
	 * Sets this message's sequence number
	 * 
	 * @param sequenceNumber The sequence number of this message is set to the given
	 *                       value
	 */
	public void setSequenceNumber(short sequenceNumber) {
		buffer.putShort(26, sequenceNumber);
	}

	/**
	 * Returns the session identifier of this message
	 * 
	 * @return This message's session identifier
	 */
	public byte getSession() {
		return buffer.get(28);
	}

	/**
	 * Sets this message's session identifier
	 * 
	 * @param session The session identifier of this message is set to the given
	 *                value
	 */
	public void setSession(byte session) {
		buffer.put(28, session);
	}

	/**
	 * Returns the command classifier of this message
	 * 
	 * @return This message's command classifier
	 */
	public byte getCommand() {
		return buffer.get(29);
	}

	/**
	 * Sets this message's command classifier in it's header
	 * 
	 * @param command The command classifier of this message is set to the given
	 *                value
	 */
	public void setCommand(byte command) {
		buffer.put(29, command);
	}

	/**
	 * Returns the command qualifier of this message
	 * 
	 * @return This message's command qualifier
	 */
	public byte getQualifier() {
		return buffer.get(30);
	}

	/**
	 * Sets this message's command qualifier in it's header
	 * 
	 * @param qualifier The command qualifier of this message is set to the given
	 *                  value
	 */
	public void setQualifier(byte qualifier) {
		buffer.put(30, qualifier);
	}

	/**
	 * Returns the request/response status of this message
	 * 
	 * @return This message's request/response status
	 */
	public byte getStatus() {
		return buffer.get(31);
	}

	/**
	 * Sets this message's request/response status in it's header
	 * 
	 * @param status The status of this message is set to the given value
	 */
	public void setStatus(byte status) {
		buffer.put(31, status);
	}

	/**
	 * Read a byte value from the payload at the given offset
	 * 
	 * @param offset The offset within the payload
	 * @return The byte value at the given offset
	 */
	public byte getByte(int offset) {
		return buffer.get(HEADER_SIZE + offset);
	}

	/**
	 * Write a byte value at the given offset in the payload
	 * 
	 * @param offset The offset at which the given value will be written
	 * @param value  The byte value to write at the given offset
	 */
	public void setByte(int offset, byte value) {
		buffer.put(HEADER_SIZE + offset, value);
	}

	/**
	 * Read a char value from the given offset in the payload
	 * 
	 * @param offset The offset from which value will be read
	 * @return The char value at the given offset
	 */
	public char getChar(int offset) {
		return buffer.getChar(HEADER_SIZE + offset);
	}

	/**
	 * Write a char value at the given offset in the payload
	 * 
	 * @param offset The offset at which value will be written
	 * @param value  The char value to write at the given offset
	 */
	public void setChar(int offset, char value) {
		buffer.putChar(HEADER_SIZE + offset, value);
	}

	/**
	 * Read a short value from the given offset in the payload
	 * 
	 * @param offset The offset from which value will be read
	 * @return The short value at the given offset
	 */
	public short getShort(int offset) {
		return buffer.getShort(HEADER_SIZE + offset);
	}

	/**
	 * Write a short value at the given offset in the payload
	 * 
	 * @param offset The offset at which value will be written
	 * @param value  The short value to be written at the given offset
	 */
	public void setShort(int offset, short value) {
		buffer.putShort(HEADER_SIZE + offset, value);
	}

	/**
	 * Read an int value from the given offset in the payload
	 * 
	 * @param offset The offset from which value will be read
	 * @return The int value at the given offset
	 */
	public int getInt(int offset) {
		return buffer.getInt(HEADER_SIZE + offset);
	}

	/**
	 * Write an int value at the given offset in the payload
	 * 
	 * @param offset The offset at which the given value will be written
	 * @param value  The int value to write at the given offset
	 */
	public void setInt(int offset, int value) {
		buffer.putInt(HEADER_SIZE + offset, value);
	}

	/**
	 * Read a long value from the given offset in the payload
	 * 
	 * @param offset The offset from which value will be read
	 * @return The long value at the given offset
	 */
	public long getLong(int offset) {
		return buffer.getLong(HEADER_SIZE + offset);
	}

	/**
	 * Write a long value at the given offset in the payload
	 * 
	 * @param offset The offset at which value will be written
	 * @param value  The long value to write at the given offset
	 */
	public void setLong(int offset, long value) {
		buffer.putLong(HEADER_SIZE + offset, value);
	}

	/**
	 * Read a double value from the given offset in the payload
	 * 
	 * @param offset The offset from which value will be read
	 * @return The double value at the given offset
	 */
	public double getDouble(int offset) {
		return buffer.getDouble(HEADER_SIZE + offset);
	}

	/**
	 * Write a double value at the given offset in the payload
	 * 
	 * @param offset The offset at which value will be written
	 * @param value  The double value to write at the given offset
	 */
	public void setDouble(int offset, double value) {
		buffer.putDouble(HEADER_SIZE + offset, value);
	}

	/**
	 * Read a sequence of bytes from the given offset in the payload
	 * 
	 * @param offset The offset from which bytes will be read
	 * @param length The number of bytes to read
	 * @return The byte array at the given offset
	 */
	public byte[] getBlob(int offset, int length) {
		int p = buffer.position();
		try {
			buffer.position(HEADER_SIZE + offset);
			byte[] blob = new byte[length];
			buffer.get(blob);
			return blob;
		} finally {
			buffer.position(p);
		}
	}

	/**
	 * Read a sequence of bytes from the given offset in the payload
	 * 
	 * @param offset The offset from which bytes will be read
	 * @param blob   The byte array where the bytes will be copied
	 */
	public void getBlob(int offset, byte[] blob) {
		int p = buffer.position();
		try {
			buffer.position(HEADER_SIZE + offset);
			buffer.get(blob);
		} finally {
			buffer.position(p);
		}
	}

	/**
	 * Write a sequence of bytes at the given offset in the payload
	 * 
	 * @param offset The offset at which the bytes will be written
	 * @param blob   The byte array to write at the given offset
	 */
	public void setBlob(int offset, byte[] blob) {
		int p = buffer.position();
		try {
			buffer.position(HEADER_SIZE + offset);
			buffer.put(blob);
		} finally {
			buffer.position(p);
		}
	}

	/**
	 * Match the internal buffer's limit to the current message length.
	 */
	public void freeze() {
		buffer.position(getLength());
		buffer.flip();
	}
}
