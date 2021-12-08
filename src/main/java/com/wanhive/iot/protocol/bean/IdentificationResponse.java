/*
 * IdentificationResponse.java
 * 
 * Stores the public ephemeral value and salt returned by a host
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
 * Public 'ephemeral value' and 'salt' returned by a host during identification
 * 
 * @author amit
 *
 */
public class IdentificationResponse {
	/**
	 * Nonce returned by the authentication hub
	 */
	private byte[] nonce;
	/**
	 * Salt returned by the authentication hub
	 */
	private byte[] salt;

	/**
	 * Returns the nonce
	 * 
	 * @return A byte array containing the nonce
	 */
	public byte[] getNonce() {
		return nonce;
	}

	/**
	 * Sets the nonce
	 * 
	 * @param nonce A byte array containing the nonce
	 */
	public void setNonce(byte[] nonce) {
		this.nonce = nonce;
	}

	/**
	 * Returns the salt
	 * 
	 * @return A byte array containing the salt
	 */
	public byte[] getSalt() {
		return salt;
	}

	/**
	 * Sets the salt
	 * 
	 * @param salt A byte array containing the salt
	 */
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}
}
