/*
 * WHEvidenceRoutine.java
 * 
 * Helper class for generating SRP-6a proof
 * 
 * This program is part of Wanhive IoT Platform.
 * 
 * Apache-2.0 License
 * Copyright 2021 Wanhive Systems Private Limited
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
import com.nimbusds.srp6.SRP6CryptoParams;
import com.nimbusds.srp6.SRP6Routines;

/**
 * Helper class for generating SRP-6a proofs
 * 
 * @author amit
 *
 */
public class WHEvidenceRoutine extends SRP6Routines {

	private static final long serialVersionUID = 1L;

	/**
	 * Computes proof
	 * 
	 * @param cryptoParams The cryptographic parameters for the SRP-6a protocol
	 * @param X            The first parameter for proof generation
	 * @param Y            The second Parameter for proof generation
	 * @param Z            The third parameter for proof generation
	 * @return The generated proof
	 */
	BigInteger computeEvidence(SRP6CryptoParams cryptoParams, BigInteger X, BigInteger Y, BigInteger Z) {
		final int padLength = (cryptoParams.N.bitLength() + 7) / 8;
		MessageDigest digest = cryptoParams.getMessageDigestInstance();
		digest.reset();

		digest.update(getPadded(X, padLength));
		digest.update(getPadded(Y, padLength));
		digest.update(getPadded(Z, padLength));
		return BigIntegerUtils.bigIntegerFromBytes(digest.digest());
	}
}
