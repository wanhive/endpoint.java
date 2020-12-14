/*
 * ProfileController.java
 * 
 * Runtime profile controller
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

/**
 * Loads an endpoint profile and executes it
 * 
 * @author amit
 *
 */
public interface ProfileController {
	/**
	 * Returns the name
	 * 
	 * @return the Name of the controller
	 */
	String getName();

	/**
	 * Returns a brief description of the controller
	 * 
	 * @return A brief description of the controller
	 */
	String getDescription();

	/**
	 * Creates and executes the endpoint associated with the given profile
	 * 
	 * @param profile The endpoint's profile
	 */
	void start(Profile profile);

	/**
	 * Stops the execution of the endpoint associated with the given profile
	 * 
	 * @param profile The endpoint's profile
	 */
	void stop(Profile profile);

	/**
	 * Checks whether an endpoint with given profile is running
	 * 
	 * @param profile The endpoint's profile
	 * @return Returns true if an endpoint associated with the profile is running,
	 *         false otherwise.
	 */
	boolean isActive(Profile profile);

	/**
	 * Returns true if there exists an active profile
	 * 
	 * @return true if an endpoint is running, false otherwise
	 */
	boolean hasActive();
}
