/*
 * HostsCache.java
 * 
 * Hosts cache interface
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

/**
 * Cache of wanhive host identifiers
 * 
 * @author amit
 *
 */
public interface HostsCache {
	/**
	 * Reads a list of host identifiers from a file
	 * 
	 * @param path  The pathname of the file
	 * @param count The maximum number of identifiers to read from the file
	 * @return A long array containing the identifiers
	 * @throws Exception
	 */
	public long[] get(String path, int count) throws Exception;
}
