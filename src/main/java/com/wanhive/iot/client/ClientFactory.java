/*
 * ClientFactory.java
 * 
 * Creates a Wanhive client
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
package com.wanhive.iot.client;

import java.net.ProtocolException;

import com.nimbusds.srp6.BigIntegerUtils;
import com.wanhive.iot.protocol.Message;
import com.wanhive.iot.protocol.Protocol;
import com.wanhive.iot.protocol.agreement.WHSRP6ClientSession;
import com.wanhive.iot.protocol.bean.IdentificationResponse;
import com.wanhive.iot.protocol.bean.Identity;
import com.wanhive.iot.protocol.hosts.Hosts;

/**
 * Creates a Wanhive client
 * 
 * @author amit
 *
 */
public class ClientFactory {
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
	 * Connects with the Wanhive network
	 * 
	 * @param identity  Identity of the client
	 * @param hosts     Hosts database for the network address resolution
	 * @param authNodes List of the stable authentication node IDs
	 * @param bootNodes List of the stable bootstrap node IDs
	 * @param timeout   Socket read timeout in milliseconds (during handshaking)
	 * @param secure    If true then SSL/TLS connection will be established
	 * @return Client object which can be used for full-duplex messaging
	 * @throws ProtocolException Could not connect to the network
	 */
	public static Client createClient(Identity identity, Hosts hosts, long[] authNodes, long[] bootNodes, int timeout,
			boolean secure) throws ProtocolException {
		try (Client auth = authenticate(identity, hosts, authNodes, timeout, secure)) {
			return bootstrap(identity, hosts, auth, bootNodes, timeout, secure);
		} catch (ProtocolException e) {
			throw e;
		} catch (Exception e) {
			throw new ProtocolException();
		}
	}

	private static Client authenticate(Identity identity, Hosts hosts, long[] nodes, int timeout, boolean secure)
			throws ProtocolException {
		if (identity.getPassword() == null || identity.getPassword().length == 0) {
			return null;
		}

		Protocol protocol = new Protocol();
		boolean connected = false;
		for (long node : nodes) {
			if (connected) { // Something bad happened
				break;
			}
			try (WHClient auth = new WHClient(hosts.get(node), timeout, secure)) {
				connected = true;
				// -----------------------------------------------------------------
				/*
				 * Identification
				 */
				WHSRP6ClientSession session = WHSRP6ClientSession.getDefaultSession(identity.getRounds());
				session.step1(WHSRP6ClientSession.getDefaultConfig(), Long.toString(identity.getUid()),
						identity.getPassword());
				Message message = protocol.createIdentificationRequest(identity.getUid(),
						BigIntegerUtils.bigIntegerToBytes(session.getPublicClientValue()));
				message = auth.execute(message);
				IdentificationResponse iresp = protocol.processIdentificationResponse(message);
				// -----------------------------------------------------------------
				/*
				 * Authentication
				 */
				session.step2(BigIntegerUtils.bigIntegerFromBytes(iresp.getSalt()),
						BigIntegerUtils.bigIntegerFromBytes(iresp.getNonce()));
				message = protocol.createAuthenticationRequest(
						BigIntegerUtils.bigIntegerToBytes(session.getClientEvidenceMessage()));
				message = auth.execute(message);
				byte[] hostresp = protocol.processAuthenticationResponse(message);
				session.step3(BigIntegerUtils.bigIntegerFromBytes(hostresp));
				return new WHClient(auth.release());
			} catch (Exception e) {

			}
		}

		throw new ProtocolException();
	}

	private static Client bootstrap(Identity identity, Hosts hosts, Client auth, long[] nodes, int timeout,
			boolean secure) throws ProtocolException {
		Protocol protocol = new Protocol();
		boolean connected = false;

		for (long node : nodes) {
			if (connected) { // Something bad happened
				break;
			}
			try (WHClient client = new WHClient(hosts.get(node), timeout, secure)) {
				connected = true;
				// -----------------------------------------------------------------
				/*
				 * Search for the host
				 */
				Message message = protocol.createFindRootRequest(identity.getUid());
				message = client.execute(message);
				long root = protocol.processFindRootResponse(message);
				if (root != node) {
					client.connect(hosts.get(root), timeout, secure);
				}
				// -----------------------------------------------------------------
				/*
				 * Establish a unique session with the host
				 */
				message = protocol.createGetKeyRequest(null);
				message = client.execute(message);
				byte[] hc = protocol.processGetKeyResponse(message);
				// -----------------------------------------------------------------
				/*
				 * Get the registration request signed by the authentication node
				 */
				message = protocol.createRegisterRequest(identity.getUid(), hc);
				if (auth != null) {
					message = auth.execute(message);
				}
				/*
				 * Complete registration
				 */
				message = client.execute(message);
				protocol.processRegisterResponse(message);
				// -----------------------------------------------------------------
				client.setTimeout(0);
				return new WHClient(client.release());
			} catch (Exception e) {

			}
		}

		throw new ProtocolException();
	}
}
