/*
 * ClientFactory.java
 * 
 * Creates a client
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

import java.io.IOException;
import java.net.ProtocolException;

import com.nimbusds.srp6.BigIntegerUtils;
import com.nimbusds.srp6.SRP6Exception;
import com.wanhive.iot.protocol.agreement.WHSRP6ClientSession;
import com.wanhive.iot.protocol.bean.IdentificationResponse;
import com.wanhive.iot.protocol.bean.Identity;
import com.wanhive.iot.protocol.hosts.Hosts;

/**
 * Creates a client
 * 
 * @author amit
 *
 */
public class ClientFactory {
	private static final String AUTHENTICATION_FAIL = "Authentication failed";
	private static final String BOOTSTRAP_FAIL = "Bootstrapping failed";

	private final Hosts hosts;
	private final long[] authNodes;
	private final long[] bootNodes;

	/**
	 * Configures the trust store
	 * 
	 * @param path     Absolute path to the JKS file
	 * @param password Trust store's password
	 */
	public static void setTrustStore(String path, String password) {
		System.setProperty("javax.net.ssl.trustStoreType", "jks");
		System.setProperty("javax.net.ssl.trustStore", path);
		System.setProperty("javax.net.ssl.trustStorePassword", password);
	}

	/**
	 * Constructor
	 * 
	 * @param hosts     The {@link Hosts} database for name resolution
	 * @param authNodes The authenticator nodes
	 * @param bootNodes The bootstrap nodes
	 */
	public ClientFactory(Hosts hosts, long[] authNodes, long[] bootNodes) {
		this.hosts = hosts;
		this.authNodes = authNodes;
		this.bootNodes = bootNodes;
	}

	/**
	 * Connects with the Wanhive network
	 * 
	 * @param identity {@link Identity} of the client
	 * @param timeout  Socket read timeout in milliseconds (during handshaking)
	 * @param secure   If true then SSL/TLS connection will be established
	 * @return The {@link Client} connected to the wanhive network
	 * @throws ProtocolException Could not connect to the network
	 */
	public Client createClient(Identity identity, int timeout, boolean secure) throws ProtocolException {
		try (WanhiveClient auth = authenticate(identity, timeout, secure)) {
			return bootstrap(identity, auth, timeout, secure);
		}
	}

	/**
	 * Helper method for {@link #createClient(Identity, int, boolean)}
	 * 
	 * @param identity Client's {@link Identity}
	 * @param timeout  Connection timeout during handshake
	 * @param secure   If true then SSL/TLS connection will be established
	 * @return A {@link WanhiveClient} connected to authentication server
	 * @throws ProtocolException
	 */
	private WanhiveClient authenticate(Identity identity, int timeout, boolean secure) throws ProtocolException {
		if (identity.getPassword() == null || identity.getPassword().length == 0) {
			return null;
		}

		boolean connected = false;
		for (long node : authNodes) {
			if (connected) { // Something went bad
				break;
			}
			try (WanhiveClient auth = new WanhiveClient(hosts.get(node), timeout, secure)) {
				connected = true;
				authenticate(auth, identity);
				return new WanhiveClient(auth.release());
			} catch (Exception e) {

			}
		}

		throw new ProtocolException(AUTHENTICATION_FAIL);
	}

	/**
	 * Helper method for {@link #authenticate(Identity, int, boolean)}
	 * 
	 * @param host The {@link Client} connection to authentication server
	 * @param id   Client's {@link Identity}
	 * @throws IOException
	 * @throws SRP6Exception
	 */
	private static void authenticate(Client host, Identity id) throws IOException, SRP6Exception {
		Protocol protocol = new Protocol();
		// -----------------------------------------------------------------
		/*
		 * Identification
		 */
		WHSRP6ClientSession session = WHSRP6ClientSession.getDefaultSession(id, 0);
		session.step1();
		Message message = protocol.createIdentificationRequest(id.getUid(),
				BigIntegerUtils.bigIntegerToBytes(session.getPublicClientValue()));
		message = host.execute(message);
		IdentificationResponse iresp = protocol.processIdentificationResponse(message);
		// -----------------------------------------------------------------
		/*
		 * Authentication
		 */
		session.step2(BigIntegerUtils.bigIntegerFromBytes(iresp.getSalt()),
				BigIntegerUtils.bigIntegerFromBytes(iresp.getNonce()));
		message = protocol
				.createAuthenticationRequest(BigIntegerUtils.bigIntegerToBytes(session.getClientEvidenceMessage()));
		message = host.execute(message);
		byte[] hostresp = protocol.processAuthenticationResponse(message);
		session.step3(BigIntegerUtils.bigIntegerFromBytes(hostresp));
	}

	/**
	 * Helper method for {@link #createClient(Identity, int, boolean)}
	 * 
	 * @param identity      Client's {@link Identity}
	 * @param authenticator The {@link Client} connection to authentication server
	 * @param timeout       Connection timeout during handshake
	 * @param secure        If true then SSL/TLS connection will be established
	 * @return A {@link WanhiveClient} connected to overlay server
	 * @throws ProtocolException
	 */
	private WanhiveClient bootstrap(Identity identity, Client authenticator, int timeout, boolean secure)
			throws ProtocolException {
		boolean connected = false;
		for (long node : bootNodes) {
			if (connected) { // Something bad happened
				break;
			}
			try (WanhiveClient client = new WanhiveClient(hosts.get(node), timeout, secure)) {
				connected = true;
				// -----------------------------------------------------------------
				/*
				 * Find the correct host
				 */
				long root = findRoot(client, identity);
				if (root != node) {
					client.connect(hosts.get(root), timeout, secure);
				}
				// -----------------------------------------------------------------
				/*
				 * Establish a unique session with the host
				 */
				byte[] sid = createSession(client);
				// -----------------------------------------------------------------
				/*
				 * Get the registration request signed by the authentication node
				 */
				authorize(client, authenticator, identity, sid);
				// -----------------------------------------------------------------
				client.setTimeout(0);
				return new WanhiveClient(client.release());
			} catch (Exception e) {

			}
		}

		throw new ProtocolException(BOOTSTRAP_FAIL);
	}

	/**
	 * Helper method for {@link #bootstrap(Identity, Client, int, boolean)}
	 * 
	 * @param host The {@link Client} connection to bootstrap server
	 * @param id   Client's {@link Identity}
	 * @return Identity of the overlay server
	 * @throws IOException
	 */
	private static long findRoot(Client host, Identity id) throws IOException {
		Protocol protocol = new Protocol();
		Message message = protocol.createFindRootRequest(id.getUid());
		message = host.execute(message);
		return protocol.processFindRootResponse(message);
	}

	/**
	 * Helper method for {@link #bootstrap(Identity, Client, int, boolean)}
	 * 
	 * @param host The {@link Client} connection to overlay server
	 * @return Session identifier
	 * @throws IOException
	 */
	private static byte[] createSession(Client host) throws IOException {
		Protocol protocol = new Protocol();
		Message message = protocol.createGetKeyRequest(null);
		message = host.execute(message);
		return protocol.processGetKeyResponse(message);
	}

	/**
	 * Helper method for {@link #bootstrap(Identity, Client, int, boolean)}
	 * 
	 * @param host The {@link Client} connection to overlay server
	 * @param auth The {@link Client} connection to authentication server
	 * @param id   Client's {@link Identity}
	 * @param sid  The session identifier
	 * @throws IOException
	 */
	private static void authorize(Client host, Client auth, Identity id, byte[] sid) throws IOException {
		Protocol protocol = new Protocol();
		Message message = protocol.createRegisterRequest(id.getUid(), sid);
		if (auth != null) {
			message = auth.execute(message);
		}
		// Complete the registration process
		message = host.execute(message);
		protocol.processRegisterResponse(message);
	}
}
