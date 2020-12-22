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
import com.nimbusds.srp6.SRP6CryptoParams;
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
	 * @param identity    Identity of the client
	 * @param hosts       Hosts database for the network address resolution
	 * @param authNodes   List of the stable authentication node IDs
	 * @param bootNodes   List of the stable bootstrap node IDs
	 * @param timeoutMils Socket read timeout (during handshaking)
	 * @param secure      If true then SSL/TLS connection will be established
	 * @return Client object which can be used for full-duplex messaging
	 * @throws ProtocolException Could not connect to the network
	 */
	public static Client createClient(Identity identity, Hosts hosts, long[] authNodes, long[] bootNodes,
			int timeoutMils, boolean secure) throws ProtocolException {
		try {
			boolean authNodeFound = false;
			boolean bootNodeFound = false;
			Message message = null;
			Protocol protocol = new Protocol();

			if (identity.getPassword() == null || identity.getPassword().length == 0) {
				for (long bootstrapNode : bootNodes) {
					if (bootNodeFound) { // Something bad happened during handshaking
						break;
					}
					try (WHClient client = new WHClient()) {
						client.connect(hosts, bootstrapNode, timeoutMils, secure);
						bootNodeFound = true;
						// -----------------------------------------------------------------
						/*
						 * Search for the host using a bootstrap node
						 */
						message = protocol.createFindRootRequest(identity.getUid());
						client.send(message);
						message = client.receive();
						long rootNode = protocol.processFindRootResponse(message);
						if (rootNode != bootstrapNode) {
							client.connect(hosts, rootNode, timeoutMils, secure);
						}
						// -----------------------------------------------------------------
						/*
						 * Create an unique session with the host
						 */
						message = protocol.createGetKeyRequest(null);
						client.send(message);
						message = client.receive();
						byte[] hc = protocol.processGetKeyResponse(message);
						// -----------------------------------------------------------------
						/*
						 * Get the registration request signed by the authentication node
						 */
						message = protocol.createRegisterRequest(identity.getUid(), hc);
						/*
						 * Complete registration
						 */
						client.send(message);
						message = client.receive();
						protocol.processRegisterResponse(message);
						// -----------------------------------------------------------------
						client.setTimeout(0);
						return new WHClient(client.release());
					} catch (Exception e) {

					}
				}
				throw new ProtocolException();
			}

			/*
			 * Default SRP-6a features for Wanhive
			 */
			SRP6CryptoParams config = WHSRP6ClientSession.getDefaultConfig();
			WHSRP6ClientSession session = WHSRP6ClientSession.getDefaultSession(identity.getRounds());

			for (long authNode : authNodes) {
				if (authNodeFound || bootNodeFound) { // Something bad happened deep down
					break;
				}
				try (WHClient auth = new WHClient()) {
					auth.connect(hosts, authNode, timeoutMils, secure);
					authNodeFound = true;
					// -----------------------------------------------------------------
					/*
					 * Identification
					 */
					session.step1(config, Long.toString(identity.getUid()), identity.getPassword());
					message = protocol.createIdentificationRequest(identity.getUid(),
							BigIntegerUtils.bigIntegerToBytes(session.getPublicClientValue()));
					auth.send(message);
					message = auth.receive();
					IdentificationResponse iresp = protocol.processIdentificationResponse(message);
					// -----------------------------------------------------------------
					/*
					 * Authentication
					 */
					session.step2(BigIntegerUtils.bigIntegerFromBytes(iresp.getSalt()),
							BigIntegerUtils.bigIntegerFromBytes(iresp.getNonce()));
					message = protocol.createAuthenticationRequest(
							BigIntegerUtils.bigIntegerToBytes(session.getClientEvidenceMessage()));
					auth.send(message);
					message = auth.receive();
					byte[] hostresp = protocol.processAuthenticationResponse(message);
					session.step3(BigIntegerUtils.bigIntegerFromBytes(hostresp));
					// -----------------------------------------------------------------
					for (long bootstrapNode : bootNodes) {
						if (bootNodeFound) { // Something bad happened during handshaking
							break;
						}
						try (WHClient client = new WHClient()) {
							client.connect(hosts, bootstrapNode, timeoutMils, secure);
							bootNodeFound = true;
							// -----------------------------------------------------------------
							/*
							 * Search for the host using a bootstrap node
							 */
							message = protocol.createFindRootRequest(identity.getUid());
							client.send(message);
							message = client.receive();
							long rootNode = protocol.processFindRootResponse(message);
							if (rootNode != bootstrapNode) {
								client.connect(hosts, rootNode, timeoutMils, secure);
							}
							// -----------------------------------------------------------------
							/*
							 * Create an unique session with the host
							 */
							message = protocol.createGetKeyRequest(null);
							client.send(message);
							message = client.receive();
							byte[] hc = protocol.processGetKeyResponse(message);
							// -----------------------------------------------------------------
							/*
							 * Get the registration request signed by the authentication node
							 */
							message = protocol.createRegisterRequest(identity.getUid(), hc);
							auth.send(message);
							message = auth.receive();
							/*
							 * Complete registration
							 */
							client.send(message);
							message = client.receive();
							protocol.processRegisterResponse(message);
							// -----------------------------------------------------------------
							client.setTimeout(0);
							return new WHClient(client.release());
						} catch (Exception e) {

						}
					}

				} catch (Exception e) {

				}
			}

		} catch (Exception e) {

		}

		throw new ProtocolException();
	}
}
