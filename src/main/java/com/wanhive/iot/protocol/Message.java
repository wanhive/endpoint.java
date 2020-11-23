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
	private ByteBuffer buffer;
	/**
	 * Maximum message size (bytes) including header and payload
	 */
	public static final int MTU = 1024;
	/**
	 * Message header size in bytes
	 */
	public static final int HEADER_SIZE = 32;
	/**
	 * Maximum payload size in bytes
	 */
	public static final int PAYLOAD_SIZE = (MTU - HEADER_SIZE);

	/**
	 * Create a new message
	 */
	public Message() {
		buffer = ByteBuffer.allocate(MTU);
		buffer.putLong(0, 0);
	}

	/**
	 * Returns the underlying ByteBuffer based storage of this message
	 * 
	 * @return ByteBuffer object containing serialized message data
	 */
	public ByteBuffer getBuffer() {
		return buffer;
	}

	/**
	 * Populate the message header fields
	 * 
	 * @param source         Source identifier of this message
	 * @param destination    Destination identifier of this message
	 * @param length         Total length of this message as number of bytes
	 * @param sequenceNumber Sequence number of this message
	 * @param session        Session identifier of this message
	 * @param command        Command classifier of this message
	 * @param qualifier      Command qualifier of this message
	 * @param status         Request/response status of this message
	 * @throws IndexOutOfBoundsException Invalid message length
	 */
	public void prepareHeader(long source, long destination, short length, short sequenceNumber, byte session,
			byte command, byte qualifier, byte status) {
		if (length >= HEADER_SIZE && length <= MTU) {
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
	 * Populate the message header fields using the given MessageHeader object
	 * 
	 * @param header desired message header
	 * @throws IndexOutOfBoundsException Invalid message length
	 */
	public void prepareHeader(MessageHeader header) {
		if (header.getLength() >= HEADER_SIZE && header.getLength() <= MTU) {
			buffer.putLong(0, header.getLabel());
			buffer.putLong(8, header.getSource());
			buffer.putLong(16, header.getDestination());
			buffer.putShort(24, header.getLength());
			buffer.putShort(26, header.getSequenceNumber());
			buffer.put(28, header.getSession());
			buffer.put(29, header.getCommand());
			buffer.put(30, header.getQualifier());
			buffer.put(31, header.getStatus());
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Return the deserialized header as a MessageHeader object
	 * 
	 * @return MessageHeader object containing deserialized header fields
	 */
	public MessageHeader getHeader() {
		MessageHeader header = new MessageHeader();
		header.setLabel(buffer.getLong(0));
		header.setSource(buffer.getLong(8));
		header.setDestination(buffer.getLong(16));
		header.setLength(buffer.getShort(24));
		header.setSequenceNumber(buffer.getShort(26));
		header.setSession(buffer.get(28));
		header.setCommand(buffer.get(29));
		header.setQualifier(buffer.get(30));
		header.setStatus(buffer.get(31));
		return header;
	}

	/**
	 * Returns label from message's header
	 * 
	 * @return this message's label
	 */
	public long getLabel() {
		return buffer.getLong(0);
	}

	/**
	 * Sets this message's label
	 * 
	 * @param label label of this message
	 */
	public void setLabel(long label) {
		buffer.putLong(0, label);
	}

	/**
	 * Returns source identifier from message's header
	 * 
	 * @return this message's source identifier
	 */
	public long getSource() {
		return buffer.getLong(8);
	}

	/**
	 * Sets this message's source identifier
	 * 
	 * @param source source identifier of this message
	 */
	public void setSource(long source) {
		buffer.putLong(8, source);
	}

	/**
	 * Returns destination identifier from message's header
	 * 
	 * @return this message's destination identifier
	 */
	public long getDestination() {
		return buffer.getLong(16);
	}

	/**
	 * Sets this message's destination identifier
	 * 
	 * @param destination destination identifier, message will be delivered to this
	 *                    destination
	 */
	public void setDestination(long destination) {
		buffer.putLong(16, destination);
	}

	/**
	 * Returns the length field from message's header
	 * 
	 * @return total length of the message as number of bytes
	 */
	public short getLength() {
		return buffer.getShort(24);
	}

	/**
	 * Sets this message's total length including header and payload
	 * 
	 * @param length number of bytes in this message
	 * @throws IndexOutOfBoundsException invalid length
	 */
	public void setLength(short length) {
		if (length >= HEADER_SIZE && length <= MTU) {
			buffer.putShort(24, length);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Returns the sequence number of this message
	 * 
	 * @return this message's sequence number
	 */
	public short getSequenceNumber() {
		return buffer.getShort(26);
	}

	/**
	 * Sets this message's sequence number in it's header
	 * 
	 * @param sequenceNumber sequence number of this message
	 */
	public void setSequenceNumber(short sequenceNumber) {
		buffer.putShort(26, sequenceNumber);
	}

	/**
	 * Returns the session identifier of this message
	 * 
	 * @return this message's session identifier
	 */
	public byte getSession() {
		return buffer.get(28);
	}

	/**
	 * Sets this message's session identifier in it's header
	 * 
	 * @param session session identifier of this message
	 */
	public void setSession(byte session) {
		buffer.put(28, session);
	}

	/**
	 * Returns the command classifier of this message
	 * 
	 * @return this message's command classifier
	 */
	public byte getCommand() {
		return buffer.get(29);
	}

	/**
	 * Sets this message's command classifier in it's header
	 * 
	 * @param command command classifier of this message
	 */
	public void setCommand(byte command) {
		buffer.put(29, command);
	}

	/**
	 * Returns the command qualifier of this message
	 * 
	 * @return this message's command qualifier
	 */
	public byte getQualifier() {
		return buffer.get(30);
	}

	/**
	 * Sets this message's command qualifier in it's header
	 * 
	 * @param qualifier command qualifier of this message
	 */
	public void setQualifier(byte qualifier) {
		buffer.put(30, qualifier);
	}

	/**
	 * Returns the request/response status of this message
	 * 
	 * @return this message's request/response status
	 */
	public byte getStatus() {
		return buffer.get(31);
	}

	/**
	 * Sets this message's request/response status in it's header
	 * 
	 * @param status request/response status of this message
	 */
	public void setStatus(byte status) {
		buffer.put(31, status);
	}

	/**
	 * Absolute getter for retrieving a byte value from the given offset in the
	 * payload
	 * 
	 * @param offset offset from which value will be read
	 * @return byte value at given offset
	 */
	public byte getByte(int offset) {
		return buffer.get(HEADER_SIZE + offset);
	}

	/**
	 * Absolute setter for writing a byte value at the given offset in the payload
	 * 
	 * @param offset offset at which value will be written
	 * @param value  byte value to be written at given offset
	 */
	public void setByte(int offset, byte value) {
		buffer.put(HEADER_SIZE + offset, value);
	}

	/**
	 * Absolute getter for retrieving a char value from the given offset in the
	 * payload
	 * 
	 * @param offset offset from which value will be read
	 * @return char value at given offset
	 */
	public char getChar(int offset) {
		return buffer.getChar(HEADER_SIZE + offset);
	}

	/**
	 * Absolute setter for writing a char value at the given offset in the payload
	 * 
	 * @param offset offset at which value will be written
	 * @param value  char value to be written at given offset
	 */
	public void setChar(int offset, char value) {
		buffer.putChar(HEADER_SIZE + offset, value);
	}

	/**
	 * Absolute getter for retrieving a short value from the given offset in the
	 * payload
	 * 
	 * @param offset offset from which value will be read
	 * @return short value at given offset
	 */
	public short getShort(int offset) {
		return buffer.getShort(HEADER_SIZE + offset);
	}

	/**
	 * Absolute setter for writing a short value at the given offset in the payload
	 * 
	 * @param offset offset at which value will be written
	 * @param value  short value to be written at given offset
	 */
	public void setShort(int offset, short value) {
		buffer.putShort(HEADER_SIZE + offset, value);
	}

	/**
	 * Absolute getter for retrieving a int value from the given offset in the
	 * payload
	 * 
	 * @param offset offset from which value will be read
	 * @return int value at given offset
	 */
	public int getInt(int offset) {
		return buffer.getInt(HEADER_SIZE + offset);
	}

	/**
	 * Absolute setter for writing an int value at the given offset in the payload
	 * 
	 * @param offset offset at which value will be written
	 * @param value  int value to be written at given offset
	 */
	public void setInt(int offset, int value) {
		buffer.putInt(HEADER_SIZE + offset, value);
	}

	/**
	 * Absolute getter for retrieving a long value from the given offset in the
	 * payload
	 * 
	 * @param offset offset from which value will be read
	 * @return long value at given offset
	 */
	public long getLong(int offset) {
		return buffer.getLong(HEADER_SIZE + offset);
	}

	/**
	 * Absolute setter for writing a long value at the given offset in the payload
	 * 
	 * @param offset offset at which value will be written
	 * @param value  long value to be written at given offset
	 */
	public void setLong(int offset, long value) {
		buffer.putLong(HEADER_SIZE + offset, value);
	}

	/**
	 * Absolute getter for retrieving a double value from the given offset in the
	 * payload
	 * 
	 * @param offset offset from which value will be read
	 * @return double value at given offset
	 */
	public double getDouble(int offset) {
		return buffer.getDouble(HEADER_SIZE + offset);
	}

	/**
	 * Absolute setter for writing a double value at the given offset in the payload
	 * 
	 * @param offset offset at which value will be written
	 * @param value  double value to be written at given offset
	 */
	public void setDouble(int offset, double value) {
		buffer.putDouble(HEADER_SIZE + offset, value);
	}

	/**
	 * Absolute getter for retrieving a byte array from the given offset in the
	 * payload
	 * 
	 * @param offset offset from which value will be read
	 * @param length length of the byte array
	 * @return byte array at the given offset
	 */
	public byte[] getBlob(int offset, int length) {
		byte[] blob = new byte[length];
		for (int i = 0; i < length; i++) {
			blob[i] = buffer.get(HEADER_SIZE + offset + i);
		}
		return blob;
	}

	/**
	 * Absolute setter for writing a byte array at the given offset in the payload
	 * 
	 * @param offset offset at which value will be written
	 * @param blob   array to be written at given offset
	 */
	public void setBlob(int offset, byte[] blob) {
		for (int i = 0; i < blob.length; i++) {
			buffer.put(HEADER_SIZE + offset + i, blob[i]);
		}
	}

	/**
	 * Match the message buffer's limit to the message length.
	 */
	public void freeze() {
		buffer.position(getLength());
		buffer.flip();
	}
}
