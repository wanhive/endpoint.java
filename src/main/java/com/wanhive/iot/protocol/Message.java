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

import com.wanhive.iot.protocol.bean.MessageContext;
import com.wanhive.iot.protocol.bean.MessageHeader;

/**
 * Wanhive's data packet implementation
 * 
 * @author amit
 *
 */
public class Message {
	private static final String BAD_MSG_LENGTH = "Invalid message length";
	/**
	 * Stores the message data
	 */
	private final ByteBuffer buffer;
	/**
	 * The maximum message size in bytes
	 */
	public static final int MTU = 1024;
	/**
	 * Message header size in bytes
	 */
	public static final int HEADER_SIZE = MessageHeader.SIZE;
	/**
	 * The maximum payload size in bytes
	 */
	public static final int PAYLOAD_SIZE = (MTU - HEADER_SIZE);

	/**
	 * Returns true if the given value is a valid message length
	 * 
	 * @param length The message length to validate
	 * @return true if the length is valid, false otherwise
	 */
	public static boolean isValidLength(int length) {
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
	 * Populates message's header
	 * 
	 * @param source         Source identifier of this message
	 * @param destination    Destination identifier of this message
	 * @param length         Total length of this message in bytes
	 * @param sequenceNumber Sequence number of this message
	 * @param session        Session identifier of this message
	 * @param command        Command classifier of this message
	 * @param qualifier      Command qualifier of this message
	 * @param status         Request/response status of this message
	 * @return This message
	 */
	public Message prepareHeader(long source, long destination, short length, short sequenceNumber, byte session,
			byte command, byte qualifier, byte status) {
		if (isValidLength(length)) {
			buffer.putLong(8, source);
			buffer.putLong(16, destination);
			buffer.putShort(24, length);
			buffer.putShort(26, sequenceNumber);
			buffer.put(28, session);
			buffer.put(29, command);
			buffer.put(30, qualifier);
			buffer.put(31, status);
			return this;
		} else {
			throw new IllegalArgumentException(BAD_MSG_LENGTH);
		}
	}

	/**
	 * Populates message's header
	 * 
	 * @param source         Source identifier of this message
	 * @param destination    Destination identifier of this message
	 * @param length         Total length of this message in bytes
	 * @param sequenceNumber Sequence number of this message
	 * @param session        Session identifier of this message
	 * @param ctx            Context of this message
	 * @return This message
	 */
	public Message prepareHeader(long source, long destination, short length, short sequenceNumber, byte session,
			MessageContext ctx) {
		return prepareHeader(source, destination, length, sequenceNumber, session, ctx.getCommand(), ctx.getQualifier(),
				ctx.getStatus());
	}

	/**
	 * Populates message's header
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
	 * @return This message
	 */
	public Message prepareHeader(long label, long source, long destination, short length, short sequenceNumber,
			byte session, byte command, byte qualifier, byte status) {
		prepareHeader(source, destination, length, sequenceNumber, session, command, qualifier, status);
		buffer.putLong(0, label);
		return this;
	}

	/**
	 * Populates message's header
	 * 
	 * @param label          Label of this message
	 * @param source         Source identifier of this message
	 * @param destination    Destination identifier of this message
	 * @param length         Total length of this message in bytes
	 * @param sequenceNumber Sequence number of this message
	 * @param session        Session identifier of this message
	 * @param ctx            Context of this message
	 * @return This message
	 */
	public Message prepareHeader(long label, long source, long destination, short length, short sequenceNumber,
			byte session, MessageContext ctx) {
		return prepareHeader(label, source, destination, length, sequenceNumber, session, ctx.getCommand(),
				ctx.getQualifier(), ctx.getStatus());
	}

	/**
	 * Populates message's header
	 * 
	 * @param header Desired message header
	 * @return This message
	 */
	public Message prepareHeader(MessageHeader header) {
		return prepareHeader(header.getLabel(), header.getSource(), header.getDestination(), header.getLength(),
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
	 * Returns the label
	 * 
	 * @return Message's label
	 */
	public long getLabel() {
		return buffer.getLong(0);
	}

	/**
	 * Sets the label
	 * 
	 * @param label Message's label is set to the given value
	 * @return This message
	 */
	public Message setLabel(long label) {
		buffer.putLong(0, label);
		return this;
	}

	/**
	 * Returns the source identifier
	 * 
	 * @return Message's source identifier
	 */
	public long getSource() {
		return buffer.getLong(8);
	}

	/**
	 * Sets the source identifier
	 * 
	 * @param source Message's source identifier is set to the given value
	 * @return This message
	 */
	public Message setSource(long source) {
		buffer.putLong(8, source);
		return this;
	}

	/**
	 * Returns the destination identifier
	 * 
	 * @return Message's destination identifier
	 */
	public long getDestination() {
		return buffer.getLong(16);
	}

	/**
	 * Sets the destination identifier
	 * 
	 * @param destination Message's destination identifier is set to the given
	 *                    value.
	 * @return This message
	 */
	public Message setDestination(long destination) {
		buffer.putLong(16, destination);
		return this;
	}

	/**
	 * Returns message's length in bytes
	 * 
	 * @return The message length
	 */
	public short getLength() {
		return buffer.getShort(24);
	}

	/**
	 * Sets the message length in bytes
	 * 
	 * @param length Message's length is set to this value
	 * @return This message
	 */
	public Message setLength(short length) {
		if (isValidLength(length)) {
			buffer.putShort(24, length);
			return this;
		} else {
			throw new IllegalArgumentException(BAD_MSG_LENGTH);
		}
	}

	/**
	 * Returns the sequence number
	 * 
	 * @return Message's sequence number
	 */
	public short getSequenceNumber() {
		return buffer.getShort(26);
	}

	/**
	 * Sets the sequence number
	 * 
	 * @param sequenceNumber Message's sequence number is set to the given value
	 * @return This message
	 */
	public Message setSequenceNumber(short sequenceNumber) {
		buffer.putShort(26, sequenceNumber);
		return this;
	}

	/**
	 * Returns the session identifier
	 * 
	 * @return Message's session identifier
	 */
	public byte getSession() {
		return buffer.get(28);
	}

	/**
	 * Sets the session identifier
	 * 
	 * @param session Message's session identifier is set to the given value
	 * @return This message
	 */
	public Message setSession(byte session) {
		buffer.put(28, session);
		return this;
	}

	/**
	 * Returns the command classifier
	 * 
	 * @return Message's command classifier
	 */
	public byte getCommand() {
		return buffer.get(29);
	}

	/**
	 * Sets the command classifier
	 * 
	 * @param command Message's command classifier is set to the given value
	 * @return This message
	 */
	public Message setCommand(byte command) {
		buffer.put(29, command);
		return this;
	}

	/**
	 * Returns the command qualifier
	 * 
	 * @return Message's command qualifier
	 */
	public byte getQualifier() {
		return buffer.get(30);
	}

	/**
	 * Sets the command qualifier
	 * 
	 * @param qualifier Message's command qualifier is set to the given value
	 * @return This message
	 */
	public Message setQualifier(byte qualifier) {
		buffer.put(30, qualifier);
		return this;
	}

	/**
	 * Returns the request/response status code
	 * 
	 * @return Message's status code
	 */
	public byte getStatus() {
		return buffer.get(31);
	}

	/**
	 * Sets the request/response status code
	 * 
	 * @param status Message's status code is set to the given value
	 * @return This message
	 */
	public Message setStatus(byte status) {
		buffer.put(31, status);
		return this;
	}

	/**
	 * Reads a byte value from the payload at the given index
	 * 
	 * @param index The index from which the byte value will be read
	 * @return The byte value at the given index
	 */
	public byte getByte(int index) {
		return buffer.get(HEADER_SIZE + index);
	}

	/**
	 * Writes a byte value at the given index in the payload
	 * 
	 * @param index The index at which the byte value will be written
	 * @param value The byte value to write
	 * @return This message
	 */
	public Message setByte(int index, byte value) {
		buffer.put(HEADER_SIZE + index, value);
		return this;
	}

	/**
	 * Reads a char value from the payload at the given index
	 * 
	 * @param index The index from which the char value will be read
	 * @return The char value at the given index
	 */
	public char getChar(int index) {
		return buffer.getChar(HEADER_SIZE + index);
	}

	/**
	 * Writes a char value at the given index in the payload
	 * 
	 * @param index The index at which the char value will be written
	 * @param value The char value to write
	 * @return This message
	 */
	public Message setChar(int index, char value) {
		buffer.putChar(HEADER_SIZE + index, value);
		return this;
	}

	/**
	 * Reads a short value from the payload at the given index
	 * 
	 * @param index The index from which the short value will be read
	 * @return The short value at the given index
	 */
	public short getShort(int index) {
		return buffer.getShort(HEADER_SIZE + index);
	}

	/**
	 * Writes a short value at the given index in the payload
	 * 
	 * @param index The index at which the short value will be written
	 * @param value The short value to write
	 * @return This message
	 */
	public Message setShort(int index, short value) {
		buffer.putShort(HEADER_SIZE + index, value);
		return this;
	}

	/**
	 * Reads an int value from the payload at the given index
	 * 
	 * @param index The index from which the int value will be read
	 * @return The int value at the given index
	 */
	public int getInt(int index) {
		return buffer.getInt(HEADER_SIZE + index);
	}

	/**
	 * Writes an int value at the given index in the payload
	 * 
	 * @param index The index at which the int value will be written
	 * @param value The int value to write
	 * @return This message
	 */
	public Message setInt(int index, int value) {
		buffer.putInt(HEADER_SIZE + index, value);
		return this;
	}

	/**
	 * Reads a long value from the payload at the given index
	 * 
	 * @param index The index from which the short value will be read
	 * @return The long value at the given index
	 */
	public long getLong(int index) {
		return buffer.getLong(HEADER_SIZE + index);
	}

	/**
	 * Writes a long value at the given index in the payload
	 * 
	 * @param index The index at which the long value will be written
	 * @param value The long value to write
	 * @return This message
	 */
	public Message setLong(int index, long value) {
		buffer.putLong(HEADER_SIZE + index, value);
		return this;
	}

	/**
	 * Reads a double value from the payload at the given index
	 * 
	 * @param index The index from which the double value will be read
	 * @return The double value at the given index
	 */
	public double getDouble(int index) {
		return buffer.getDouble(HEADER_SIZE + index);
	}

	/**
	 * Writes a double value at the given index in the payload
	 * 
	 * @param index The index at which the double value will be written
	 * @param value The double value to write
	 * @return This message
	 */
	public Message setDouble(int index, double value) {
		buffer.putDouble(HEADER_SIZE + index, value);
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
			buffer.position(HEADER_SIZE + index);
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
			buffer.position(HEADER_SIZE + index);
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
	 * @return This message
	 */
	public Message setBlob(int index, byte[] blob) {
		int p = buffer.position();
		try {
			buffer.position(HEADER_SIZE + index);
			buffer.put(blob);
			return this;
		} finally {
			buffer.position(p);
		}
	}

	/**
	 * Match the internal buffer's limit to the current message length.
	 * 
	 * @return This message
	 */
	public Message freeze() {
		buffer.position(getLength());
		buffer.flip();
		return this;
	}
}
