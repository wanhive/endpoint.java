/*
 * RequestContext.java
 * 
 * The standard request contexts
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

import com.wanhive.iot.protocol.bean.MessageContext;

/**
 * The standard request contexts
 * 
 * @author amit
 *
 */
public class RequestContext {
	/**
	 * Identification request
	 */
	public static final MessageContext IDENTIFY = new MessageContext((byte) 0, (byte) 1, StatusCode.REQUEST);
	/**
	 * Authentication request
	 */
	public static final MessageContext AUTHENTICATE = new MessageContext((byte) 0, (byte) 2, StatusCode.REQUEST);
	/**
	 * Registration request
	 */
	public static final MessageContext REGISTER = new MessageContext((byte) 1, (byte) 0, StatusCode.REQUEST);
	/**
	 * Session key request
	 */
	public static final MessageContext GETKEY = new MessageContext((byte) 1, (byte) 1, StatusCode.REQUEST);
	/**
	 * Bootstrap request
	 */
	public static final MessageContext FINDROOT = new MessageContext((byte) 1, (byte) 2, StatusCode.REQUEST);
	/**
	 * Publish (to a topic) request
	 */
	public static final MessageContext PUBLISH = new MessageContext((byte) 2, (byte) 0, StatusCode.REQUEST);
	/**
	 * Subscription (to a topic) request
	 */
	public static final MessageContext SUBSCRIBE = new MessageContext((byte) 2, (byte) 1, StatusCode.REQUEST);
	/**
	 * Unsubscription (from a topic) request
	 */
	public static final MessageContext UNSUBSCRIBE = new MessageContext((byte) 2, (byte) 2, StatusCode.REQUEST);
}
