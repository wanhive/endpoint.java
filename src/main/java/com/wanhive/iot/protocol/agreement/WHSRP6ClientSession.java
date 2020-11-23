/*
 * WHSRP6ClientSession.java
 * 
 * SRP-6a client session manager
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

public class WHSRP6ClientSession extends SRP6Session {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	private BigInteger x = null;

	/**
	 * The client private value 'a'.
	 */
	private BigInteger a = null;

	/**
	 * The current SRP-6a auth state.
	 */
	private State state;

	/**
	 * Custom routine for password key 'x' computation.
	 */
	private XRoutine xRoutine = null;

	/**
	 * Creates a new client-side SRP-6a authentication session and sets its state to
	 * {@link State#INIT}.
	 *
	 * @param timeout The SRP-6a authentication session timeout in seconds. If the
	 *                authenticating counterparty (server or client) fails to
	 *                respond within the specified time the session will be closed.
	 *                If zero timeouts are disabled.
	 */
	public WHSRP6ClientSession(final int timeout) {

		super(timeout);

		state = State.INIT;

		updateLastActivityTime();
	}

	/**
	 * Creates a new client-side SRP-6a authentication session and sets its state to
	 * {@link State#INIT}. Session timeouts are disabled.
	 */
	public WHSRP6ClientSession() {

		this(0);
	}

	/**
	 * Creates and returns default session suitable for Wanhive
	 * 
	 * @param rounds Number of password hashing rounds
	 * 
	 * @return new WHSRP6ClientSession object suitable for Wanhive
	 */
	public static WHSRP6ClientSession getDefaultSession(int rounds) {
		WHSRP6ClientSession session = new WHSRP6ClientSession();
		session.setXRoutine(new WHXRoutine(rounds));
		session.setClientEvidenceRoutine(new WHClientEvidenceRoutine());
		session.setServerEvidenceRoutine(new WHServerEvidenceRoutine());
		return session;
	}

	/**
	 * Creates and returns default configuration suitable for Wanhive
	 * 
	 * @return SRP6CryptoParams object suitable for Wanhive
	 */
	public static SRP6CryptoParams getDefaultConfig() {
		return SRP6CryptoParams.getInstance(2048, "SHA-512");
	}

	/**
	 * Sets a custom routine for the password key 'x' computation. Note that the
	 * custom routine must be set prior to {@link State#STEP_2}.
	 *
	 * @param routine The password key 'x' routine or {@code null} to use the
	 *                {@link SRP6Routines#computeX default one} instead.
	 */
	public void setXRoutine(final XRoutine routine) {

		xRoutine = routine;
	}

	/**
	 * Gets the custom routine for the password key 'x' computation.
	 *
	 * @return The routine instance or {@code null} if the default
	 *         {@link SRP6Routines#computeX default one} is used.
	 */
	public XRoutine getXRoutine() {

		return xRoutine;
	}

	/**
	 * Records the identity 'I' and password 'P' of the authenticating user. The
	 * session is incremented to {@link State#STEP_1}.
	 * 
	 * <p>
	 * Argument origin:
	 * 
	 * <ul>
	 * <li>From user: user identity 'I' and password 'P'.
	 * </ul>
	 * 
	 * @param config   The SRP-6a crypto parameters. Must not be {@code null}.
	 * @param userID   The identity 'I' of the authenticating user, UTF-8 encoded.
	 *                 Must not be {@code null} or empty.
	 * @param password The user password 'P', UTF-8 encoded. Must not be
	 *                 {@code null}.
	 * 
	 * @throws IllegalStateException If the method is invoked in a state other than
	 *                               {@link State#INIT}.
	 */
	public void step1(final SRP6CryptoParams config, final String userID, final byte[] password) {
		// Check arguments
		if (config == null)
			throw new IllegalArgumentException("The SRP-6a crypto parameters must not be null");

		this.config = config;

		MessageDigest digest = config.getMessageDigestInstance();

		if (userID == null || userID.trim().isEmpty()) {
			throw new IllegalArgumentException("The user identity 'I' must not be null or empty");
		}

		this.userID = userID;

		if (password == null) {
			throw new IllegalArgumentException("The user password 'P' must not be null");
		}

		this.password = password;

		// Check current state
		if (state != State.INIT)
			throw new IllegalStateException("State violation: Session must be in INIT state");

		// Generate client private and public values
		a = srp6Routines.generatePrivateValue(config.N, random);
		digest.reset();

		A = srp6Routines.computePublicClientValue(config.N, config.g, a);

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
	 * @throws IllegalStateException If the method is invoked in a state other than
	 *                               {@link State#STEP_1}.
	 * @throws SRP6Exception         If the session has timed out or the public
	 *                               server value 'B' is invalid.
	 */
	public SRP6ClientCredentials step2(final BigInteger s, final BigInteger B) throws SRP6Exception {

		MessageDigest digest = config.getMessageDigestInstance();

		if (digest == null)
			throw new IllegalArgumentException("Unsupported hash algorithm 'H': " + config.H);

		if (s == null)
			throw new IllegalArgumentException("The salt 's' must not be null");

		this.s = s;

		if (B == null)
			throw new IllegalArgumentException("The public server value 'B' must not be null");

		this.B = B;

		// Check current state
		if (state != State.STEP_1)
			throw new IllegalStateException("State violation: Session must be in STEP_1 state");

		// Check timeout
		if (hasTimedOut())
			throw new SRP6Exception("Session timeout", SRP6Exception.CauseType.TIMEOUT);

		// Check B validity
		if (!srp6Routines.isValidPublicValue(config.N, B))
			throw new SRP6Exception("Bad server public value 'B'", SRP6Exception.CauseType.BAD_PUBLIC_VALUE);

		// Compute the password key 'x'
		if (xRoutine != null) {

			// With custom routine
			x = xRoutine.computeX(config.getMessageDigestInstance(), BigIntegerUtils.bigIntegerToBytes(s),
					userID.getBytes(Charset.forName("UTF-8")), password);

		} else {
			// With default routine
			x = srp6Routines.computeX(digest, BigIntegerUtils.bigIntegerToBytes(s), password);
			digest.reset();
		}

		// Compute the session key
		k = srp6Routines.computeK(digest, config.N, config.g);
		digest.reset();

		if (hashedKeysRoutine != null) {
			URoutineContext hashedKeysContext = new URoutineContext(A, B);
			u = hashedKeysRoutine.computeU(config, hashedKeysContext);
		} else {
			u = srp6Routines.computeU(digest, config.N, A, B);
			digest.reset();
		}

		S = srp6Routines.computeSessionKey(config.N, config.g, k, x, u, a, B);

		// Compute the client evidence message
		if (clientEvidenceRoutine != null) {
			// With custom routine
			SRP6ClientEvidenceContext ctx = new SRP6ClientEvidenceContext(userID, s, A, B, S);
			M1 = clientEvidenceRoutine.computeClientEvidence(config, ctx);

		} else {
			// With default routine
			M1 = srp6Routines.computeClientEvidence(digest, A, B, S);
			digest.reset();
		}

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

		// Check argument

		if (M2 == null)
			throw new IllegalArgumentException("The server evidence message 'M2' must not be null");

		this.M2 = M2;

		// Check current state
		if (state != State.STEP_2)
			throw new IllegalStateException("State violation: Session must be in STEP_2 state");

		// Check timeout
		if (hasTimedOut())
			throw new SRP6Exception("Session timeout", SRP6Exception.CauseType.TIMEOUT);

		// Compute the own server evidence message 'M2'
		BigInteger computedM2 = null;

		if (serverEvidenceRoutine != null) {

			// With custom routine
			SRP6ServerEvidenceContext ctx = new SRP6ServerEvidenceContext(A, M1, S);

			computedM2 = serverEvidenceRoutine.computeServerEvidence(config, ctx);

		} else {
			// With default routine
			// MessageDigest digest = config.getMessageDigestInstance();
			// computedM2 = srp6Routines.computeServerEvidence(digest, A, M1, S);
		}

		if (computedM2 != null && !computedM2.equals(M2)) {
			throw new SRP6Exception("Bad server credentials", SRP6Exception.CauseType.BAD_CREDENTIALS);
		}

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
