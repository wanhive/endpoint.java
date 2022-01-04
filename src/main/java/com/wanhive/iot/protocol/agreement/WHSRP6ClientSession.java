/*
 * WHSRP6ClientSession.java
 * 
 * Client-side SRP-6a session manager
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

/*
 * This file incorporates work covered under the Apache License version 2.0
 * from nimbus-srp project.
 * Authors: Vladimir Dzhuvinov, Bernard Wittwer
 * Ref: https://connect2id.com/products/nimbus-srp/download
 */

package com.wanhive.iot.protocol.agreement;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;

import com.nimbusds.srp6.BigIntegerUtils;
import com.nimbusds.srp6.SRP6ClientCredentials;
import com.nimbusds.srp6.SRP6ClientEvidenceContext;
import com.nimbusds.srp6.SRP6CryptoParams;
import com.nimbusds.srp6.SRP6Exception;
import com.nimbusds.srp6.SRP6ServerEvidenceContext;
import com.nimbusds.srp6.SRP6Session;
import com.nimbusds.srp6.URoutineContext;
import com.nimbusds.srp6.XRoutine;
import com.wanhive.iot.protocol.bean.Identity;

/**
 * Client-side session manager for identification and authentication
 * 
 * @author amit
 *
 */
public class WHSRP6ClientSession extends SRP6Session {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The default SRP-6a key length in bits
	 */
	private static final int DEFAULT_KEY_LENGTH = 3072;
	/**
	 * The default SRP-6a message digest name
	 */
	private static final String DEFAULT_DIGEST_NAME = "SHA-512";

	/**
	 * Enumerates the states of a client-side SRP-6a authentication session.
	 */
	public static enum State {

		/**
		 * The session is initialised and ready to begin authentication by proceeding to
		 * {@link #STEP_1}.
		 */
		INIT,

		/**
		 * The authenticating user has input their identity 'I' (username) and password
		 * 'P'. The session is ready to proceed to {@link #STEP_2}.
		 */
		STEP_1,

		/**
		 * The user identity 'I' and The client public key 'A' is submitted to the
		 * server which has replied with the matching salt 's' and its public value 'B'
		 * based on the user's password verifier 'v'. The session is ready to proceed to
		 * {@link #STEP_3}.
		 */
		STEP_2,

		/**
		 * Evidence message 'M1' are submitted and the server has replied with own
		 * evidence message 'M2'. The session is finished (authentication was successful
		 * or failed).
		 */
		STEP_3
	}

	/**
	 * The user password 'P'.
	 */
	private byte[] password;

	/**
	 * The password key 'x'.
	 */
	private BigInteger x;

	/**
	 * The client private value 'a'.
	 */
	private BigInteger a;

	/**
	 * The current SRP-6a auth state.
	 */
	private State state;

	/**
	 * Custom routine for password key 'x' computation.
	 */
	private XRoutine xRoutine;

	/**
	 * Creates a new client-side SRP-6a authentication session and sets its state to
	 * {@link State#INIT}.
	 *
	 * @param timeout The SRP-6a authentication session timeout in seconds. If the
	 *                authenticating counter-party (server or client) fails to
	 *                respond within the specified time the session will be closed.
	 *                If zero timeouts are disabled.
	 */
	private WHSRP6ClientSession(int timeout) {
		super(timeout);
		updateLastActivityTime();
		state = State.INIT;
	}

	/**
	 * Helper method for {@link #step1()}
	 */
	private void computeA() {
		MessageDigest digest = config.getMessageDigestInstance();
		digest.reset();
		this.a = srp6Routines.generatePrivateValue(config.N, random);
		digest.reset();
		this.A = srp6Routines.computePublicClientValue(config.N, config.g, a);
	}

	/**
	 * Helper method for {@link #step2(BigInteger, BigInteger)}
	 */
	private void computeX() {
		MessageDigest digest = config.getMessageDigestInstance();

		digest.reset();
		// Compute the password key 'x'
		if (xRoutine != null) {
			// With custom routine
			this.x = xRoutine.computeX(config.getMessageDigestInstance(), BigIntegerUtils.bigIntegerToBytes(s),
					userID.getBytes(Charset.forName("UTF-8")), password);

		} else {
			// With default routine
			this.x = srp6Routines.computeX(digest, BigIntegerUtils.bigIntegerToBytes(s), password);
		}
	}

	/**
	 * Helper method for {@link #step2(BigInteger, BigInteger)}
	 */
	private void computeSessionKey() {
		MessageDigest digest = config.getMessageDigestInstance();

		digest.reset();
		this.k = srp6Routines.computeK(digest, config.N, config.g);

		digest.reset();
		if (hashedKeysRoutine != null) {
			URoutineContext hashedKeysContext = new URoutineContext(A, B);
			this.u = hashedKeysRoutine.computeU(config, hashedKeysContext);
		} else {
			this.u = srp6Routines.computeU(digest, config.N, A, B);
		}

		this.S = srp6Routines.computeSessionKey(config.N, config.g, k, x, u, a, B);
	}

	/**
	 * Helper method for {@link #step2(BigInteger, BigInteger)}
	 */
	private void computeClientEvidence() {
		MessageDigest digest = config.getMessageDigestInstance();
		digest.reset();
		if (clientEvidenceRoutine != null) {
			// With custom routine
			SRP6ClientEvidenceContext ctx = new SRP6ClientEvidenceContext(userID, s, A, B, S);
			this.M1 = clientEvidenceRoutine.computeClientEvidence(config, ctx);
		} else {
			// With default routine
			this.M1 = srp6Routines.computeClientEvidence(digest, A, B, S);
		}
	}

	/**
	 * Helper method for {@link #step3(BigInteger)}
	 * 
	 * @return The computed server evidence
	 */
	private BigInteger computeM2() {
		MessageDigest digest = config.getMessageDigestInstance();
		digest.reset();
		if (serverEvidenceRoutine != null) {
			// With custom routine
			SRP6ServerEvidenceContext ctx = new SRP6ServerEvidenceContext(A, M1, S);
			return serverEvidenceRoutine.computeServerEvidence(config, ctx);
		} else {
			// With default routine: NONE
			return null;
		}
	}

	/**
	 * Creates and returns the default client session
	 * 
	 * @param userID   The identity 'I' of the authenticating user, UTF-8 encoded.
	 *                 Must not be {@code null} or empty.
	 * @param password The user password 'P', UTF-8 encoded. Must not be
	 *                 {@code null}.
	 * @param rounds   The password hashing rounds
	 * @return The client session object
	 */
	public static WHSRP6ClientSession getDefaultSession(Identity id, int timeout) {
		WHSRP6ClientSession session = new WHSRP6ClientSession(timeout);
		session.setClientEvidenceRoutine(new WHClientEvidenceRoutine());
		session.setServerEvidenceRoutine(new WHServerEvidenceRoutine());

		session.config = SRP6CryptoParams.getInstance(DEFAULT_KEY_LENGTH, DEFAULT_DIGEST_NAME);
		session.userID = Long.toString(id.getUid());
		session.password = id.getPassword();
		session.xRoutine = new WHXRoutine(id.getRounds());
		return session;
	}

	/**
	 * Records the identity 'I' and password 'P' of the authenticating user. The
	 * session is incremented to {@link State#STEP_1}.
	 * 
	 */
	public void step1() {
		// Check current state
		if (state != State.INIT) {
			throw new IllegalStateException("State violation: not in INIT state");
		}

		// Check arguments
		if (this.config == null || this.config.getMessageDigestInstance() == null) {
			throw new IllegalArgumentException("The SRP-6a crypto parameters must not be null");
		}

		if (this.userID == null || this.userID.trim().isEmpty()) {
			throw new IllegalArgumentException("The user identity 'I' must not be null or empty");
		}

		if (this.password == null) {
			throw new IllegalArgumentException("The user password 'P' must not be null");
		}

		// Generate client private and public values
		computeA();

		// Increment the state
		state = State.STEP_1;
		updateLastActivityTime();
	}

	/**
	 * Receives the password salt 's' and public value 'B' from the server. The
	 * SRP-6a crypto parameters are also set. The session is incremented to
	 * {@link State#STEP_2}.
	 *
	 * <p>
	 * Argument origin:
	 * 
	 * <ul>
	 * <li>From server: password salt 's', public value 'B'.
	 * <li>From server or pre-agreed: crypto parameters prime 'N', generator 'g' and
	 * hash function 'H'.
	 * </ul>
	 *
	 * @param s The password salt 's'. Must not be {@code null}.
	 * @param B The public server value 'B'. Must not be {@code null}.
	 *
	 * @return the client evidence message 'M1'.
	 *
	 * @throws SRP6Exception If the session has timed out or the public server value
	 *                       'B' is invalid.
	 */
	public SRP6ClientCredentials step2(final BigInteger s, final BigInteger B) throws SRP6Exception {
		// Check current state
		if (state != State.STEP_1) {
			throw new IllegalStateException("State violation: Session must be in STEP_1 state");
		}

		// Check timeout
		if (hasTimedOut()) {
			throw new SRP6Exception("Session timeout", SRP6Exception.CauseType.TIMEOUT);
		}

		// Check salt
		if (s == null) {
			throw new IllegalArgumentException("The salt 's' must not be null");
		}

		// Check B validity
		if (B == null || !srp6Routines.isValidPublicValue(config.N, B)) {
			throw new SRP6Exception("Bad server public value 'B'", SRP6Exception.CauseType.BAD_PUBLIC_VALUE);
		}

		this.s = s;
		this.B = B;

		// Compute the password key 'x'
		computeX();
		// Compute the session key
		computeSessionKey();
		// Compute the client evidence message
		computeClientEvidence();
		// Increment the state
		state = State.STEP_2;
		updateLastActivityTime();
		return new SRP6ClientCredentials(A, M1);
	}

	/**
	 * Receives the server evidence message 'M1'. The session is incremented to
	 * {@link State#STEP_3}.
	 * 
	 * <p>
	 * Argument origin:
	 * 
	 * <ul>
	 * <li>From server: evidence message 'M2'.
	 * </ul>
	 * 
	 * @param M2 The server evidence message 'M2'. Must not be {@code null}.
	 * 
	 * @throws IllegalStateException If the method is invoked in a state other than
	 *                               {@link State#STEP_2}.
	 * @throws SRP6Exception         If the session has timed out or the server
	 *                               evidence message 'M2' is invalid.
	 */
	public void step3(final BigInteger M2) throws SRP6Exception {
		// Check current state
		if (state != State.STEP_2) {
			throw new IllegalStateException("State violation: Session must be in STEP_2 state");
		}

		// Check timeout
		if (hasTimedOut()) {
			throw new SRP6Exception("Session timeout", SRP6Exception.CauseType.TIMEOUT);
		}

		// Check argument
		if (M2 == null) {
			throw new IllegalArgumentException("The server evidence message 'M2' must not be null");
		}

		this.M2 = M2;
		// Compute the own server evidence message 'M2'
		BigInteger computedM2 = computeM2();
		// Verify M2
		if (computedM2 != null && !computedM2.equals(M2)) {
			throw new SRP6Exception("Bad server credentials", SRP6Exception.CauseType.BAD_CREDENTIALS);
		}

		// Increment the state
		state = State.STEP_3;
		updateLastActivityTime();
	}

	/**
	 * Returns the current state of this SRP-6a authentication session.
	 *
	 * @return The current state.
	 */
	public State getState() {
		return state;
	}
}
