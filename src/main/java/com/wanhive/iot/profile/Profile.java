/*
 * Profile.java
 * 
 * Serializable endpoint profile
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
	 * This profile's name
	 */
	private String name;
	/**
	 * Generic attachment
	 */
	private Serializable attachment;

	/**
	 * Returns the remote endpoint identifier
	 * 
	 * @return The remote endpoint identifier
	 */
	public long getId() {
		return id.get();
	}

	/**
	 * Sets the remote endpoint identifier
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
	 * Returns the attached object
	 * 
	 * @return The attached {@link Serializable} object
	 */
	public Serializable getAttachment() {
		return attachment;
	}

	/**
	 * Attaches a serializable object
	 * 
	 * @param attachment The {@link Serializable} attachment
	 */
	public void setAttachment(Serializable attachment) {
		this.attachment = attachment;
	}
}
