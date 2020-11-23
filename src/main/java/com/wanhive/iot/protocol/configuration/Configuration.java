/*
 * Configuration.java
 * 
 * INI configuration file handler
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

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * INI configuration file handler
 * 
 * @author amit
 *
 */
public class Configuration {
	/**
	 * Parses and returns an ini configuration object
	 * 
	 * @param pathName      pathname of the configuration file
	 * @param listDelimiter the list delimiter character
	 * @return INIConfiguration object containing the configuration data
	 * @throws ConfigurationException
	 */
	public static INIConfiguration get(String pathName, char listDelimiter) throws ConfigurationException {
		FileBasedConfigurationBuilder<INIConfiguration> builder = new FileBasedConfigurationBuilder<INIConfiguration>(
				INIConfiguration.class)
						.configure(new Parameters().properties().setFileName(pathName).setThrowExceptionOnMissing(true)
								.setListDelimiterHandler(new DefaultListDelimiterHandler(listDelimiter)));
		INIConfiguration config = builder.getConfiguration();
		return config;
	}
}
