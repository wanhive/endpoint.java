/*
 * ProfileMap.java
 * 
 * Record keeper of the endpoint profiles
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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanhive.iot.protocol.configuration.ObjectSerializer;

/**
 * Record keeper of the endpoint profiles
 * 
 * @author amit
 *
 */
public class ProfileMap {
	private final String filename;
	private final ConcurrentHashMap<Long, Profile> profiles = new ConcurrentHashMap<Long, Profile>();

	/**
	 * Constructor
	 * 
	 * @param filename pathname of the file containing the profiles
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public ProfileMap(String filename) throws ClassNotFoundException, IOException {
		this.filename = filename;
		reload();
	}

	/**
	 * Reads the profiles from the file system
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public void reload() throws ClassNotFoundException, IOException {
		ConcurrentHashMap<Long, Profile> newProfiles = (ConcurrentHashMap<Long, Profile>) ObjectSerializer
				.load(filename);
		profiles.clear();
		for (Map.Entry<Long, Profile> entry : newProfiles.entrySet()) {
			profiles.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Returns the map containing all the profiles
	 * 
	 * @return A map of the profiles
	 */
	public Map<Long, Profile> get() {
		return profiles;
	}

	/**
	 * Commits the profiles to the disk
	 * 
	 * @throws IOException
	 * 
	 */
	public void commit() throws IOException {
		ObjectSerializer.store(filename, profiles);
	}
}
