/*
 * Hosts.java
 * 
 * Hosts management
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

package com.wanhive.iot.protocol.hosts;

import com.wanhive.iot.protocol.bean.NameInfo;

/**
 * Wanhive's hosts manager interface
 * 
 * @author amit
 *
 */
public interface Hosts extends AutoCloseable {
	/**
	 * Returns the network address of a Wanhive host
	 * 
	 * @param identity Identity of the host
	 * @return A NameInfo object containing the host's network address
	 */
	public NameInfo get(long identity);

	/**
	 * Stores the network address of a Wanhive host
	 * 
	 * @param identity Host's identity
	 * @param ni       A NameInfo object containing the host's network address
	 */
	public void put(long identity, NameInfo ni);

	/**
	 * Removes a Wanhive host from the record
	 * 
	 * @param identity The identity of the host to remove
	 */
	public void remove(long identity);

	/**
	 * Returns list of host identifiers of the given type
	 * 
	 * @param type  The host's type
	 * @param limit The maximum number of identifiers to return
	 * @return An array containing the host identifiers
	 */
	public long[] list(int type, int limit);
}
