/*
 * MessageHeader.java
 * 
 * Message header
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

package com.wanhive.iot.protocol.bean;

/**
 * Fixed size message header compliant with the Wanhive protocol
 * 
 * @author amit
 *
 */
public final class MessageHeader {
	/**
	 * The label identifier (implementation dependent)
	 */
	private long label;
	/**
	 * Message address
	 */
	private final MessageAddress address;
	/**
	 * Message flow control
	 */
	private final MessageControl control;
	/**
	 * Message context
	 */
	private final MessageContext context;

	/**
	 * The message header size in bytes
	 */
	public static final int SIZE = 32;

	public MessageHeader() {
		this.label = 0;
		this.address = new MessageAddress();
		this.control = new MessageControl();
		this.context = new MessageContext();
	}

	/**
	 * Returns the label
	 * 
	 * @return The label identifier
	 */
	public long getLabel() {
		return label;
	}

	/**
	 * Sets the label
	 * 
	 * @param label The label identifier
	 */
	public void setLabel(long label) {
		this.label = label;
	}

	/**
	 * Returns the message address structure
	 * 
	 * @return The MessageAddress object
	 */
	public MessageAddress getAddress() {
		return address;
	}

	/**
	 * Returns the message flow control structure
	 * 
	 * @return The MessageControl object
	 */
	public MessageControl getControl() {
		return control;
	}

	/**
	 * Returns the message context structure
	 * 
	 * @return The MessageContext object
	 */
	public MessageContext getContext() {
		return context;
	}

	/**
	 * Copies the message address information into this object
	 * 
	 * @param address The MessageAddress object
	 * @return This object
	 */
	public MessageHeader importAddress(MessageAddress address) {
		getAddress().set(address.getSource(), address.getDestination());
		return this;
	}

	/**
	 * Copies the message flow control information into this object
	 * 
	 * @param control The MessageControl object
	 * @return This object
	 */
	public MessageHeader importControl(MessageControl control) {
		getControl().setLength(control.getLength());
		getControl().setSequenceNumber(control.getSequenceNumber());
		getControl().setSession(control.getSession());
		return this;
	}

	/**
	 * Copies the message context information into this object
	 * 
	 * @param context The MessageContext object
	 * @return This object
	 */
	public MessageHeader importContext(MessageContext context) {
		getContext().set(context.getCommand(), context.getQualifier(), context.getStatus());
		return this;
	}
}
