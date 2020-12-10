/*
 * NameInfo.java
 * 
 * Wanhive host address
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

/**
 * Network address structure of a wanhive host
 * 
 * @author amit
 *
 */
public class NameInfo {
	/**
	 * The host name (most likely an IP adress)
	 */
	private String host;
	/**
	 * The service name (most likely a PORT identifier)
	 */
	private String service;
	/**
	 * The host type
	 */
	private int type;

	/**
	 * Get the host name
	 * 
	 * @return The string containing the host name
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Set the host name
	 * 
	 * @param host The string containing the host name
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Get the service type
	 * 
	 * @return The string containing the service type
	 */
	public String getService() {
		return service;
	}

	/**
	 * Set the service type
	 * 
	 * @param service The string containing the service type
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * Get the host type
	 * 
	 * @return The host type identifier
	 */
	public int getType() {
		return type;
	}

	/**
	 * Set the host type
	 * 
	 * @param type The host type identifier
	 */
	public void setType(int type) {
		this.type = type;
	}
}
