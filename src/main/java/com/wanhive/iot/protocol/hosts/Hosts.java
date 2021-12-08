/*
 * Hosts.java
 * 
 * Hosts management interface
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
 * Hosts management interface
 * 
 * @author amit
 *
 */
public interface Hosts extends AutoCloseable {
	/**
	 * Returns the network address of a Wanhive host
	 * 
	 * @param identity Identity of the host
	 * @return A {@link NameInfo} object
	 */
	public NameInfo get(long identity);

	/**
	 * Stores the network address of a Wanhive host
	 * 
	 * @param identity Host's identity
	 * @param ni       A {@link NameInfo} object
	 */
	public void put(long identity, NameInfo ni);

	/**
	 * Removes a host from the record
	 * 
	 * @param identity Identity of the host to remove
	 */
	public void remove(long identity);

	/**
	 * Returns a list of host identities of the given type
	 * 
	 * @param type  The host's type
	 * @param limit The maximum number of identifiers to return
	 * @return An array containing the host identities
	 */
	public long[] list(int type, int limit);
}
