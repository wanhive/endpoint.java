/*
 * ProfileMap.java
 * 
 * Endpoint profiles manager
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanhive.iot.protocol.configuration.ObjectSerializer;

/**
 * Endpoint profiles manager
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
	 * @param pathname Pathname of the file containing the endpoint profiles. If the
	 *                 file doesn't exist then an empty ProfileMap is created.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 */
	public ProfileMap(String pathname) throws FileNotFoundException, ClassNotFoundException, IOException {
		this.filename = pathname;
		File file = new File(this.filename);
		if (file.exists()) {
			load();
		}
	}

	/**
	 * Reads the endpoint profiles from the file system.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public void load() throws FileNotFoundException, ClassNotFoundException, IOException {
		ConcurrentHashMap<Long, Profile> newProfiles = (ConcurrentHashMap<Long, Profile>) ObjectSerializer
				.load(filename);
		profiles.clear();
		for (Map.Entry<Long, Profile> entry : newProfiles.entrySet()) {
			profiles.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Returns a Map containing the endpoint profiles
	 * 
	 * @return The profiles Map
	 */
	public Map<Long, Profile> get() {
		return profiles;
	}

	/**
	 * Writes the endpoint profiles to the file system
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void store() throws FileNotFoundException, IOException {
		ObjectSerializer.store(filename, profiles);
	}
}
