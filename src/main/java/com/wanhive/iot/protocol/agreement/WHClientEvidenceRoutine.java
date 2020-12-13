/*
 * WHClientEvidenceRoutine.java
 * 
 * Customized SRP-6a class for generating client's proof
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
import com.nimbusds.srp6.ClientEvidenceRoutine;
import com.nimbusds.srp6.SRP6ClientEvidenceContext;
import com.nimbusds.srp6.SRP6CryptoParams;
import com.nimbusds.srp6.SRP6Routines;

/**
 * Generates client's proof
 * 
 * @author amit
 *
 */
public class WHClientEvidenceRoutine extends SRP6Routines implements ClientEvidenceRoutine {

	private static final long serialVersionUID = 1L;

	@Override
	public BigInteger computeClientEvidence(SRP6CryptoParams cryptoParams, SRP6ClientEvidenceContext ctx) {
		final int padLength = (cryptoParams.N.bitLength() + 7) / 8;
		MessageDigest digest = cryptoParams.getMessageDigestInstance();
		digest.reset();

		digest.update(getPadded(ctx.A, padLength));
		digest.update(getPadded(ctx.B, padLength));
		digest.update(getPadded(ctx.S, padLength));
		return BigIntegerUtils.bigIntegerFromBytes(digest.digest());
	}

}
