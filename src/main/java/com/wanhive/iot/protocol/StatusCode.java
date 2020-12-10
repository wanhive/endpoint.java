/*
 * StatusCode.java
 * 
 * The standard status codes for messaging
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

/**
 * The standard status codes for messaging
 * 
 * @author amit
 *
 */
public class StatusCode {
	/**
	 * A service request
	 */
	public static final byte REQUEST = (byte) 127;
	/**
	 * A success response
	 */
	public static final byte OK = (byte) 1;
	/**
	 * A Failure response
	 */
	public static final byte NOK = (byte) 0;
}
