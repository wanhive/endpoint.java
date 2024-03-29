/*
 * Receiver.java
 * 
 * The incoming message processor interface
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
package com.wanhive.iot.edge;

import com.wanhive.iot.protocol.Message;

/**
 * The incoming message processor interface
 * 
 * @author amit
 *
 */
public interface Receiver {
	/**
	 * Processes an incoming message
	 * 
	 * @param message The {@link Message} to process
	 */
	public void receive(Message message);
}
