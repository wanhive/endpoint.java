/*
 * Identity.java
 * 
 * Endpoint's identity
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

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Endpoint's credentials for identification and authentication
 * 
 * @author amit
 *
 */
public class Identity implements Serializable {
	/**
	 * The version number
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Unique identifier
	 */
	private final AtomicLong uid = new AtomicLong();
	/**
	 * Password
	 */
	private byte[] password;
	/**
	 * Password hashing rounds
	 */
	private int rounds;

	/**
	 * The default constructor
	 */
	public Identity() {

	}

	/**
	 * The constructor
	 * 
	 * @param uid      UID of the endpoint
	 * @param password Password of the endpoint
	 * @param rounds   Password hashing rounds
	 */
	public Identity(long uid, byte[] password, int rounds) {
		this.uid.set(uid);
		this.password = password;
		this.rounds = rounds;
	}

	/**
	 * Returns the Unique Identifier of the endpoint
	 * 
	 * @return The long value containing the unique identifier
	 */
	public long getUid() {
		return uid.get();
	}

	/**
	 * Sets the unique identifier
	 * 
	 * @param uid The unique identifier will be set to this value
	 */
	public void setUid(long uid) {
		this.uid.set(uid);
	}

	/**
	 * Returns the password
	 * 
	 * @return The byte array of password
	 */
	public byte[] getPassword() {
		return password;
	}

	/**
	 * Sets the password
	 * 
	 * @param password The byte array containing the password
	 */
	public void setPassword(byte[] password) {
		this.password = password;
	}

	/**
	 * Returns the password hashing rounds
	 * 
	 * @return The password hashing rounds
	 */
	public int getRounds() {
		return rounds;
	}

	/**
	 * Sets the password hashing rounds
	 * 
	 * @param rounds The desired password hashing rounds
	 */
	public void setRounds(int rounds) {
		this.rounds = rounds;
	}
}
