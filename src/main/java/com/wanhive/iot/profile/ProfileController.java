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
	 * Returns name of the controller
	 * 
	 * @return name of the controller
	 */
	String getName();

	/**
	 * Returns brief description of the controller
	 * 
	 * @return Brief description of the controller
	 */
	String getDescription();

	/**
	 * Creates and executes an end point associated with the given profile
	 * 
	 * @param profile Profile of the end point
	 * @throws Exception
	 */
	void start(Profile profile) throws Exception;

	/**
	 * Stops execution of the end point associated with the given profile
	 * 
	 * @param profile Profile of the end point
	 * @throws Exception
	 */
	void stop(Profile profile) throws Exception;

	/**
	 * Checks whether an end point with given profile is in-use
	 * 
	 * @param profile End point profile
	 * @return Returns true if an end point associated with the profile is running,
	 *         false otherwise.
	 */
	boolean isActive(Profile profile);

	/**
	 * Returns true if there exists an active profile in the program
	 * 
	 * @return true if an end point is running, otherwise false
	 */
	boolean hasActive();
}
