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

import com.wanhive.iot.protocol.bean.MessageAddress;
import com.wanhive.iot.protocol.bean.MessageContext;
import com.wanhive.iot.protocol.bean.MessageControl;
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
	 * Stores the raw message data
	 */
	private final ByteBuffer buffer;
	/**
	 * The payload handler
	 */
	private final Payload payload;
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
	 * Returns the backing array that stores the message data
	 * 
	 * @return The byte array that stores the message data
	 */
	byte[] getBuffer() {
		return buffer.array();
	}

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
	 * Create a new message. MTU is the default message length.
	 */
	public Message() {
		buffer = ByteBuffer.allocate(MTU);
		payload = new Payload(buffer, HEADER_SIZE);
		setLength((short) MTU);
		setLabel(0);
	}

	/**
	 * Populates message's header. Doesn't modify the label.
	 * 
	 * @param address The message address structure
	 * @param ctrl    The message flow control structure
	 * @param ctx     The message context
	 * @return This message
	 */
	public Message prepareHeader(MessageAddress address, MessageControl ctrl, MessageContext ctx) {
		return this.setLength(ctrl.getLength()).setSource(address.getSource()).setDestination(address.getDestination())
				.setSequenceNumber(ctrl.getSequenceNumber()).setSession(ctrl.getSession()).setCommand(ctx.getCommand())
				.setQualifier(ctx.getQualifier()).setStatus(ctx.getStatus());
	}

	/**
	 * Populates message's header
	 * 
	 * @param label   Label of this message
	 * @param address The message address structure
	 * @param ctrl    The message flow control structure
	 * @param ctx     The message context
	 * @return this message
	 */
	public Message prepareHeader(long label, MessageAddress address, MessageControl ctrl, MessageContext ctx) {
		return this.prepareHeader(address, ctrl, ctx).setLabel(label);
	}

	/**
	 * Populates this message's header
	 * 
	 * @param header Desired message header
	 * @return This message
	 */
	public Message prepareHeader(MessageHeader header) {
		return prepareHeader(header.getLabel(), header.getAddress(), header.getControl(), header.getContext());
	}

	/**
	 * Deserializes and returns the message header
	 * 
	 * @param header This message's header data is copied here
	 */
	public void getHeader(MessageHeader header) {
		header.setLabel(getLabel());
		header.getAddress().set(getSource(), getDestination());
		header.getControl().set(getLength(), getSequenceNumber(), getSession());
		header.getContext().set(getCommand(), getQualifier(), getStatus());
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
			buffer.limit(length);
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

	public Payload getPayload() {
		return this.payload;
	}

	/**
	 * Returns the number of messages required for carrying the given bytes of data
	 * as payload
	 * 
	 * @param bytes Data size in bytes
	 * @return Message count
	 */
	public static long packets(int bytes) {
		if (bytes < 0) {
			throw new IllegalArgumentException(BAD_MSG_LENGTH);
		} else if (bytes <= PAYLOAD_SIZE) {
			return 1;
		} else {
			return ((long) bytes + PAYLOAD_SIZE - 1) / PAYLOAD_SIZE;
		}
	}
}
