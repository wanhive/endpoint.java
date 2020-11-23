/*
 * ObjectSerializer.java
 * 
 * Serialization and deserialization of java objects
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
package com.wanhive.iot.protocol.configuration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Utility class for serialization and deserialization of objects
 * 
 * @author amit
 *
 */
public class ObjectSerializer {
	/**
	 * Writes a serializable object to the file system
	 * 
	 * @param pathName pathname of the file where the object will be stored
	 * @param object   the serializable object
	 * @throws Exception
	 */
	public static void store(String pathName, Serializable object) throws Exception {
		try (FileOutputStream file = new FileOutputStream(pathName)) {
			try (ObjectOutputStream out = new ObjectOutputStream(file)) {
				out.writeObject(object);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Reads a serializable object from the file system
	 * 
	 * @param pathName pathname of the file containing the serialized object
	 * @return the object which was stored in the given file
	 * @throws Exception
	 */
	public static Object load(String pathName) throws Exception {
		try (FileInputStream file = new FileInputStream(pathName)) {
			try (ObjectInputStream in = new ObjectInputStream(file)) {
				return in.readObject();
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
