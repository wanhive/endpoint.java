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
	 * Stores the raw message data
	 */
	private final ByteBuffer buffer;
	/**
	 * The header structure
	 */
	private final Header header;
	/**
	 * The payload structure
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
	 * Create a new message. MTU is the default message length.
	 */
	public Message() {
		buffer = ByteBuffer.allocate(Packet.MTU);
		header = new Header(buffer);
		payload = new Payload(buffer, Packet.HEADER_SIZE);
		header.setLength((short) Packet.MTU);
		header.setLabel(0);
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
		header().setLength(ctrl.getLength()).setSource(address.getSource()).setDestination(address.getDestination())
				.setSequenceNumber(ctrl.getSequenceNumber()).setSession(ctrl.getSession()).setCommand(ctx.getCommand())
				.setQualifier(ctx.getQualifier()).setStatus(ctx.getStatus());
		return this;
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
		prepareHeader(address, ctrl, ctx);
		header().setLabel(label);
		return this;
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
	 * Returns the header
	 * 
	 * @return This message's header
	 */
	public Header header() {
		return header;
	}

	/**
	 * Returns the payload
	 * 
	 * @return This message's payload
	 */
	public Payload payload() {
		return this.payload;
	}
}
