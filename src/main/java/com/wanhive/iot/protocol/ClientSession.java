/*
 * ClientSession.java
 * 
 * Management of endpoint pairs
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

/**
 * Endpoint pair management
 * 
 * @author amit
 *
 */
public interface ClientSession {
	/**
	 * Sets the local identity
	 * 
	 * @param identity Unique identity of the local end point
	 */
	void setLocalIdentity(long identity);

	/**
	 * Returns the local end point's identity
	 * 
	 * @return Local end point's identity
	 */
	long getLocalIdentity();

	/**
	 * Sets identity of the remote end point
	 * 
	 * @param identity Unique identity of the remote end point
	 */
	void setRemoteIdentity(long identity);

	/**
	 * Returns remote end point's identity
	 * 
	 * @return Remote end point's identity
	 */
	long getRemoteIdentity();

	/**
	 * Associates the given value with the given key, replaces on conflict
	 * 
	 * @param key   Key with which the given value will be associated
	 * @param value Value which will be associated with the given key
	 */
	void setProperty(Object key, Object value);

	/**
	 * Returns the value associated with the given key, null if not found
	 * 
	 * @param key The key whose association will be returned
	 * @return The value associated with the given key
	 */
	Object getProperty(Object key);
}
