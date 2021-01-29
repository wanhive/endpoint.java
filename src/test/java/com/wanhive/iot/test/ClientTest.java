/*
 * ClientTest.java
 * 
 * Testing Client application
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
package com.wanhive.iot.test;

import java.nio.charset.Charset;

import org.apache.commons.configuration2.INIConfiguration;

import com.wanhive.iot.edge.Executor;
import com.wanhive.iot.protocol.Client;
import com.wanhive.iot.protocol.ClientFactory;
import com.wanhive.iot.protocol.Message;
import com.wanhive.iot.protocol.Protocol;
import com.wanhive.iot.protocol.bean.Identity;
import com.wanhive.iot.protocol.configuration.Configuration;
import com.wanhive.iot.protocol.hosts.HostsCache;
import com.wanhive.iot.protocol.hosts.WanhiveHosts;
import com.wanhive.iot.protocol.hosts.WanhiveHostsCache;

/**
 * Client application test
 * 
 * @author amit
 *
 */
public class ClientTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			/*
			 * Load configuration file from disk
			 */
			INIConfiguration config = Configuration.get(args[0], ',');

			/*
			 * Configure SSL
			 */
			boolean enableSSL = config.getSection("SSL").getBoolean("enable", false);
			if (enableSSL) {
				ClientFactory.setTrustStore(config.getSection("SSL").getString("trust", null),
						config.getSection("SSL").getString("password", null));
			}

			/*
			 * Create a new client
			 */
			boolean sslEnabled = config.getSection("SSL").getBoolean("enable", false);
			if (sslEnabled) {
				ClientFactory.setTrustStore(config.getSection("SSL").getString("trust", null),
						config.getSection("SSL").getString("password", null));
			}

			Identity id = new Identity(65537,
					config.getSection("CLIENT").getString("password", "").getBytes(Charset.forName("UTF-8")),
					config.getSection("CLIENT").getInt("passwordHashRounds", 1));

			WanhiveHosts hosts = null;
			String hostsDb = config.getSection("HOSTS").getString("hostsDb", null);
			if (hostsDb != null) {
				hosts = new WanhiveHosts(hostsDb);
			} else {
				String hostsFile = config.getSection("HOSTS").getString("hostsFile", null);
				hosts = new WanhiveHosts(WanhiveHosts.IN_MEMORY);
				hosts.importHosts(hostsFile);
			}

			HostsCache cache = new WanhiveHostsCache();
			long[] auths = cache.get(config.getSection("BOOTSTRAP").getString("auths"), 16);
			long[] boots = cache.get(config.getSection("BOOTSTRAP").getString("nodes"), 16);
			int timeout = config.getSection("CLIENT").getInt("timeOut", 0);

			Client client = ClientFactory.createClient(id, hosts, auths, boots, timeout, sslEnabled);
			System.out.println("CONNECTED");

			/*
			 * Execute the client
			 */
			int queueCapacity = config.getSection("HUB").getInt("messagePoolSize", 1024);
			Executor exec = new Executor(client, queueCapacity, queueCapacity);
			Thread th = new Thread(exec);
			th.start();
			Protocol proto = new Protocol();
			for (int i = 0; i < 5; i++) {
				Message msg = proto.createPublishRequest((byte) 5, "HelloWorld".getBytes());
				exec.offer(msg);
				Thread.sleep(5000, 0);
			}
			exec.close();
			th.join();
			System.out.println("END");
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
