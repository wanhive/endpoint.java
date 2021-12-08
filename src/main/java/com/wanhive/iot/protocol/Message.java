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
	/**
	 * Serialized message data
	 */
	private final ByteBuffer buffer;
	/**
	 * The header
	 */
	private final Header header;
	/**
	 * The payload
	 */
	private final Payload payload;

	/**
	 * Returns the backing array that stores the message data
	 * 
	 * @return The byte array that stores the message data
	 */
	byte[] getBuffer() {
		return buffer.array();
	}

	/**
	 * Create a new {@link Message}. MTU is the default message length.
	 */
	public Message() {
		buffer = ByteBuffer.allocate(Packet.MTU);
		header = new Header(buffer);
		payload = new Payload(buffer, Packet.HEADER_SIZE);
		header.setLength((short) Packet.MTU);
		header.setLabel(0);
	}

	/**
	 * Populates the header. Doesn't modify the label.
	 * 
	 * @param address The message address structure
	 * @param control The message flow control structure
	 * @param context The message context
	 * @return {@code this} {@link Message}
	 */
	public Message setHeader(MessageAddress address, MessageControl control, MessageContext context) {
		// Length should be set first
		header().setLength(control.getLength()).setSource(address.getSource()).setDestination(address.getDestination())
				.setSequenceNumber(control.getSequenceNumber()).setSession(control.getSession())
				.setCommand(context.getCommand()).setQualifier(context.getQualifier()).setStatus(context.getStatus());
		return this;
	}

	/**
	 * Populates the header
	 * 
	 * @param label   Label of this message
	 * @param address The message address structure
	 * @param control The message flow control structure
	 * @param context The message context
	 * @return {@code this} {@link Message}
	 */
	public Message setHeader(long label, MessageAddress address, MessageControl control, MessageContext context) {
		setHeader(address, control, context);
		header().setLabel(label);
		return this;
	}

	/**
	 * Populates the header
	 * 
	 * @param header Desired message header
	 * @return {@code this} {@link Message}
	 */
	public Message setHeader(MessageHeader header) {
		return setHeader(header.getLabel(), header.getAddress(), header.getControl(), header.getContext());
	}

	/**
	 * Returns the header
	 * 
	 * @return The {@link Header}
	 */
	public Header header() {
		return header;
	}

	/**
	 * Returns the payload
	 * 
	 * @return The {@link Payload}
	 */
	public Payload payload() {
		return this.payload;
	}
}
