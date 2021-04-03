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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Serializable object handler
 * 
 * @author amit
 *
 */
public class ObjectSerializer {
	/**
	 * Writes a serializable object to the file system
	 * 
	 * @param pathname The pathname of the file where the object will be stored
	 * @param object   The serializable object
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void store(String pathname, Serializable object) throws FileNotFoundException, IOException {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathname))) {
			out.writeObject(object);
		}
	}

	/**
	 * Writes a serializable object to the file system
	 * 
	 * @param file   The file where the object will be stored
	 * @param object The serializable object
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void store(File file, Serializable object) throws FileNotFoundException, IOException {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
			out.writeObject(object);
		}
	}

	/**
	 * 
	 * Reads a serializable object from the file system
	 * 
	 * @param pathname The pathname of the file containing the serialized object
	 * @return The object read from the given file
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 */
	public static Object load(String pathname) throws FileNotFoundException, IOException, ClassNotFoundException {
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(pathname))) {
			return in.readObject();
		}
	}

	/**
	 * Reads a serializable object from the file system
	 * 
	 * @param file The file from where the object will be read
	 * @return The object read from the given file
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 */
	public static Object load(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
			return in.readObject();
		}
	}
}
