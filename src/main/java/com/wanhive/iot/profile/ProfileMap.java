/*
 * ProfileMap.java
 * 
 * Record keeper of endpoint profiles
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.wanhive.iot.protocol.configuration.ObjectSerializer;

/**
 * Reads and writes endpoint profiles from-and-to the filesystem
 * 
 * @author amit
 *
 */
public class ProfileMap {
	private final String filename;
	private final ConcurrentHashMap<Long, Profile> profiles = new ConcurrentHashMap<Long, Profile>();

	/**
	 * Loads all the profiles from the given file
	 * 
	 * @param filename pathname of the file containing the profiles
	 * @throws Exception
	 */
	public ProfileMap(String filename) throws Exception {
		if (filename == null) {
			throw new NullPointerException();
		} else {
			this.filename = filename;
			reload();
		}
	}

	/**
	 * Reloads the profiles from the disk
	 */
	@SuppressWarnings("unchecked")
	public void reload() {
		try {
			ConcurrentHashMap<Long, Profile> newProfiles = (ConcurrentHashMap<Long, Profile>) ObjectSerializer
					.load(filename);
			profiles.clear();
			for (Map.Entry<Long, Profile> entry : newProfiles.entrySet()) {
				profiles.put(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			Logger.getGlobal().warning(e.getMessage());
		}
	}

	/**
	 * Returns the map containing all the profiles
	 * 
	 * @return
	 */
	public Map<Long, Profile> get() {
		return profiles;
	}

	/**
	 * Commits the profiles to the disk
	 * 
	 * @throws Exception
	 */
	public void commit() throws Exception {
		ObjectSerializer.store(filename, profiles);
	}
}
