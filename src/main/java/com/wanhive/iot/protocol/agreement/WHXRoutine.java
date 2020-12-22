/*
 * WHXRoutine.java
 * 
 * SRP-6a based private key (x) generator
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

package com.wanhive.iot.protocol.agreement;

import java.math.BigInteger;
import java.security.MessageDigest;

import com.nimbusds.srp6.BigIntegerUtils;
import com.nimbusds.srp6.XRoutine;

/**
 * The Private key generator for clients
 * 
 * @author amit
 *
 */
public class WHXRoutine implements XRoutine {
	private final int rounds;

	/**
	 * Constructor
	 * 
	 * @param rounds Password hashing rounds
	 */
	public WHXRoutine(int rounds) {
		this.rounds = rounds;
	}

	@Override
	public BigInteger computeX(MessageDigest digest, byte[] salt, byte[] username, byte[] password) {
		digest.reset();
		digest.update(username);
		digest.update((byte) ':');
		digest.update(password);
		byte[] x = digest.digest(); // H ( I | ":" | p)

		int i = rounds;
		do {
			digest.reset();
			digest.update(salt);
			digest.update(x);
			x = digest.digest();
		} while ((--i) > 0);

		return BigIntegerUtils.bigIntegerFromBytes(x);
	}
}
