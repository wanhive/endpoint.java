/*
 * Profile.java
 * 
 * Endpoint profile
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
package com.wanhive.iot.profile;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import com.wanhive.iot.protocol.bean.Identity;

/**
 * Serializable endpoint profile
 * 
 * @author amit
 *
 */
public class Profile extends Identity {
	/**
	 * The version number
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Remote endpoint identifier
	 */
	private final AtomicLong id = new AtomicLong();
	/**
	 * Name of this profile
	 */
	private String name;
	/**
	 * Generic reference
	 */
	private Serializable reference;

	/**
	 * Returns this profile's remote endpoint identifier
	 * 
	 * @return The remote endpoint identifier
	 */
	public long getId() {
		return id.get();
	}

	/**
	 * Sets this profile's remote endpoint identifier
	 * 
	 * @param id Remote endpoint's identifier
	 */
	public void setId(long id) {
		this.id.set(id);
	}

	/**
	 * Returns this profile's name
	 * 
	 * @return This profile's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets this profile's name
	 * 
	 * @param name The desired name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the serializable object attached to this profile
	 * 
	 * @return This profile's attachment
	 */
	public Serializable getReference() {
		return reference;
	}

	/**
	 * Attaches a serializable object to this profile
	 * 
	 * @param reference The serializable attachment for this profile
	 */
	public void setReference(Serializable reference) {
		this.reference = reference;
	}
}
